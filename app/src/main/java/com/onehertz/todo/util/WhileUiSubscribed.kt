package com.onehertz.todo.util

import kotlinx.coroutines.flow.SharingStarted

val WhileUiSubscribed: SharingStarted = SharingStarted.WhileSubscribed(5000)