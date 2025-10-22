package com.itb.postapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String
)

