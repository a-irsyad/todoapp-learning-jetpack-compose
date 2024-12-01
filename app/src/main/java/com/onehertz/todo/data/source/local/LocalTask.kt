package com.onehertz.todo.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task")
data class LocalTask(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val isCompleted: Boolean
)