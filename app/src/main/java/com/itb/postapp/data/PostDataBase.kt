package com.itb.postapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import com.itb.postapp.data.model.Post

@Database(
    entities = [Post::class],
    version = 1,
    exportSchema = false
) abstract class PostsDatabase : RoomDatabase() {

    abstract fun postsDao(): PostsDao

    companion object {

        const val DATABASE_NAME = "posts_db"

        @Volatile
        private var INSTANCE: PostsDatabase? = null

        fun getInstance(context: Context): PostsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    PostsDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}