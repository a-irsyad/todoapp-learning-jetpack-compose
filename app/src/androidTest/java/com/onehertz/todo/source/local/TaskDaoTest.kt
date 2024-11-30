package com.onehertz.todo.source.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class TaskDaoTest {
    private lateinit var taskDao: TaskDao
    private lateinit var toDoDatabase: ToDoDatabase

    private val localTask1 = LocalTask("1", "title1", "description1", false)
    private val localTask2 = LocalTask("2", "title2", "description2", true)
    private val localTasks = listOf(localTask1, localTask2)

    @Before
    fun createDatabase() {
        val context: Context = ApplicationProvider.getApplicationContext()
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        toDoDatabase = Room.inMemoryDatabaseBuilder(context, ToDoDatabase::class.java)
            .allowMainThreadQueries() // Allowing main thread queries, just for testing.
            .build()
        taskDao = toDoDatabase.taskDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        toDoDatabase.close()
    }

    @Test
    @Throws(Exception::class)
    fun upsertTask_insertNewTask_newTaskAdded() = runBlocking{
        taskDao.upsertTask(localTask1)
        assertEquals(localTask1, taskDao.getTask(localTask1.id))
    }

    @Test
    @Throws(Exception::class)
    fun upsertTask_updateTask_taskUpdated() = runBlocking{
        taskDao.upsertTask(localTask1)
        val updatedLocalTask1 = localTask1.copy(title = "updated title")
        taskDao.upsertTask(updatedLocalTask1)
        assertEquals(updatedLocalTask1, taskDao.getTask(localTask1.id))
    }

    @Test
    @Throws(Exception::class)
    fun upsertAllTask_insertAllTasks_allTasksUpdated() = runBlocking{
        taskDao.upsertAllTasks(localTasks)
        assertEquals(localTasks, taskDao.getAllTasks())
    }

    @Test
    @Throws(Exception::class)
    fun upsertAllTask_updateAllTasks_allTasksUpdated() = runBlocking{
        taskDao.upsertAllTasks(localTasks)
        val updatedLocalTask1 = localTask1.copy(title = "updated title")
        val updatedLocalTask2 = localTask2.copy(title = "updated title")
        val updatedLocalTasks = listOf(updatedLocalTask1, updatedLocalTask2)
        taskDao.upsertAllTasks(updatedLocalTasks)
        assertEquals(updatedLocalTasks, taskDao.getAllTasks())
    }

}