package com.onehertz.todo.data

import com.onehertz.todo.data.source.local.FakeTaskDao
import com.onehertz.todo.data.source.network.FakeNetworkDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TaskRepositoryImplTest {

    private lateinit var taskRepositoryImpl: TaskRepositoryImpl
    private lateinit var fakeTaskDao: FakeTaskDao
    private lateinit var fakeNetworkDataSource: FakeNetworkDataSource

    private val task1 = Task("1", "title1", "description1")
    private val task2 = Task("2", "title2", "description2")
    private val task3 = Task("3", "title3", "description3")
    private val localTasks = listOf(task3)
    private val networkTasks = listOf(task1, task2)
    private val newTasks = listOf(Task("4", "title4", "description4"))

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun createRepository() {
        fakeTaskDao = FakeTaskDao(localTasks.toLocalTasks())
        fakeNetworkDataSource = FakeNetworkDataSource(networkTasks.toNetworkTask())

        taskRepositoryImpl = TaskRepositoryImpl(
            taskDao = fakeTaskDao,
            networkDataSource = fakeNetworkDataSource,
            dispatcher = testDispatcher,
            scope = testScope
        )
    }

    @Test
    fun getTasks__emptyRepositoryAndUninitialisedCache() = testScope.runTest {
        fakeTaskDao.deleteAllTasks()
        fakeNetworkDataSource.uploadTasks(emptyList())
        assertEquals(taskRepositoryImpl.getTasks().size, 0)
    }

    @Test
    fun getTasks_repositoryCachesAfterFirstApiCall() = testScope.runTest {
        val firstFetch = taskRepositoryImpl.getTasks(forceUpdate = true)
        fakeNetworkDataSource.uploadTasks(newTasks.toNetworkTask())
        val secondFetch = taskRepositoryImpl.getTasks()
        assertEquals(firstFetch, secondFetch)
    }

    @Test
    fun getTask_requestAllTasksFromNetworkDataSource() = testScope.runTest {
        val localTask = taskRepositoryImpl.getTasks(true)
        val remoteTask = fakeNetworkDataSource.fetchTasks().toTasks()
        assertEquals(localTask, remoteTask)
    }

    @Test
    fun createTask_alsoSaveToLocalAndNetwork() = testScope.runTest {
        val newTaskId = taskRepositoryImpl.createTask("title", "description")
        assertNotNull(fakeTaskDao.getAllTasks().find { it.id == newTaskId })
        assertNotNull(fakeNetworkDataSource.fetchTasks().find { it.id == newTaskId })
    }

    @Test
    fun getTasks_withDirtyCache_tasksAreFetchedFromRemote() = testScope.runTest {
        val tasks = taskRepositoryImpl.getTasks(false)
        fakeNetworkDataSource.uploadTasks(newTasks.toNetworkTask())
        val cachedTask = taskRepositoryImpl.getTasks(false)
        assertEquals(tasks, cachedTask)

        val refreshedTask = taskRepositoryImpl.getTasks(true)
        assertEquals(refreshedTask, newTasks)
    }

    @Test
    fun getTasks_ifNetworkDataSourceUnavailable_tasksAreRetrievedFromLocal() = testScope.runTest {
        fakeNetworkDataSource.uploadTasks(emptyList())
        assertEquals(taskRepositoryImpl.getTasks(), fakeTaskDao.getAllTasks().toTasks())
    }

    @Test
    fun getTasks_ifBothNetworkAndLocalDataUnavailable_resultNull() = testScope.runTest {
        fakeNetworkDataSource.uploadTasks(emptyList())
        fakeTaskDao.deleteAllTasks()
        assertEquals(taskRepositoryImpl.getTasks(true).size, 0)
    }

    @Test
    fun completeTask_setTaskAsCompleted() = testScope.runTest {
        val newTaskId = taskRepositoryImpl.createTask("title", "description")
        assertFalse(taskRepositoryImpl.getTasks().find { it.id == newTaskId }!!.isCompleted)
        taskRepositoryImpl.completeTask(newTaskId)
        assertTrue(taskRepositoryImpl.getTasks().find { it.id == newTaskId }!!.isCompleted)
    }

    @Test
    fun deleteCompletedTasks() = testScope.runTest {
        fakeTaskDao.deleteAllTasks()
        val completedTask = task1.copy(isCompleted = true)
        fakeTaskDao.upsertAllTasks(listOf(completedTask.toLocalTask(), task2.toLocalTask()))
        taskRepositoryImpl.clearCompletedTasks()

        val tasks = taskRepositoryImpl.getTasks()
        assertEquals(tasks.size, 1)
        assertTrue(tasks.contains(task2))
        assertFalse(tasks.contains(completedTask))
    }

    @Test
    fun deleteAllTasks() = testScope.runTest {
        assertTrue(taskRepositoryImpl.getTasks().isNotEmpty())
        taskRepositoryImpl.deleteAllTasks()
        assertTrue(taskRepositoryImpl.getTasks().isEmpty())
    }

    @Test
    fun deleteTask() = testScope.runTest {
        taskRepositoryImpl.deleteAllTasks()
        val task1Id = taskRepositoryImpl.createTask("title1", "desc1")
        val task2Id = taskRepositoryImpl.createTask("title2", "desc2")
        taskRepositoryImpl.deleteTask(task1Id)
        val tasks = taskRepositoryImpl.getTasks()
        assertNull(tasks.find { it.id == task1Id })
        assertNotNull(tasks.find { it.id == task2Id })
        assertEquals(tasks.size, 1)
    }
}