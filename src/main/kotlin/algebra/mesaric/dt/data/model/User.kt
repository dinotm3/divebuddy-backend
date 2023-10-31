package algebra.mesaric.dt.data.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.Table

@Serializable
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val country: String,
    var lat: Double,
    var lng: Double,
    var depth: Int,
    val password: String,
    val salt: String
)

object Users : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 125)
    val email = varchar("email", 125)
    val country = varchar("country", 125)
    var lat = double("latitude")
    var lng = double("longitude")
    val depth = integer("depth")
    val password = varchar("password", 125)
    val salt = varchar("salt", 125)

    override val primaryKey = PrimaryKey(id)
}