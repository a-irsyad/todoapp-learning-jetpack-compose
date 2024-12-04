package com.onehertz.todo.data

import com.onehertz.todo.data.source.local.LocalTask
import com.onehertz.todo.data.source.local.TaskDao
import com.onehertz.todo.data.source.network.NetworkDataSource
import com.onehertz.todo.di.ApplicationScope
import com.onehertz.todo.di.DefaultDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val networkDataSource: NetworkDataSource,

    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope

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

    override suspend fun createTask(title: String, description: String): String {
        val taskId: String
        withContext(dispatcher) {
            taskId = UUID.randomUUID().toString()
            taskDao.upsertTask(
                LocalTask(
                    id = taskId,
                    title = title,
                    description = description,
                    isCompleted = false
                )
            )

        }
        saveTaskToNetwork()
        return taskId
    }

    override suspend fun updateTask(taskId: String, title: String, description: String) {
        val task = getTask(taskId)?.copy(
            title = title,
            description = description
        ) ?: throw Exception("Task (id $taskId) not found")

        taskDao.upsertTask(task.toLocalTask())
        saveTaskToNetwork()
    }

    override suspend fun completeTask(taskId: String) {
        taskDao.updateCompleted(taskId, isCompleted = true)
        saveTaskToNetwork()
    }

    override suspend fun activateTask(taskId: String) {
        taskDao.updateCompleted(taskId, isCompleted = false)
        saveTaskToNetwork()
    }

    override suspend fun clearCompletedTasks() {
        taskDao.deleteAllComleted()
        saveTaskToNetwork()
    }

    override suspend fun deleteAllTasks() {
        taskDao.deleteAllTasks()
        saveTaskToNetwork()
    }

    override suspend fun deleteTask(taskId: String) {
        taskDao.deleteTask(taskId)
        saveTaskToNetwork()
    }

    private fun saveTaskToNetwork() {
        scope.launch {
            try {
                val localTasks = taskDao.getAllTasks()
                val networkTasks = withContext(dispatcher) {
                    localTasks.toNetworkTasks()
                }
                networkDataSource.uploadTasks(networkTasks)
            } catch (e: Exception) {
            }
        }
    }
}