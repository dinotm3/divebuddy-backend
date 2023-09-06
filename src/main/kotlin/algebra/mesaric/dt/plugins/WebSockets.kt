package algebra.mesaric.dt.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.Duration

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {

        // I am using MutableList because each chat will have at least 2 users which means at least 2 websocket sessions
        val activeChatSessions = mutableMapOf<String, MutableList<DefaultWebSocketServerSession>>()
        val chatHistories = mutableMapOf<String, MutableList<String>>()

        webSocket("/chat/{user1Id}/{user2Id}") {
            val user1Id = call.parameters["user1Id"] ?: ""
            val user2Id = call.parameters["user2Id"] ?: ""

            if (user1Id.isNotEmpty() && user2Id.isNotEmpty()) {
                val chatId = generateUniqueChatId(user1Id, user2Id)
                val chatSession = checkChatExistence(chatId, activeChatSessions)

                if (chatSession != null) {

                    val chatHistory = chatHistories[chatId]
                    send("Server: You are connected!")
                    send("History: " + chatHistory.toString())
                    for (frame in incoming) {
                        frame as? Frame.Text ?: continue
                        val receivedText = frame.readText()
                        send("User: $receivedText")
                        chatHistory?.add("User: $receivedText")
                    }
                }

                if (chatSession == null) {

                    addToActiveChatSessions(chatId, activeChatSessions, createNewChatSession())
                    val chatHistory = createChatHistory(chatId, chatHistories)
                    send("Server: You are connected to a new chat!")
                    for (frame in incoming) {
                        frame as? Frame.Text ?: continue
                        val receivedText = frame.readText()
                        send("User: $receivedText")
                        chatHistory?.add("User: $receivedText")
                    }
                } else {
                    close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Invalid parameters"))
                }
            }
        }
    }
}

private fun addToActiveChatSessions(
    chatId: String,
    activeChatSessions: MutableMap<String, MutableList<DefaultWebSocketServerSession>>,
    newChatSession: MutableList<DefaultWebSocketServerSession>
): MutableList<DefaultWebSocketServerSession>?
{
    activeChatSessions[chatId] = newChatSession
    return activeChatSessions[chatId]
}

private fun createChatHistory(chatId: String, chatHistories: MutableMap<String, MutableList<String>>): MutableList<String>? {
    chatHistories[chatId] = mutableListOf()
    return chatHistories[chatId]
}

private fun generateUniqueChatId(user1Id: String, user2Id: String): String {
    // Sort the user IDs to ensure consistent chat IDs regardless of the order
    val sortedUserIds = listOf(user1Id, user2Id).sorted()

    // Combine the sorted user IDs to create a unique chat ID
    return "${sortedUserIds[0]}_${sortedUserIds[1]}"
}

private fun checkChatExistence(chatId: String, activeChatSessions: MutableMap<String, MutableList<DefaultWebSocketServerSession>>): MutableList<DefaultWebSocketServerSession>? {
    return activeChatSessions[chatId]
}

private fun createNewChatSession(): MutableList<DefaultWebSocketServerSession> {
    return mutableListOf()
}