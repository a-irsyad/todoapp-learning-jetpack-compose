package com.onehertz.todo.ui.screen.task

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.onehertz.todo.R
import com.onehertz.todo.data.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject


@HiltViewModel
class TaskScreenViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _savedFilterType =
        savedStateHandle.getStateFlow(TASKS_FILTER_SAVED_STATE_KEY, TaskFilterType.ALL_TASKS)

    private val _filterUiInfo = _savedFilterType.map { getFilterUiInfo(it) }.distinctUntilChanged()


    private fun getFilterUiInfo(taskFilterType: TaskFilterType): FilteringUiInfo {
        when (taskFilterType) {
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