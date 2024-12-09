package com.onehertz.todo.ui.screen.task

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.onehertz.todo.R
import com.onehertz.todo.data.Task
import com.onehertz.todo.ui.theme.ToDoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    onTaskClick: (Task) -> Unit
) {
    Scaffold(
        topBar = { TaskScreenTopBar() },
        floatingActionButton = {
            FloatingActionButton(onClick = {}) {
                Icon(Icons.Default.Add, stringResource(R.string.add_task))
            }
        }
    ) { scaffoldPaddings ->
        TasksContent(
            isLoading = false,
            tasks = emptyList(),
            onRefresh = { },
            scaffoldPaddings = scaffoldPaddings,
            noTaskLabel = R.string.no_task,
            noTaskIconRes = R.drawable.no_task,
            onTaskClick = onTaskClick,
            pullRefreshState = rememberPullToRefreshState()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreenTopBar(
    title: @Composable () -> Unit = { Text(stringResource(R.string.app_name)) },
    openDrawer: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = title,
        navigationIcon = {
            IconButton(openDrawer) {
                Icon(
                    imageVector = Icons.Rounded.Menu,
                    contentDescription = stringResource(R.string.open_drawer)
                )
            }
        },
        actions = {
            FilterTasksMenu()
            MoreMenu()
        }
    )
}

@Composable
fun FilterTasksMenu(
    onFilterTask: () -> Unit = {},
    onActiveTask: () -> Unit = {},
    onCompletedTask: () -> Unit = {}
) {
    TopAppBarDropDownMenu(icon = {
        Icon(
            painterResource(R.drawable.ic_filter),
            stringResource(R.string.menu_filter)
        )
    }) { closeMenu ->
        DropdownMenuItem(
            text = { Text(stringResource(R.string.nav_all)) },
            onClick = { onFilterTask(); closeMenu() }
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.nav_active)) },
            onClick = { onActiveTask(); closeMenu() }
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.nav_completed)) },
            onClick = { onCompletedTask(); closeMenu() }
        )
    }
}


@Composable
fun MoreMenu(
    onClearCompleted: () -> Unit = {},
    onRefresh: () -> Unit = {}
) {
    TopAppBarDropDownMenu(icon = {
        Icon(
            painterResource(R.drawable.ic_more),
            stringResource(R.string.menu_more)
        )
    }) { closeMenu ->
        DropdownMenuItem(
            text = { Text(stringResource(R.string.nav_clear_completed)) },
            onClick = { onClearCompleted(); closeMenu() }
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.nav_refresh)) },
            onClick = { onRefresh(); closeMenu() }
        )
    }
}

@Composable
private fun TopAppBarDropDownMenu(
    icon: @Composable () -> Unit,
    content: @Composable ColumnScope.(() -> Unit) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
        IconButton(onClick = { expanded = !expanded }) {
            icon()
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            content { expanded = !expanded }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TasksContent(
    isLoading: Boolean,
    tasks: List<Task>,
    scaffoldPaddings: PaddingValues,
    pullRefreshState: PullToRefreshState,
    onRefresh: () -> Unit,
    onTaskClick: (Task) -> Unit,
    @DrawableRes noTaskIconRes: Int,
    @StringRes noTaskLabel: Int,
    modifier: Modifier = Modifier
) {
    PullToRefreshBox(
        isRefreshing = isLoading,
        onRefresh = onRefresh,
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (tasks.isEmpty()) {
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(noTaskIconRes),
                            contentDescription = null,
                            modifier = Modifier.size(96.dp)
                        )
                        Text(stringResource(noTaskLabel))
                    }
                }
            } else {
                items(tasks) { task ->
                    TaskItem(
                        task = task,
                        onClick = onTaskClick,
                        onCheckedChange = {}
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskItem(
    task: Task,
    onClick: (Task) -> Unit,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(task) }
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = onCheckedChange
        )
        Text(
            text = task.title,
            style = MaterialTheme.typography.titleLarge,
            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
            modifier = Modifier.padding(start = dimensionResource(R.dimen.dimen_margin_small))
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
fun TaskScreenPreview() {
    ToDoTheme {
        Surface {
            TaskScreen({})
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
fun TaskItemPreview() {
    ToDoTheme {
        Surface {
            TaskItem(
                task = Task("1", "title1", "description1"),
                onClick = {},
                onCheckedChange = {},
            )
        }
    }
}

