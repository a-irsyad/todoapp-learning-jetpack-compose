package com.onehertz.todo.source.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.onehertz.todo.data.source.local.LocalTask
import com.onehertz.todo.data.source.local.TaskDao
import com.onehertz.todo.data.source.local.ToDoDatabase
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
    fun upsertTask_insertNewTask_newTaskAdded() = runBlocking {
        taskDao.upsertTask(localTask1)
        assertEquals(localTask1, taskDao.getTask(localTask1.id))
    }

    @Test
    @Throws(Exception::class)
    fun upsertTask_updateTask_taskUpdated() = runBlocking {
        taskDao.upsertTask(localTask1)
        val updatedLocalTask1 = localTask1.copy(title = "updated title")
        taskDao.upsertTask(updatedLocalTask1)
        assertEquals(updatedLocalTask1, taskDao.getTask(localTask1.id))
    }

    @Test
    @Throws(Exception::class)
    fun upsertAllTask_insertAllTasks_allTasksUpdated() = runBlocking {
        taskDao.upsertAllTasks(localTasks)
        assertEquals(localTasks, taskDao.getAllTasks())
    }

    @Test
    @Throws(Exception::class)
    fun upsertAllTask_updateAllTasks_allTasksUpdated() = runBlocking {
        taskDao.upsertAllTasks(localTasks)
        val updatedLocalTask1 = localTask1.copy(title = "updated title")
        val updatedLocalTask2 = localTask2.copy(title = "updated title")
        val updatedLocalTasks = listOf(updatedLocalTask1, updatedLocalTask2)
        taskDao.upsertAllTasks(updatedLocalTasks)
        assertEquals(updatedLocalTasks, taskDao.getAllTasks())
    }

    @Test
    @Throws(Exception::class)
    fun observeTask_returnFlowOfLocalTask() = runBlocking {
        taskDao.upsertTask(localTask1)
        val flow = taskDao.observeTask(localTask1.id)
        flow.take(1).collect { value ->
            assertEquals(localTask1, value)
        }
    }

    @Test
    @Throws(Exception::class)
    fun observeAllTask_returnFlowOfAllLocalTasks() = runBlocking {
        taskDao.upsertAllTasks(localTasks)
        val flow = taskDao.observeAllTasks()
        flow.take(1).collect { value ->
            assertEquals(localTasks, value)
        }
    }

    @Test
    @Throws(Exception::class)
    fun deleteTask_aTaskDeleted() = runBlocking {
        taskDao.upsertTask(localTask1)
        taskDao.deleteTask(localTask1.id)
        assertTrue(!taskDao.getAllTasks().contains(localTask1))
    }

    @Test
    @Throws(Exception::class)
    fun deleteAllTask_allTaskDeleted() = runBlocking {
        taskDao.upsertAllTasks(localTasks)
        taskDao.deleteAllTasks()
        assertTrue(taskDao.getAllTasks().isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun deleteAllCompleted_allCompletedTaskDeleted() = runBlocking {
        taskDao.upsertAllTasks(localTasks)
        taskDao.deleteAllComleted()
        val completedTask = taskDao.getAllTasks().filter { it.isCompleted }
        val incompletedTask = taskDao.getAllTasks().filter { !it.isCompleted }
        assertTrue(completedTask.isEmpty())
        assertTrue(incompletedTask.isNotEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun updateCompleted_canChangeTaskCompletedStatus() = runBlocking {
        taskDao.upsertTask(localTask1)
        taskDao.updateCompleted(localTask1.id, false)
        assertEquals(taskDao.getTask(localTask1.id).isCompleted, false)
        taskDao.updateCompleted(localTask1.id, true)
        assertEquals(taskDao.getTask(localTask1.id).isCompleted, true)
    }
}