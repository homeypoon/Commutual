/**
 * This file contains the EditTaskUiState data class.
 * It is used to represent the state of the EditTaskScreen in the UI.
 */

package com.example.commutual.ui.screens.edit_task

import com.example.commutual.model.Task
import com.google.firebase.Timestamp

data class EditTaskUiState(
    val task: Task = Task(),
    val date: Long = Timestamp.now().toDate().time,
    val selectedDateText: String = "",

    val startTime: String = "",
    val endTime: String = "",

    val selectedYear: Int = 0,
    val selectedMonth: Int = 0,
    val selectedDay: Int = 0,

    val startHour: Int = 0,
    val startMin: Int = 0,
    val endHour: Int = 0,
    val endMin: Int = 0,

    val expandedDropDownMenu: Boolean = false,
    val completed: Boolean = false
)

