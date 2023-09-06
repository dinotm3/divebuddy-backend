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

        val activeChatSessions = mutableMapOf<String, MutableList<DefaultWebSocketServerSession>>()
        val chatHistories = mutableMapOf<String, MutableList<String>>()

        webSocket("/chat/{user1Id}/{user2Id}") {
            val user1Id = call.parameters["user1Id"] ?: ""
            val user2Id = call.parameters["user2Id"] ?: ""

            if (user1Id.isNotEmpty() && user2Id.isNotEmpty()) {
                val chatId = generateUniqueChatId(user1Id, user2Id)
                val chatSession = checkChatExistence(chatId, activeChatSessions)

                if (chatSession != null) {
                    // Retrieve and send chat history if available
                    val chatHistory = chatHistories[chatId]
                    send("History: " + chatHistory.toString())
                    send("Server: You are connected!")

                    for (frame in incoming) {
                        frame as? Frame.Text ?: continue
                        val receivedText = frame.readText()
                        send("User: $receivedText")
                        chatHistory?.add("User: $receivedText")
                    }
                }
                if (chatSession == null) {
                    // Create a new chat session
                    val newChatSession = mutableListOf<DefaultWebSocketServerSession>()

                    // Initialize chat history for the new chat session
                    chatHistories[chatId] = mutableListOf()

                    // Add the new chat session to the list of active sessions
                    activeChatSessions[chatId] = newChatSession
                    val chatHistory = chatHistories[chatId]
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
private fun generateUniqueChatId(user1Id: String, user2Id: String): String {
    // Sort the user IDs to ensure consistent chat IDs regardless of the order
    val sortedUserIds = listOf(user1Id, user2Id).sorted()

    // Combine the sorted user IDs to create a unique chat ID
    return "${sortedUserIds[0]}_${sortedUserIds[1]}"
}

private fun checkChatExistence(chatId: String, activeChatSessions: MutableMap<String, MutableList<DefaultWebSocketServerSession>>): MutableList<DefaultWebSocketServerSession>? {
    return activeChatSessions[chatId]
}

private fun createNewChatSession(chatId: String, activeChatSessions: MutableMap<String, String>) {
    // You can perform additional setup or initialization for the chat session here
    // For example, store the chat session in a database for persistence


    // Store the chat session in the map (in-memory example)
    activeChatSessions[chatId] = "Chat session data, if needed"
}