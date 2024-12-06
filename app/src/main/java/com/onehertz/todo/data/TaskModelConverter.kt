package com.onehertz.todo.data

import com.onehertz.todo.data.source.local.LocalTask
import com.onehertz.todo.data.source.network.NetworkTask
import com.onehertz.todo.data.source.network.TaskStatus

fun Task.toLocalTask() = LocalTask(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted
)

@JvmName("externalToLocal")
fun List<Task>.toLocalTasks() = map(Task::toLocalTask)

fun LocalTask.toTask() = Task(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted
)

@JvmName("localToExternal")
fun List<LocalTask>.toTasks() = map(LocalTask::toTask)

fun NetworkTask.toTask() = Task(
    id = id,
    title = title,
    description = shortDescription,
    isCompleted = (status == TaskStatus.COMPLETE)
)

@JvmName("networkToExternal")
fun List<NetworkTask>.toTasks() = map(NetworkTask::toTask)

fun Task.toNetworkTask() = NetworkTask(
    id = id,
    title = title,
    shortDescription = description,
    status = if (isCompleted) TaskStatus.COMPLETE else TaskStatus.ACTIVE
)

@JvmName("externalToNetwork")
fun List<Task>.toNetworkTask() = map(Task::toNetworkTask)

fun NetworkTask.toLocalTask() = LocalTask(
    id = id,
    title = title,
    description = shortDescription,
    isCompleted = (status == TaskStatus.COMPLETE)
)

@JvmName("networkToLocal")
fun List<NetworkTask>.toLocalTasks() = map(NetworkTask::toLocalTask)

fun LocalTask.toNetworkTask() = NetworkTask(
    id = id,
    title = title,
    shortDescription = description,
    status = if (isCompleted) TaskStatus.COMPLETE else TaskStatus.ACTIVE
)

@JvmName("localToNetwork")
fun List<LocalTask>.toNetworkTasks() = map(LocalTask::toNetworkTask)