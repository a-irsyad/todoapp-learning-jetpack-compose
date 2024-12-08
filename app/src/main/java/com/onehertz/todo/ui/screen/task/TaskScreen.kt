package com.onehertz.todo.ui.screen.task

import android.content.res.Configuration
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.onehertz.todo.R
import com.onehertz.todo.ui.theme.ToDoTheme

@Composable
fun TaskScreen(

) {
    Scaffold(
        topBar = {
            TaskScreenTopBar(
            )
        },
    ) { scaffoldPaddings ->
        Text("Task Screen : $scaffoldPaddings")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreenTopBar(
    title: @Composable () -> Unit = { Text(stringResource(R.string.app_name)) },
    openDrawer: () -> Unit = {},
    modifier: Modifier = Modifier
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
            TODO("continue here")
        }
    )
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

