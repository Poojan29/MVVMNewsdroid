package com.example.basedroid.db

import android.content.Context
import androidx.room.*
import com.example.basedroid.model.Article

@Database(
    entities = [Article::class],
    version = 1
)
@TypeConverters(
    Converters::class
)
abstract class ArticleDatabase : RoomDatabase(){

    abstract fun getArticleDao(): ArticleDao

    companion object {
        //meaning that writes to this field are immediately made visible to other threads.
        @Volatile
        private var INSTANCE: ArticleDatabase? = null

        fun getArticleDatabase(context: Context): ArticleDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ArticleDatabase::class.java,
                    "article_database.db"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }

}
