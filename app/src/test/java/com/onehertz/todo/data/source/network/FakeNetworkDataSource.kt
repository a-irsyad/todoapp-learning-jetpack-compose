package com.onehertz.todo.data.source.network

class FakeNetworkDataSource(
    initialTasks: List<NetworkTask> = emptyList()
) : NetworkDataSource {

    private var _tasks: MutableList<NetworkTask> = initialTasks.toMutableList()

    override suspend fun fetchTasks(): List<NetworkTask> = _tasks.toList()

    override suspend fun uploadTasks(tasks: List<NetworkTask>) {
        _tasks.clear()
        _tasks.addAll(tasks)
    }
}