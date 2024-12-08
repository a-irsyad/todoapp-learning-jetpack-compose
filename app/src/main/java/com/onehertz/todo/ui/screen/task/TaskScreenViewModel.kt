package com.onehertz.todo.ui.screen.task

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onehertz.todo.R
import com.onehertz.todo.data.Task
import com.onehertz.todo.data.TaskRepository
import com.onehertz.todo.ui.ADD_RESULT_OK
import com.onehertz.todo.ui.DELETE_RESULT_OK
import com.onehertz.todo.ui.EDIT_RESULT_OK
import com.onehertz.todo.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TaskScreenViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _savedFilterType =
        savedStateHandle.getStateFlow(TASKS_FILTER_SAVED_STATE_KEY, TaskFilterType.ALL_TASKS)

    private val _filterUiInfo = _savedFilterType.map { getFilterUiInfo(it) }.distinctUntilChanged()
    private val _userMessage: MutableStateFlow<Int?> = MutableStateFlow(null)
    private val _isLoading = MutableStateFlow(false)
    private val _filteredTasks =
        combine(taskRepository.getTasksStream(), _savedFilterType) { tasks, filterType ->
            filterTasks(tasks, filterType)
        }
            .map { Result.Success(it) }
            .catch<Result<List<Task>>> { emit(Result.Error(R.string.loading_tasks_error)) }
    val uiState: StateFlow<TaskScreenUiState> = combine(
        _filteredTasks, _isLoading, _userMessage, _filterUiInfo
    ) { filteredTasks, isLoading, userMessage, filterUiInfo ->
        when (filteredTasks) {
            Result.Loading -> TaskScreenUiState(isLoading = true)

            is Result.Error ->
                TaskScreenUiState(userMessage = filteredTasks.errorMessage)

            is Result.Success ->
                TaskScreenUiState(
                    tasks = filteredTasks.data,
                    isLoading = false,
                    userMessage = userMessage,
                    filteringUiInfo = filterUiInfo
                )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = TaskScreenUiState(isLoading = true)
    )

    fun setTaskFilter(taskFilterType: TaskFilterType) {
        savedStateHandle[TASKS_FILTER_SAVED_STATE_KEY] = taskFilterType
    }

    fun clearCompletedTasks() = viewModelScope.launch {
        taskRepository.clearCompletedTasks()
        showSnackBarMessage(R.string.completed_task_cleared)
        refresh()
    }

    fun completeTask(task: Task, isCompleted: Boolean) = viewModelScope.launch {
        if (isCompleted) {
            taskRepository.completeTask(task.id)
            showSnackBarMessage(R.string.task_marked_complete)
        } else {
            taskRepository.activateTask(task.id)
            showSnackBarMessage(R.string.task_mark_active)
        }
    }

    private fun showSnackBarMessage(message: Int) {
        _userMessage.value = message
    }

    fun snackbarMessageShown() {
        _userMessage.value = null
    }

    fun refresh() {
        _isLoading.value = true
        viewModelScope.launch {
            taskRepository.refresh()
            _isLoading.value = false
        }
    }

    private fun filterTasks(tasks: List<Task>, filterType: TaskFilterType): List<Task> {
        return when (filterType) {
            TaskFilterType.ALL_TASKS -> tasks
            TaskFilterType.ACTIVE_TASK -> tasks.filter { !it.isCompleted }
            TaskFilterType.COMPLETED_TASK -> tasks.filter { it.isCompleted }
        }
    }

    private fun getFilterUiInfo(taskFilterType: TaskFilterType): FilteringUiInfo {
        return when (taskFilterType) {
            TaskFilterType.ALL_TASKS -> {
                FilteringUiInfo(
                    filteringLabel = R.string.all,
                    noTaskIconRes = R.drawable.no_task,
                    noTaskLabel = R.string.no_task
                )
            }

            TaskFilterType.ACTIVE_TASK -> {
                FilteringUiInfo(
                    filteringLabel = R.string.active_task,
                    noTaskIconRes = R.drawable.no_active_task,
                    noTaskLabel = R.string.no_active_task
                )
            }

            TaskFilterType.COMPLETED_TASK -> {
                FilteringUiInfo(
                    filteringLabel = R.string.completed_task,
                    noTaskIconRes = R.drawable.no_completed_task,
                    noTaskLabel = R.string.no_completed_task
                )
            }
        }
    }

    fun showEditResultMessage(result: Int){
        when (result){
            ADD_RESULT_OK -> showSnackBarMessage(R.string.successfully_added_task_message)
            EDIT_RESULT_OK -> showSnackBarMessage(R.string.successfully_saved_task_message)
            DELETE_RESULT_OK -> showSnackBarMessage(R.string.successfully_deleted_task_message)
        }
    }
}


data class TaskScreenUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
    val filteringUiInfo: FilteringUiInfo = FilteringUiInfo()
)

data class FilteringUiInfo(
    val filteringLabel: Int = R.string.all,
    val noTaskIconRes: Int = R.drawable.no_task,
    val noTaskLabel: Int = R.string.no_task
)

enum class TaskFilterType {
    ALL_TASKS, ACTIVE_TASK, COMPLETED_TASK
}

private const val TASKS_FILTER_SAVED_STATE_KEY = "TASKS_FILTER_SAVED_STATE_KEY"