package com.onehertz.todo.ui.screen.task

import androidx.lifecycle.SavedStateHandle
import com.onehertz.todo.MainCoroutineRule
import com.onehertz.todo.R
import com.onehertz.todo.data.FakeTaskRepository
import com.onehertz.todo.data.Task
import com.onehertz.todo.ui.ADD_RESULT_OK
import com.onehertz.todo.ui.DELETE_RESULT_OK
import com.onehertz.todo.ui.EDIT_RESULT_OK
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TaskScreenViewModelTest {

    @get:Rule
    val rule = MainCoroutineRule()

    private lateinit var viewmodel: TaskScreenViewModel
    private lateinit var fakeTaskRepository: FakeTaskRepository


    @Before
    fun setupViewModel() {
        val task1 = Task(id = "1", title = "Title1", description = "Desc1")
        val task2 = Task(id = "2", title = "Title2", description = "Desc2", isCompleted = true)
        val task3 = Task(id = "3", title = "Title3", description = "Desc3", isCompleted = true)
        fakeTaskRepository = FakeTaskRepository()
        fakeTaskRepository.addTasksForTesting(task1, task2, task3)

        viewmodel = TaskScreenViewModel(
            taskRepository = fakeTaskRepository,
            savedStateHandle = SavedStateHandle()
        )
    }

    @Test
    fun loadActiveTasksFromRepositoryAndLoadIntoView() = runTest{
        viewmodel.setTaskFilter(TaskFilterType.ACTIVE_TASK)
        viewmodel.refresh()
        assertFalse(viewmodel.uiState.first().isLoading)
        assertEquals(viewmodel.uiState.first().tasks.size, 1)
    }

    @Test
    fun loadCompletedTasksFromRepositoryAndLoadIntoView() = runTest{
        viewmodel.setTaskFilter(TaskFilterType.COMPLETED_TASK)
        viewmodel.refresh()
        assertFalse(viewmodel.uiState.first().isLoading)
        assertEquals(viewmodel.uiState.first().tasks.size, 2)
    }

    @Test
    fun loadTasks_error() = runTest{
        fakeTaskRepository.setShouldThrowError(true)
        viewmodel.refresh()
        assertFalse(viewmodel.uiState.first().isLoading)
        assertEquals(0, viewmodel.uiState.first().tasks.size)
    }

    @Test
    fun clearCompletedTasks_clearsTasks() = runTest{
        viewmodel.clearCompletedTasks()
        val allTasks = viewmodel.uiState.first().tasks
        val completedTasks = allTasks.filter{it.isCompleted}
        val activeTask = allTasks.filter{!it.isCompleted}
        assertTrue(completedTasks.isEmpty())
        assertEquals(1, activeTask.size)
        assertEquals(R.string.completed_task_cleared, viewmodel.uiState.first().userMessage)
    }

    @Test
    fun showEditResultMessages_editOk_snackbarUpdated() = runTest{
        viewmodel.showEditResultMessage(EDIT_RESULT_OK)
        assertEquals(viewmodel.uiState.first().userMessage, R.string.successfully_saved_task_message)
    }

    @Test
    fun showEditResultMessages_deleteOK_snackbarUpdated() = runTest{
        viewmodel.showEditResultMessage(DELETE_RESULT_OK)
        assertEquals(viewmodel.uiState.first().userMessage, R.string.successfully_deleted_task_message)
    }

    @Test
    fun showEditResultMessages_addOk_snackbarUpdated() = runTest{
        viewmodel.showEditResultMessage(ADD_RESULT_OK)
        assertEquals(viewmodel.uiState.first().userMessage, R.string.successfully_added_task_message)
    }

    @Test
    fun completeTask_dataAndSnackBarUpdated() = runTest{
        val task = Task("0", "title", "description")
        fakeTaskRepository.addTasksForTesting(task)
        viewmodel.completeTask(task, true)
        assertTrue(fakeTaskRepository.getTask(task.id, false)?.isCompleted ?: false)
        assertEquals(viewmodel.uiState.first().userMessage, R.string.task_marked_complete)
    }

    @Test
    fun activateTask_dataAndSnackBarUpdated() = runTest{
        val task = Task("0", "title", "description", true)
        fakeTaskRepository.addTasksForTesting(task)
        viewmodel.completeTask(task, false)
        assertFalse(fakeTaskRepository.getTask(task.id, false)?.isCompleted ?: true)
        assertEquals(viewmodel.uiState.first().userMessage, R.string.task_mark_active)
    }
}