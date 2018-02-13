package mushi.taibai.data.source

import mushi.taibai.data.Poem

/**
 * Concrete implementation to load poem from the data sources into a cache.
 * Created by Tan.Yangfan on 2018/2/8.
 */

class PoemsRepository(private val mPoemsRemoteDataSource: PoemsDataSource,
                      private val mPoemsLocalDataSource: PoemsDataSource) : PoemsDataSource {

    companion object {
        private var INSTANCE: PoemsRepository? = null

        fun getInstance(poemsRemoteDataSource: PoemsDataSource, poemsLocalDataSource: PoemsDataSource): PoemsRepository {
            INSTANCE?.let {
                synchronized(PoemsRepository::class) {
                    INSTANCE = PoemsRepository(poemsRemoteDataSource, poemsLocalDataSource)
                }
            }
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }

    /**
     * This variable has package local visibility so it can be accessed from tests.
     */
    var mCachedPoems: MutableMap<String, Poem>? = null

    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    private var mCacheIsDirty: Boolean = false

    override fun getPoems(callback: PoemsDataSource.LoadPoemsCallback) {

        // Respond immediately with cache if available and not dirty
        mCachedPoems?.takeIf { !mCacheIsDirty }?.run {
            callback.onPoemsLoaded(ArrayList(mCachedPoems?.values))
        }

        if (mCacheIsDirty) {
            // If the cache is dirty we need to fetch new data from the network.
            getPoemsFromRemoteDataSource(callback)
        } else {
            mPoemsLocalDataSource.getPoems(object : PoemsDataSource.LoadPoemsCallback {
                override fun onPoemsLoaded(poems: List<Poem>) {
                    refreshCache(poems)

                    callback.onPoemsLoaded(ArrayList(mCachedPoems?.values))
                }

                override fun onDataNotAvailable() {
                    getPoemsFromRemoteDataSource(callback)
                }
            })
        }
    }

    override fun getPoem(id: String, callback: PoemsDataSource.GetPoemCallback) {
        val cachePoem = getPoemFromCache(id)
        cachePoem?.apply {
            callback.onPoemLoaded(this)
        }

        mPoemsLocalDataSource.getPoem(id, object : PoemsDataSource.GetPoemCallback {
            override fun onPoemLoaded(poem: Poem?) {
                mCachedPoems ?: run {
                    mCachedPoems = LinkedHashMap()
                }
                mCachedPoems?.put(poem?.id.toString(), poem!!)

                callback.onPoemLoaded(poem)
            }

            override fun onDataNotAvailable() {
                mPoemsRemoteDataSource.getPoem(id, object : PoemsDataSource.GetPoemCallback {
                    override fun onPoemLoaded(poem: Poem?) {
                        poem ?: kotlin.run {
                            onDataNotAvailable()
                            return
                        }

                        mCachedPoems ?: run {
                            mCachedPoems = LinkedHashMap()
                        }
                        mCachedPoems?.put(poem.id.toString(), poem)

                        callback.onPoemLoaded(poem)
                    }

                    override fun onDataNotAvailable() {
                        callback.onDataNotAvailable()
                    }
                })
            }
        })
    }

    override fun savePoem(poem: Poem?) {
        mPoemsRemoteDataSource.savePoem(poem)
        mPoemsLocalDataSource.savePoem(poem)

        mCachedPoems ?: kotlin.run {
            mCachedPoems = LinkedHashMap()
        }
        mCachedPoems?.put(poem?.id.toString(), poem!!)
    }

    override fun refreshPoems() {
        mCacheIsDirty = true
    }

    override fun deleteAllPoems() {
        mPoemsRemoteDataSource.deleteAllPoems()
        mPoemsLocalDataSource.deleteAllPoems()

        mCachedPoems ?: kotlin.run {
            mCachedPoems = LinkedHashMap()
        }
        mCachedPoems?.clear()
    }

    private fun getPoemsFromRemoteDataSource(callback: PoemsDataSource.LoadPoemsCallback) {
        mPoemsRemoteDataSource.getPoems(object : PoemsDataSource.LoadPoemsCallback {
            override fun onPoemsLoaded(poems: List<Poem>) {
                refreshCache(poems)
                refreshLocalDataSource(poems)

                callback.onPoemsLoaded(ArrayList(mCachedPoems?.values))
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }
        })
    }

    private fun refreshCache(poems: List<Poem>) {
        mCachedPoems ?: run {
            mCachedPoems = LinkedHashMap()
        }
        mCachedPoems?.clear()
        for (poem: Poem in poems) {
            mCachedPoems?.put(poem.id.toString(), poem)
        }
        mCacheIsDirty = false
    }

    private fun refreshLocalDataSource(poems: List<Poem>) {
        mPoemsLocalDataSource.deleteAllPoems()
        for (poem: Poem in poems) {
            mPoemsLocalDataSource.savePoem(poem)
        }
    }

    private fun getPoemFromCache(id: String): Poem? {
        return mCachedPoems?.takeIf { it.isNotEmpty() }?.let { it[id] }
    }
}