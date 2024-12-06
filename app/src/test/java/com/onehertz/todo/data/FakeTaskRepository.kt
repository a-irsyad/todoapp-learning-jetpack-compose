package com.onehertz.todo.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import java.util.UUID

class FakeTaskRepository : TaskRepository {

    private val _savedTasks: MutableStateFlow<MutableList<Task>> = MutableStateFlow(mutableListOf())
    private val savedTask: StateFlow<List<Task>> = _savedTasks.asStateFlow()
    private var shouldThrowError = false

    override fun getTasksStream(): Flow<List<Task>> =
        if (shouldThrowError) throw Exception("Error") else savedTask

    override fun getTaskStream(taskId: String): Flow<Task?> = flow {
        emit(savedTask.value.find { it.id == taskId })
    }

    override suspend fun getTasks(forceUpdate: Boolean): List<Task> =
        if (shouldThrowError) throw Exception("Error") else savedTask.first()

    override suspend fun getTask(taskId: String, forceUpdate: Boolean): Task? =
        if (shouldThrowError) throw Exception("Error") else savedTask.first()
            .find { it.id == taskId }

    override suspend fun refresh() {}

    override suspend fun createTask(title: String, description: String): String {
        val taskId = UUID.randomUUID().toString()
        val task = Task(id = taskId, title = title, description = description)
        _savedTasks.update { it.toMutableList().apply { add(task) } }
        return taskId
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