package algebra.mesaric.dt.data.model

import io.ktor.server.locations.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
@Location("/chat/{chatId}")
data class Chat(
    val chatId: Int,
    val userId: Int,
    )

object Chats : Table() {
    val chatId = integer("id").autoIncrement()
    val userId = integer("userId")
}