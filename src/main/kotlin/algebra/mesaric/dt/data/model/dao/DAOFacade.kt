package algebra.mesaric.dt.data.model.dao

import algebra.mesaric.dt.data.model.User
import org.jetbrains.exposed.sql.ResultRow

interface DAOFacade {
    suspend fun allUsers(): List<User>
    suspend fun getUserById(id: Int): User?
    suspend fun insertUser(name: String, email: String, country: String, lat: Double, lng: Double, depth: Int, password: String, salt: String): User?
    suspend fun deleteAllUsers()
    suspend fun loadMockData()
    suspend fun getUserByUsername(username: String): User?

}