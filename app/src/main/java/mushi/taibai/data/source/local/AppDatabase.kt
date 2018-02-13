package mushi.taibai.data.source.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import mushi.taibai.data.Poem

/**
 * The Room Database that contains poem
 * Created by Tan.Yangfan on 2018/2/8.
 */
@Database(entities = [Poem::class], version = 1)
abstract class AppDatabase private constructor(context: Context) : RoomDatabase() {

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            INSTANCE?.let {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            AppDatabase::class.java, "tangshi.db")
                            .build()
                }
            }
            return INSTANCE!!
        }
    }

    /**
     * poem操作dao
     */
    abstract fun poemDao(): PoemDao
}