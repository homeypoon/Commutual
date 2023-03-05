package com.example.commutual.ui.screens.chat

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.focus.FocusManager
import com.example.commutual.*
import com.example.commutual.model.*
import com.example.commutual.model.service.AccountService
import com.example.commutual.model.service.LogService
import com.example.commutual.model.service.StorageService
import com.example.commutual.ui.screens.CommutualViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


@HiltViewModel
class MessageViewModel @Inject constructor(
    logService: LogService,
    private val storageService: StorageService,
    private val accountService: AccountService

) : CommutualViewModel(logService = logService) {
    val message = mutableStateOf(Message())
    val sender = mutableStateOf(User())
    val partnerObject = mutableStateOf(User())
    val chat = mutableStateOf(Chat())

    val messageTabs = enumValues<MessageTabEnum>()

//    var tasks = storageService.tasks


    fun getMessagesWithUsers(chatId: String): Flow<List<Pair<Message, User>>> {
        return storageService.getMessagesWithUsers(chatId)
    }

    fun getTasksWithUsers(chatId: String): Flow<Pair<List<Pair<Task, User>>, List<Pair<Task, User>>>> {
        return storageService.getTasksWithUsers(chatId)
    }

    var uiState = mutableStateOf(MessageUiState())
        private set

    val tabIndex
        get() = uiState.value.tabIndex

    private val messageText
        get() = uiState.value.messageText

    private val partnerName
        get() = uiState.value.partner


    fun getSender(chatId: String) {
        launchCatching {

            chat.value = storageService.getChatWithChatId(chatId) ?: Chat()

            partnerObject.value = storageService.getPartner(chat.value.membersId) ?: User()

            uiState.value = uiState.value.copy(
                partner = partnerObject.value
            )
        }
    }

    fun setMessageTab(tabIndex: Int) {
        uiState.value = uiState.value.copy(tabIndex = tabIndex)
    }

    fun resetMessageText() {
        uiState.value = uiState.value.copy(messageText = "")
    }

    fun onMessageTextChange(newValue: String) {
        message.value = message.value.copy(text = newValue)
        uiState.value = uiState.value.copy(messageText = newValue)
    }



    suspend fun onSendClick(chatId: String, focusManager: FocusManager) {

        // Close keyboard
        focusManager.clearFocus()

        // If the user didn't input text for the message, don't save or send the message
        if (messageText.isBlank()) {
            return
        }

        storageService.saveMessage(message.value, chatId)

        resetMessageText()
    }

    fun onAddTaskClick(openScreen: (String) -> Unit) {
        openScreen("$EDIT_TASK_SCREEN?$TASK_ID=$TASK_DEFAULT_ID?$CHAT_ID=${chat.value.chatId}?$SCREEN_TITLE=${ST_CREATE_TASK}")
    }
}