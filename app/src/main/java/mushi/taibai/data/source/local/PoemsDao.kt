package mushi.taibai.data.source.local

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import mushi.taibai.data.Poem

/**
 * Data Access Object for the poem table.
 * Created by Tan.Yangfan on 2018/2/8.
 */
@Dao
interface PoemsDao {

    @Query("SELECT * FROM poem")
    fun getPoems(): List<Poem>

    /**
     * Select a poem by id
     * @param id the poem id
     * @return the poem with id
     */
    @Query("SELECT * FROM poem WHERE id = :id")
    fun getPoemById(id: Int): Poem

//    /**
//     * Insert a poem in the database. If the poem already exists, replace it.
//     *
//     * @param poem the poem to be inserted.
//     */
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertPoem(poem: Poem)
}
