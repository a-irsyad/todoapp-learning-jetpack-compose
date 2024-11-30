package com.onehertz.todo.source.local

import androidx.room.Database
import androidx.room.RoomDatabase


// TODO: testing this database

@Database(
    entities = [LocalTask::class],
    version = 1,
    exportSchema = false
)
abstract class ToDoDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}