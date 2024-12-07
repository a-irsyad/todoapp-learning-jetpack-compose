package com.onehertz.todo.ui.screen.task

import androidx.lifecycle.SavedStateHandle
import com.onehertz.todo.MainCoroutineRule
import com.onehertz.todo.data.FakeTaskRepository
import com.onehertz.todo.data.Task
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
}