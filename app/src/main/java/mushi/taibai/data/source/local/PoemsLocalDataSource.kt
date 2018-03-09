package mushi.taibai.data.source.local

import mushi.taibai.data.Poem
import mushi.taibai.data.source.PoemsDataSource
import mushi.taibai.util.AppExecutors

/**
 * Concrete implementation of a data source as a db.
 * Created by Tan.Yangfan on 2018/2/25.
 */
class PoemsLocalDataSource(private val mAppExecutors: AppExecutors,
                           private val mPoemsDao: PoemsDao) : PoemsDataSource {

    companion object {
        @Volatile
        private var INSTANCE: PoemsLocalDataSource? = null

        fun getInstance(appExecutors: AppExecutors, poemsDao: PoemsDao): PoemsLocalDataSource {
            INSTANCE?.let {
                synchronized(PoemsLocalDataSource::class) {
                    INSTANCE = PoemsLocalDataSource(appExecutors, poemsDao)
                }
            }

            return INSTANCE!!
        }

        fun clearInstance() {
            INSTANCE = null
        }
    }

    override fun getPoems(callback: PoemsDataSource.LoadPoemsCallback) {
        val runnable = Runnable {
            val poems = mPoemsDao.getPoems()

            mAppExecutors.mainThread().execute({
                if (poems.isEmpty())
                    callback.onDataNotAvailable()
                else
                    callback.onPoemsLoaded(poems)
            })
        }

        mAppExecutors.diskIO().execute(runnable)
    }

    override fun getPoem(id: String, callback: PoemsDataSource.GetPoemCallback) {
        val runnable = Runnable {
            val poem: Poem? = mPoemsDao.getPoemById(id.toInt())

            mAppExecutors.mainThread().execute({
                if (poem == null)
                    callback.onDataNotAvailable()
                else
                    callback.onPoemLoaded(poem)
            })
        }

        mAppExecutors.diskIO().execute(runnable)
    }

    override fun savePoem(poem: Poem?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun refreshPoems() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteAllPoems() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
