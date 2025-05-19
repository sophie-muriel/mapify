package com.mapify.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.mapify.model.Conversation
import com.mapify.model.Message
import com.mapify.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime
import java.util.UUID

class ConversationsViewModel(private val usersViewModel: UsersViewModel) : ViewModel() {

    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations

    private val _recentSearches = mutableStateListOf<User>()
    val recentSearches: List<User> get() = _recentSearches

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    init {
        _conversations.value = getConversations()
    }

    private fun create(conversation: Conversation) {
        _conversations.update { currentList -> currentList + conversation }
    }

    fun createConversation(sender: User, recipient: User): Conversation {
        val newConversation = Conversation(
            id = UUID.randomUUID().toString(),
            participants = listOf(sender, recipient),
            messages = emptyList(),
            isRead = mutableMapOf(sender.id to true, recipient.id to false)
        )
        create(newConversation)
        return newConversation
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun addRecentSearch(user: User) {
        if (!_recentSearches.any { it.id == user.id }) {
            _recentSearches.add(0, user)
            if (_recentSearches.size > 10) {
                _recentSearches.removeLast()
            }
        }
    }

    fun find(id: String): Conversation? {
        return _conversations.value.find { it.id == id }
    }

    fun deleteForUser(conversationId: String, userId: String) {
        _conversations.update { currentList ->
            currentList.map { conversation ->
                if (conversation.id == conversationId) {
                    if (conversation.participants.any { it.id == userId }) {
                        conversation.copy(
                            messages = emptyList(),
                            participants = conversation.participants.filter { it.id != userId }
                        )
                    } else {
                        conversation
                    }
                } else {
                    conversation
                }
            }
        }
    }

    fun getMessages(conversationId: String) {
        val conversation = find(conversationId)
        _messages.value = conversation?.messages?.reversed() ?: emptyList()
    }

    fun sendMessage(conversationId: String, senderId: String, content: String) {
        _conversations.update { currentList ->
            currentList.map { conversation ->
                if (conversation.id == conversationId) {
                    val newMessage = Message(
                        id = UUID.randomUUID().toString(),
                        senderId = senderId,
                        content = content,
                        timestamp = LocalDateTime.now()
                    )
                    conversation.copy(messages = conversation.messages + newMessage)
                } else {
                    conversation
                }
            }
        }
        getMessages(conversationId)
    }

    fun markAsRead(id: String, userId: String) {
        val conversation = find(id)!!
        val updatedConversation = conversation.copy(
            isRead = conversation.isRead.toMutableMap().apply {
                this[userId] = true
            }
        )

        _conversations.update { currentList ->
            currentList.map { existingConversation ->
                if (existingConversation.id == conversation.id) updatedConversation
                else existingConversation
            }
        }
    }

    fun markAsUnread(id: String, userId: String) {
        val conversation = find(id)!!
        val updatedConversation = conversation.copy(
            isRead = conversation.isRead.toMutableMap().apply {
                this[userId] = false
            }
        )

        _conversations.update { currentList ->
            currentList.map { existingConversation ->
                if (existingConversation.id == conversation.id) updatedConversation
                else existingConversation
            }
        }
    }


    private fun getConversations(): List<Conversation> {
        val allUsers = usersViewModel.users.value

        return listOf(
//            Conversation(
//                id = UUID.randomUUID().toString(),
//                participants = listOf(allUsers[1], allUsers[0]),
//                messages = listOf(
//                    Message(
//                        id = "msg1",
//                        senderId = allUsers[0].id,
//                        content = "Hi, just checking if there are any updates on the report.",
//                        timestamp = LocalDateTime.now().minusMinutes(5)
//                    )
//                ),
//                isRead = mutableMapOf(
//                    allUsers[0].id to true,
//                    allUsers[1].id to false
//                )
//            ),
//            Conversation(
//                id = UUID.randomUUID().toString(),
//                participants = listOf(allUsers[2], allUsers[0]),
//                messages = listOf(
//                    Message(
//                        id = "msg2",
//                        senderId = allUsers[2].id,
//                        content = "Thanks for your response.",
//                        timestamp = LocalDateTime.now().minusHours(2)
//                    )
//                ),
//                isRead = mutableMapOf(
//                    allUsers[2].id to true,
//                    allUsers[0].id to false
//                )
//            ),
//            Conversation(
//                id = UUID.randomUUID().toString(),
//                participants = listOf(allUsers[1], allUsers[2]),
//                messages = listOf(
//                    Message(
//                        id = "msg3",
//                        senderId = allUsers[2].id,
//                        content = "Could you take a look at the file I sent you?",
//                        timestamp = LocalDateTime.now().minusDays(5)
//                    )
//                ),
//                isRead = mutableMapOf(
//                    allUsers[2].id to true,
//                    allUsers[1].id to false
//                )
//            )
        )
    }
}