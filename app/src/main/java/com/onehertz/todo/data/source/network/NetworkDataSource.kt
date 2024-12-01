package com.onehertz.todo.data.source.network

interface NetworkDataSource {
    suspend fun fetchTasks(): List<NetworkTask>
    suspend fun uploadTasks(tasks: List<NetworkTask>)
}