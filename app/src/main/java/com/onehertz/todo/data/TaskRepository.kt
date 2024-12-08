package com.onehertz.todo.data

import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getTasksStream(): Flow<List<Task>>
    fun getTaskStream(taskId: String): Flow<Task?>

    suspend fun getTasks(forceUpdate: Boolean = false): List<Task>
    suspend fun getTask(taskId: String, forceUpdate: Boolean = false): Task?

    suspend fun refresh()

    suspend fun createTask(title: String, description: String): String
    suspend fun updateTask(taskId: String, title: String, description: String)
    suspend fun completeTask(taskId: String)
    suspend fun activateTask(taskId: String)
    suspend fun clearCompletedTasks()
    suspend fun deleteAllTasks()
    suspend fun deleteTask(taskId: String)
}