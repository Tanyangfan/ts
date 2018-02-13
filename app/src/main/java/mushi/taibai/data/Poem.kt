package mushi.taibai.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * 唐诗表
 * id自增
 * Created by Tan.Yangfan on 2018/2/8.
 */
@Entity(tableName = "poem")
data class Poem(
        @ColumnInfo(name = "id")
        @PrimaryKey(autoGenerate = true)
        val id: Int,
        @ColumnInfo(name = "title")
        val title: String,
        @ColumnInfo(name = "author")
        val author: String,
        @ColumnInfo(name = "txt")
        val text: String
)