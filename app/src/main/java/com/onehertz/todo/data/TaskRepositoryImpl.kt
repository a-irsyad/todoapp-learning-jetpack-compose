package com.onehertz.todo.data

import com.onehertz.todo.data.source.local.TaskDao
import com.onehertz.todo.data.source.network.NetworkDataSource
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
    private val networkDataSource: NetworkDataSource,
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
        return taskDao.observeTask(taskId).map { it.toTask() }
    }

    override suspend fun getTasks(forceUpdate: Boolean): List<Task> {
        if (forceUpdate) {
            refresh()
        }
        return withContext(dispatcher) {
            taskDao.getAllTasks().map { it.toTask() }
        }
    }

    override suspend fun getTask(taskId: String, forceUpdate: Boolean): Task? {
        if (forceUpdate) {
            refresh()
        }
        return taskDao.getTask(taskId)?.toTask()
    }

    override suspend fun refresh() {
        withContext(dispatcher) {
            val networkTasks = networkDataSource.fetchTasks()
            taskDao.deleteAllTasks()
            taskDao.upsertAllTasks(networkTasks.toLocalTasks())
        }
    }

    override suspend fun refreshTask() {
        TODO("continue from here")
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