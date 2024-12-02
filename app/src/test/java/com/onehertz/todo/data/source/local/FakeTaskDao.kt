package com.onehertz.todo.data.source.local

import kotlinx.coroutines.flow.Flow

class FakeTaskDao(initialTask: List<LocalTask> = emptyList()) : TaskDao {

    private var _tasks: MutableList<LocalTask> = initialTask.toMutableList()

    override fun observeAllTasks(): Flow<List<LocalTask>> {
        TODO("Not yet implemented")
    }

    override fun observeTask(taskId: String): Flow<LocalTask> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllTasks(): List<LocalTask> = _tasks

    override suspend fun getTask(taskId: String): LocalTask? {
        return _tasks.find { it.id == taskId }
    }

    override suspend fun upsertTask(task: LocalTask) {
        var taskIndex = _tasks.indexOfFirst { it.id == task.id }
        if (taskIndex != -1) {
            _tasks[taskIndex] = task
        } else {
            _tasks.add(task)
        }
    }

    override suspend fun upsertAllTasks(tasks: List<LocalTask>) {
        _tasks.clear()
        _tasks.addAll(tasks)
    }

    override suspend fun deleteTask(taskId: String) {
        val index = _tasks.indexOfFirst { it.id == taskId }
        if (index != -1) {
            _tasks.removeAt(index)
        }
    }

    override suspend fun deleteAllTasks() {
        _tasks.clear()
    }

    override suspend fun deleteAllComleted() {
        _tasks.removeIf { it.isCompleted }
    }

    override suspend fun updateCompleted(taskId: String, isCompleted: Boolean) {
        val index = _tasks.indexOfFirst { it.id == taskId }
        if (index != -1) {
            _tasks[index] = _tasks[index].copy(isCompleted = true)
        }
    }
}