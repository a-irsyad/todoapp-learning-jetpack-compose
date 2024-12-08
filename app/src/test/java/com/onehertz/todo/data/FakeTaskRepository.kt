package com.onehertz.todo.data

import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import java.util.UUID

class FakeTaskRepository : TaskRepository {

    private val _savedTasks: MutableStateFlow<List<Task>> = MutableStateFlow(emptyList())
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

    override suspend fun refresh() {
        if (shouldThrowError) _savedTasks.update { emptyList() }
    }

    override suspend fun createTask(title: String, description: String): String {
        val taskId = UUID.randomUUID().toString()
        val task = Task(id = taskId, title = title, description = description)
        _savedTasks.update { it.toMutableList().apply { add(task) } }
        return taskId
    }

    override suspend fun updateTask(taskId: String, title: String, description: String) {
        _savedTasks.update { tasks ->
            val index = tasks.indexOfFirst { it.id == taskId }
            if (index == -1) {
                tasks
            } else {
                tasks.toMutableList().apply {
                    this[index] = this[index].copy(title = title, description = description)
                }
            }
        }
    }

    override suspend fun completeTask(taskId: String) {
        _savedTasks.update { tasks ->
            val index = tasks.indexOfFirst { it.id == taskId }
            if (index == -1) {
                tasks
            } else {
                tasks.toMutableList().apply {
                    this[index] = this[index].copy(isCompleted = true)
                }
            }
        }
    }

    override suspend fun activateTask(taskId: String) {
        _savedTasks.update { tasks ->
            val index = tasks.indexOfFirst { it.id == taskId }
            if (index == -1) {
                tasks
            } else {
                tasks.toMutableList().apply {
                    this[index] = this[index].copy(isCompleted = false)
                }
            }
        }
    }

    override suspend fun clearCompletedTasks() {
        _savedTasks.update { tasks ->
            tasks.filter { !it.isCompleted }
        }
    }

    override suspend fun deleteAllTasks() {
        _savedTasks.update { emptyList() }
    }

    override suspend fun deleteTask(taskId: String) {
        _savedTasks.update { tasks ->
            tasks.filter { it.id == taskId }
        }
    }

    @VisibleForTesting
    fun addTasksForTesting(vararg tasks: Task){
        _savedTasks.update{
            it.toMutableList().apply { addAll(tasks) }
        }
    }

    @VisibleForTesting
    fun setShouldThrowError(throwError: Boolean){
        shouldThrowError = throwError
    }

}