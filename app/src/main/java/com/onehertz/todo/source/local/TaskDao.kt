package com.onehertz.todo.source.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM task")
    fun observeAllTasks(): Flow<List<LocalTask>>

    @Query("SELECT * FROM task WHERE id=:taskId")
    fun observeTask(taskId: String): Flow<LocalTask>

    @Query("SELECT * FROM task")
    suspend fun getAllTasks(): List<LocalTask>

    @Query("SELECT * FROM task WHERE id=:taskId")
    suspend fun getTask(taskId: String): LocalTask

    @Upsert
    suspend fun upsertTask(task: LocalTask)

    @Upsert
    suspend fun upsertAllTasks(tasks: List<LocalTask>)

    @Query("DELETE FROM task WHERE id = :taskId")
    suspend fun deleteTask(taskId: String)

    @Query("DELETE FROM task")
    suspend fun deleteAllTasks()

    @Query("DELETE FROM task WHERE isCompleted = 1")
    suspend fun deleteAllComleted()

    @Query("UPDATE task SET isCompleted = :isCompleted WHERE id = :taskId")
    suspend fun updateCompleted(taskId: String, isCompleted: Boolean)

}