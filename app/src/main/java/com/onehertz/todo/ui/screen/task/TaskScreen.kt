package com.onehertz.todo.ui.screen.task

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.onehertz.todo.R
import com.onehertz.todo.ui.theme.ToDoTheme

@Composable
fun TaskScreen(

) {
    Scaffold(
        topBar = { TaskScreenTopBar() },
        floatingActionButton = {
            FloatingActionButton(onClick = {}){
                Icon(Icons.Default.Add, stringResource(R.string.add_task))
            }
        }
    ) { scaffoldPaddings ->
        Text("Task Screen", modifier = Modifier.padding(scaffoldPaddings))
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



@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
fun TaskScreenPreview() {
    ToDoTheme {
        Surface {
            TaskScreen()
        }
    }
}

