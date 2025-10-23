package com.itb.postapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import com.itb.postapp.data.model.PostEntity

@Database(
    entities = [PostEntity::class],
    version = 1,
    exportSchema = false
) abstract class PostsDatabase : RoomDatabase() {

    abstract fun postsDao(): PostsDao

}