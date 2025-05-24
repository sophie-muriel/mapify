package com.mapify.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mapify.model.Conversation
import com.mapify.model.Message
import com.mapify.model.Participant
import com.mapify.model.User
import com.mapify.utils.RequestResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.util.UUID

class ConversationsViewModel(private val usersViewModel: UsersViewModel) : ViewModel() {

    private val db = Firebase.firestore

    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations

    private val _recentSearches = mutableStateListOf<User>()
    val recentSearches: List<User> get() = _recentSearches

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _conversationResult = MutableStateFlow<RequestResult?>(null)
    val conversationResult: StateFlow<RequestResult?> = _conversationResult.asStateFlow()

    init {
        getConversations()
    }

    private fun create(conversation: Conversation) {
        viewModelScope.launch {
            _conversationResult.value = RequestResult.Loading
            _conversationResult.value = runCatching { createFirebase(conversation) }
                .fold(
                    onSuccess = { RequestResult.Success("Conversation created successfully") },
                    onFailure = {
                        Log.e("CreateConversation", "Failed to create: ${it.message}", it)
                        RequestResult.Failure(it.message ?: "Error creating conversation")
                    }
                )
        }
    }

    private suspend fun createFirebase(conversation: Conversation) {
        val conversationRef = db.collection("conversations").document()
        val conversationId = conversationRef.id

        val conversationCopy = conversation.copy(id = conversationId)

        val conversationMap = mapOf(
            "id" to conversationCopy.id,
            "participants" to conversationCopy.participants.map { participant ->
                mapOf(
                    "id" to participant.id,
                    "name" to participant.name
                )
            },
            "messages" to conversationCopy.messages.map { message ->
                mapOf(
                    "id" to message.id,
                    "senderId" to message.senderId,
                    "content" to message.content,
                    "timestamp" to message.timestamp
                )
            },
            "isRead" to conversationCopy.isRead
        )

        conversationRef.set(conversationMap).await()
    }

    fun createConversation(sender: Participant, recipient: Participant): Conversation {
        val newConversation = Conversation(
            participants = listOf(
                Participant(sender.id, sender.name),
                Participant(recipient.id, recipient.name)
            ),
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

    private suspend fun findAllFirebase(): List<Conversation> {
        val formatter = java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME

        val query = db.collection("conversations").get().await()

        return query.documents.mapNotNull { doc ->
            val data = doc.data ?: return@mapNotNull null
            val messagesRaw = data["messages"] as? List<Map<String, Any?>> ?: emptyList()
            val messages = messagesRaw.mapNotNull { msg ->
                try {
                    Message(
                        id = msg["id"] as String,
                        senderId = msg["senderId"] as String,
                        senderName = msg["senderName"] as String,
                        content = msg["content"] as String,
                        timestamp = LocalDateTime.parse(msg["timestamp"] as String, formatter)
                    )
                } catch (e: Exception) {
                    null
                }
            }

            val participants = (data["participants"] as? List<Map<String, String>>)?.map {
                Participant(it["id"]!!, it["name"]!!)
            } ?: emptyList()

            val isReadMap =
                (data["isRead"] as? Map<String, Boolean>)?.toMutableMap() ?: mutableMapOf()
            val deletedForList = data["deletedFor"] as? MutableList<String> ?: mutableListOf()

            Conversation(
                id = doc.id,
                participants = participants,
                messages = messages,
                isRead = isReadMap,
                deletedFor = deletedForList
            )
        }
    }


    private suspend fun updateFirebaseConversation(conversation: Conversation) {
        val conversationMap = mapOf(
            "id" to conversation.id,
            "participants" to conversation.participants.map {
                mapOf("id" to it.id, "name" to it.name)
            },
            "messages" to conversation.messages.map {
                mapOf(
                    "id" to it.id,
                    "senderId" to it.senderId,
                    "senderName" to it.senderName,
                    "content" to it.content,
                    "timestamp" to it.timestamp.toString()
                )
            },
            "isRead" to conversation.isRead
        )
        db.collection("conversations").document(conversation.id).set(conversationMap).await()
    }

    fun getMessages(conversationId: String) {
        val conversation = find(conversationId)
        _messages.value = conversation?.messages?.reversed() ?: emptyList()
    }

    fun sendMessage(conversationId: String, senderId: String, senderName: String, content: String) {
        viewModelScope.launch {
            val newMessage = Message(
                id = UUID.randomUUID().toString(),
                senderId = senderId,
                senderName = senderName,
                content = content,
                timestamp = LocalDateTime.now()
            )

            val conversation = find(conversationId) ?: return@launch

            val updatedIsRead = conversation.isRead.toMutableMap().apply {
                conversation.participants
                    .map { it.id }
                    .filter { it != senderId }
                    .forEach { this[it] = false }
            }

            val updatedConversation = conversation.copy(
                messages = conversation.messages + newMessage,
                isRead = updatedIsRead
            )

            updateFirebaseConversation(updatedConversation)

            _conversations.update { list ->
                list.map { if (it.id == conversationId) updatedConversation else it }
            }
            getMessages(conversationId)
        }
    }

    fun deleteForUser(conversationId: String, userId: String) {
        _conversations.update { currentList ->
            currentList.map { conversation ->
                if (conversation.id == conversationId) {
                    val updatedDeletedFor = conversation.deletedFor.toMutableList()
                    if (!updatedDeletedFor.contains(userId)) {
                        updatedDeletedFor.add(userId)
                    }
                    conversation.copy(deletedFor = updatedDeletedFor)
                } else {
                    conversation
                }
            }
        }

        viewModelScope.launch {
            db.collection("conversations").document(conversationId)
                .update("deletedFor", FieldValue.arrayUnion(userId))
                .await()
        }
    }

    fun markAsRead(id: String, userId: String) {
        val conversation = find(id) ?: return
        val updatedIsRead = conversation.isRead.toMutableMap().apply {
            this[userId] = true
        }
        val updatedConversation = conversation.copy(isRead = updatedIsRead)

        _conversations.update { currentList ->
            currentList.map {
                if (it.id == id) updatedConversation else it
            }
        }

        viewModelScope.launch {
            db.collection("conversations")
                .document(id)
                .update("isRead.${userId}", true)
                .await()
        }
    }

    fun markAsUnread(id: String, userId: String) {
        val conversation = find(id) ?: return
        val updatedIsRead = conversation.isRead.toMutableMap().apply {
            this[userId] = false
        }
        val updatedConversation = conversation.copy(isRead = updatedIsRead)

        _conversations.update { currentList ->
            currentList.map {
                if (it.id == id) updatedConversation else it
            }
        }

        viewModelScope.launch {
            db.collection("conversations")
                .document(id)
                .update("isRead.${userId}", false)
                .await()
        }
    }

    fun observeMessages(conversationId: String) {
        db.collection("conversations")
            .document(conversationId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("Firestore", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val data = snapshot.data
                    val messagesRaw =
                        data?.get("messages") as? List<Map<String, Any?>> ?: emptyList()
                    val formatter = java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME

                    val messages = messagesRaw.mapNotNull { msg ->
                        try {
                            Message(
                                id = msg["id"] as String,
                                senderId = msg["senderId"] as String,
                                senderName = msg["senderName"] as String,
                                content = msg["content"] as String,
                                timestamp = LocalDateTime.parse(
                                    msg["timestamp"] as String,
                                    formatter
                                )
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }

                    _messages.value = messages.reversed()
                } else {
                    Log.d("Firestore", "Current data: null")
                }
            }
    }

    private var conversationsListener: ListenerRegistration? = null

    fun observeAllConversations(userId: String) {
        conversationsListener?.remove()

        conversationsListener = db.collection("conversations")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("Firestore", "Listen failed for conversations.", e)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val formatter = java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME

                    val conversationsList = snapshots.documents.mapNotNull { doc ->
                        val data = doc.data ?: return@mapNotNull null

                        val messagesRaw =
                            data["messages"] as? List<Map<String, Any?>> ?: emptyList()
                        val messages = messagesRaw.mapNotNull { msg ->
                            try {
                                Message(
                                    id = msg["id"] as String,
                                    senderId = msg["senderId"] as String,
                                    senderName = msg["senderName"] as String,
                                    content = msg["content"] as String,
                                    timestamp = LocalDateTime.parse(
                                        msg["timestamp"] as String,
                                        formatter
                                    )
                                )
                            } catch (e: Exception) {
                                null
                            }
                        }

                        val participants =
                            (data["participants"] as? List<Map<String, String>>)?.map {
                                Participant(it["id"]!!, it["name"]!!)
                            } ?: emptyList()

                        val isReadMap = (data["isRead"] as? Map<String, Boolean>)?.toMutableMap()
                            ?: mutableMapOf()
                        val deletedForList =
                            data["deletedFor"] as? MutableList<String> ?: mutableListOf()

                        Conversation(
                            id = doc.id,
                            participants = participants,
                            messages = messages,
                            isRead = isReadMap,
                            deletedFor = deletedForList
                        )
                    }

                    _conversations.value = conversationsList
                }
            }
    }

    fun stopObservingConversations() {
        conversationsListener?.remove()
    }


    private fun getConversations() {
        viewModelScope.launch {
            _conversations.value = findAllFirebase()
        }
    }

    fun getVisibleConversationsForUser(userId: String): List<Conversation> {
        return _conversations.value.filter { !it.deletedFor.contains(userId) }
    }
}