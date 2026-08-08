package com.example.androidinterview.util

import com.example.androidinterview.domain.model.ActivityStatus
import com.example.androidinterview.domain.model.ActivityType

fun ActivityStatus.toFriendlyStatus(): String = name.lowercase().replaceFirstChar { it.uppercase() }

fun ActivityType.toFriendlyType(): String = name.lowercase().replaceFirstChar { it.uppercase() }