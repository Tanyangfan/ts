package mushi.taibai.data.source

import mushi.taibai.data.Poem

/**
 * Main entry point for accessing poem data
 * Created by Tan.Yangfan on 2018/2/8.
 */
interface PoemsDataSource {

    interface LoadPoemsCallback {

        fun onPoemsLoaded(poems: List<Poem>)

        fun onDataNotAvailable()
    }

    interface GetPoemCallback {

        fun onPoemLoaded(poem: Poem?)

        fun onDataNotAvailable()
    }

    /**
     * Get poems
     */
    fun getPoems(callback: LoadPoemsCallback)

    /**
     * Get poem by id
     */
    fun getPoem(id: String, callback: GetPoemCallback)

    fun savePoem(poem: Poem?)

    fun refreshPoems()

    fun deleteAllPoems()
}