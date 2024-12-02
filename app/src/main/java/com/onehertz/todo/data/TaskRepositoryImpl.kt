package com.onehertz.todo.data

import com.onehertz.todo.data.source.local.TaskDao
import com.onehertz.todo.di.DefaultDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher
) : TaskRepository {
    override fun getTasksStream(): Flow<List<Task>> {
        return taskDao.observeAllTasks().map {
            withContext(dispatcher) {
                it.toTasks()
            }
        }
    }

    override fun getTaskStream(taskId: String): Flow<Task> {
        TODO("continue from here")
    }

    override suspend fun getTasks(forceUpdate: Boolean): List<Task> {
        TODO("Not yet implemented")
    }

    override suspend fun getTask(taskId: String, forceUpdate: Boolean): Task? {
        TODO("Not yet implemented")
    }

    override suspend fun refresh() {
        TODO("Not yet implemented")
    }

    override suspend fun refreshTask() {
        TODO("Not yet implemented")
    }

    override suspend fun createTask(title: String, description: String) {
        TODO("Not yet implemented")
    }

    override suspend fun updateTask(taskId: String, title: String, description: String) {
        TODO("Not yet implemented")
    }

    override suspend fun completeTask(taskId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun activateTask(taskId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun clearCompletedTasks() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllTasks() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTask(taskId: String) {
        TODO("Not yet implemented")
    }
}