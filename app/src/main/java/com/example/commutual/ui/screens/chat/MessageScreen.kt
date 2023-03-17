package com.example.commutual.ui.screens.chat

import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.commutual.FormatterClass
import com.example.commutual.R
import com.example.commutual.common.composable.BasicToolbar
import com.example.commutual.common.composable.MessageInputField
import com.example.commutual.model.Message
import com.example.commutual.model.Task
import com.example.commutual.model.Task.Companion.ATTENDANCE_NO
import com.example.commutual.model.Task.Companion.ATTENDANCE_NOT_DONE
import com.example.commutual.model.Task.Companion.ATTENDANCE_YES
import com.example.commutual.model.Task.Companion.COMPLETION_NO
import com.example.commutual.model.Task.Companion.COMPLETION_NOT_DONE
import com.example.commutual.model.Task.Companion.COMPLETION_YES
import com.example.commutual.ui.screens.item.*
import kotlinx.coroutines.launch
import com.example.commutual.R.string as AppText

@Composable
fun MessagesScreen(
    popUpScreen: () -> Unit,
    openScreen: (String) -> Unit,
    chatId: String,
    modifier: Modifier = Modifier,
    viewModel: MessageViewModel = hiltViewModel()
) {

//    val messages = remember { mutableStateListOf<Message>() }
    LaunchedEffect(Unit) {
        viewModel.getSender(chatId)
    }

    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val uiState by viewModel.uiState

    val messages by viewModel.getMessagesWithUsers(chatId).collectAsState(emptyList())

    val tasksWithUsers by viewModel.getTasksWithUsers(chatId)
        .collectAsState(Pair(emptyList(), emptyList()))
    val (completedTasks, upcomingTasks) = tasksWithUsers

    // Sort messages and tasks by timestamp
    val messagesWithTasks = (messages.map { Pair(it.first.timestamp, it) } +
            upcomingTasks.map { Pair(it.first.timestamp, it) } +
            completedTasks.map { Pair(it.first.timestamp, it) })
        .sortedBy { it.first }
        .map { it.second }


    LaunchedEffect(messages) {
        if (messages.isNotEmpty() && messagesWithTasks.isNotEmpty()) {
            scrollState.animateScrollToItem(messages.size - 1)
        }
    }

//    val currentUserId = viewModel.currentUserId
//    val partnerId = viewModel.chat.value.membersId.first { it != currentUserId }

    androidx.compose.material.Scaffold(

        floatingActionButton = {

            if (viewModel.tabIndex == 1) {
                ExtendedFloatingActionButton(
                    text = {
                        Text(
                            text = stringResource(R.string.add_task),
                            color = MaterialTheme.colorScheme.onTertiary,
                            style = MaterialTheme.typography.displayMedium
                        )
                    },
                    icon = {
                        androidx.compose.material.Icon(
                            Icons.Filled.Add, "Add",
                            tint = MaterialTheme.colorScheme.onTertiary
                        )
                    },
                    onClick = { viewModel.onAddTaskClick(openScreen) },
                    modifier = modifier.padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.tertiary,
                )
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
        ) {
            BasicToolbar(
                title = (stringResource(AppText.chat_with, uiState.partner.username))
            )

            TabRow(selectedTabIndex = viewModel.tabIndex) {
                viewModel.messageTabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = viewModel.tabIndex == index,
                        onClick = { viewModel.setMessageTab(index) },
                        text = {
                            Text(
                                text = stringResource(tab.tabStringRes),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )
                }
            }


            if (viewModel.tabIndex == 0) {

//            Chat Tab
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    state = scrollState,
                    contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp)
                ) {
                    items(messagesWithTasks) { (item, user) ->

                        when (item) {
                            is Message -> {
                                MessageItem(item, user)
                                { timestamp -> FormatterClass.formatTimestamp(timestamp, true) }
                            }
                            is Task -> {
                                CreatedTaskItem(item, user, viewModel::onCreatedTaskCLicked)
                                Log.d("item.showAttendance", item.showAttendance.toString())
                                Log.d(
                                    "item.attendance[viewModel.currentUserId] ==",
                                    (item.attendance[viewModel.currentUserId] == ATTENDANCE_NOT_DONE).toString()
                                )

                                if (item.showAttendance && (item.attendance[viewModel.currentUserId] == ATTENDANCE_NOT_DONE)) {
                                    AttendanceItem(
                                        task = item,
                                        onCLick = {
                                            viewModel.onAttendanceItemClicked()
                                        },
                                        onYesClick = {
                                            viewModel.onAttendanceYesClicked(
                                                item,
                                                chatId
                                            )
                                        },
                                        onNoClick = {
                                            viewModel.onAttendanceNoClicked(item, chatId)
                                        }
                                    )
                                }

                                if ((item.showAttendance) && (item.attendance[viewModel.currentUserId] == ATTENDANCE_YES)) {
                                    AttendanceYesItem(
                                        task = item,
                                        user = viewModel.currentUser.value,
                                        onCLick = {
                                            viewModel.onAttendanceItemClicked()
                                        })
                                } else if (item.attendance[viewModel.currentUserId] == ATTENDANCE_NO) {
                                    AttendanceNoItem(
                                        task = item,
                                        user = viewModel.currentUser.value,
                                        onCLick = {
                                            viewModel.onAttendanceItemClicked()

                                        })
                                }
                                if ((item.showAttendance) &&
                                    (item.attendance[viewModel.chat.value.membersId.first { it != viewModel.currentUserId }]
                                            == ATTENDANCE_YES)
                                ) {
                                    AttendanceYesItem(
                                        task = item,
                                        user = viewModel.partnerObject.value,
                                        onCLick = {
                                            viewModel.onAttendanceItemClicked()

                                        })
                                } else if ((item.showAttendance) && (item.attendance[viewModel.chat.value.membersId.first { it != viewModel.currentUserId }] == ATTENDANCE_NO)) {
                                    AttendanceNoItem(
                                        task = item,
                                        user = viewModel.partnerObject.value,
                                        onCLick = {
                                            viewModel.onAttendanceItemClicked()
                                        })
                                }
                                // completion
                                if (item.showCompletion && (item.completion[viewModel.currentUserId] == COMPLETION_NOT_DONE)) {
                                    CompletionItem(
                                        task = item,
                                        onCLick = {
                                            viewModel.onCompletionItemClicked()
                                        },
                                        onYesClick = {
                                            viewModel.onCompletionYesClicked(
                                                item,
                                                chatId
                                            )
                                        },
                                        onNoClick = {
                                            viewModel.onCompletionNoClicked(item, chatId)
                                        }
                                    )
                                }

                                if ((item.showCompletion) && (item.completion[viewModel.currentUserId] == COMPLETION_YES)) {
                                    CompletionYesItem(
                                        task = item,
                                        user = viewModel.currentUser.value,
                                        onCLick = {
                                            viewModel.onCompletionItemClicked()
                                        })
                                } else if (item.completion[viewModel.currentUserId] == COMPLETION_NO) {
                                    CompletionNoItem(
                                        task = item,
                                        user = viewModel.currentUser.value,
                                        onCLick = {
                                            viewModel.onCompletionItemClicked()

                                        })
                                }
                                if ((item.showCompletion) &&
                                    (item.completion[viewModel.chat.value.membersId.first { it != viewModel.currentUserId }]
                                            == COMPLETION_YES)
                                ) {
                                    CompletionYesItem(
                                        task = item,
                                        user = viewModel.partnerObject.value,
                                        onCLick = {
                                            viewModel.onCompletionItemClicked()
                                        })
                                } else if ((item.showCompletion) && (item.completion[viewModel.chat.value.membersId.first { it != viewModel.currentUserId }] == COMPLETION_NO)) {
                                    CompletionNoItem(
                                        task = item,
                                        user = viewModel.partnerObject.value,
                                        onCLick = {
                                            viewModel.onCompletionItemClicked()
                                        })
                                }


                            }
                        }

                    }
                }

                MessageInputField(
                    R.string.message,
                    uiState.messageText,
                    viewModel::onMessageTextChange,
                    {
                        Icon(
                            painter = painterResource(R.drawable.ic_home),
                            contentDescription = stringResource(R.string.send)
                        )
                    },
                    {
                        IconButton(
                            onClick = {

                                coroutineScope.launch {
                                    viewModel.onSendClick(chatId, focusManager)
                                    if (messages.isNotEmpty()) {
                                        scrollState.animateScrollToItem(messages.size - 1)
                                    }  else if (messagesWithTasks.isNotEmpty()) {
                                        scrollState.animateScrollToItem(tasksWithUsers.first.size - 1)
                                }
                                }
                            },
                            content = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_send),
                                    contentDescription = stringResource(R.string.send)
                                )
                            }
                        )
                    },
                    4,
                    Modifier
                        .padding(2.dp, 8.dp)
                        .fillMaxWidth(),
                    focusManager
                )

            } else {
                // Tasks Tab

                if (upcomingTasks.isNotEmpty()) {

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp)
                    ) {
                        item {
                            Text(
                                text = stringResource(R.string.upcoming_tasks)
                            )
                        }
                        items(upcomingTasks) { (upcomingTasks, creator) ->
                            TaskItem(task = upcomingTasks, creator = creator)
                        }
                    }
                }

                if (completedTasks.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.completed_tasks)
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp)
                    ) {
                        items(completedTasks) { (completedTasks, creator) ->
                            TaskItem(task = completedTasks, creator = creator)
                        }
                    }

                    Spacer(modifier = Modifier.padding(10.dp))

                }

            }
        }

    }
}


