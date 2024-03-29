/**
 * Layout that displays when the user is prompted to confirm their attendance to or
 * completion of a task session
 */

package com.example.commutual.ui.screens.item.task_reminder

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.commutual.FormatterClass.Companion.formatTimestamp
import com.example.commutual.R
import com.example.commutual.model.AlarmReceiver.Companion.ATTENDANCE
import com.example.commutual.model.Task


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationItem(
    confirmationType: Int,
    task: Task,
    onCLick: () -> Unit,
    onYesClick: () -> Unit,
    onNoClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(20.dp, 8.dp)
    ) {
        // Show timestamp accordingly
        Text(
            text = if (confirmationType == ATTENDANCE) {
                // Attendance timestamp
                formatTimestamp(task.showAttendanceTimestamp, true)
            } else {
                // Completion timestamp
                formatTimestamp(task.showCompletionTimestamp, true)
            },
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .padding(vertical = 4.dp)
                .fillMaxWidth()
        )

        Surface(
            onClick = onCLick,
            shape = RoundedCornerShape(8.dp),
            shadowElevation = 4.dp,
            color = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ) {
            Column(
                Modifier
                    .padding(12.dp, 12.dp)
                    .fillMaxWidth()
            ) {

                // Show item title accordingly
                Text(
                    if (confirmationType == ATTENDANCE) {
                        // Attendance title
                        stringResource(R.string.task_starting)
                    } else {
                        // Completion title
                        stringResource(R.string.task_over)
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .fillMaxWidth()
                )

                Divider()

                // Task title
                Text(
                    stringResource(
                        R.string.formatted_task_title, task.title
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .fillMaxWidth()
                )

                // Task session duration
                Text(
                    stringResource(
                        R.string.formatted_task_time_nd,
                        task.startTime,
                        task.endTime
                    ),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .fillMaxWidth()
                )

                // Prompt confirmation question
                Text(
                    text = if (confirmationType == ATTENDANCE) {
                        // Attendance question
                        stringResource(
                            R.string.are_you_here
                        )
                    } else {
                        // Confirmation question
                        stringResource(
                            R.string.did_you_complete
                        )
                    },
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .fillMaxWidth()
                )

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(vertical = 4.dp, horizontal = 12.dp)
                        .fillMaxWidth()
                ) {

                    // Yes Button
                    Button(
                        onClick = onYesClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .size(48.dp)
                            .weight(1f)

                    ) {
                        Text(
                            stringResource(R.string.yes),
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }

                    // No Button
                    Button(
                        onClick = onNoClick,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                            .size(64.dp, 48.dp)
                    ) {
                        Text(
                            stringResource(R.string.no),
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
            }
        }
    }
}





