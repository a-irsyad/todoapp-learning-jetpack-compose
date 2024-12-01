package com.onehertz.todo.data.source.network

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class NetworkDataSourceImpl : NetworkDataSource {

    private val mutex = Mutex()
    private var tasks = listOf(
        NetworkTask(
            id = "PISA",
            title = "Build tower in Pisa",
            shortDescription = "Ground looks good, no foundation work required"
        ),
        NetworkTask(
            id = "TACOMA",
            title = "Finish bridge in Tacoma",
            shortDescription = "Found awesome girders at half the cost!"
        )
    )

    override suspend fun fetchTasks(): List<NetworkTask> = mutex.withLock {
        delay(SERVICE_LATENCY_IN_MILLIS)
        return tasks
    }

    override suspend fun uploadTasks(tasks: List<NetworkTask>) = mutex.withLock {
        delay(SERVICE_LATENCY_IN_MILLIS)
        this.tasks = tasks
    }
}

private const val SERVICE_LATENCY_IN_MILLIS = 2000L