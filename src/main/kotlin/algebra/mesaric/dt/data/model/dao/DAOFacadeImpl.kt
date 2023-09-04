package algebra.mesaric.dt.data.model.dao

import algebra.mesaric.dt.data.factory.DatabaseFactory.dbQuery
import algebra.mesaric.dt.data.model.User
import algebra.mesaric.dt.data.model.Users
import org.jetbrains.exposed.sql.*

class DAOFacadeImpl : DAOFacade {
    private fun resultRowToUser(row: ResultRow) = User(
        id = row[Users.id],
        name = row[Users.name],
        email = row[Users.email],
        country = row[Users.country],
        lat = row[Users.lat],
        lng = row[Users.lng],
    )

    override suspend fun allUsers(): List<User> = dbQuery {
        Users.selectAll().map (::resultRowToUser)
    }

    override suspend fun getUserById(id: Int): User? = dbQuery {
        Users
            .select { Users.id eq id }
            .map (::resultRowToUser)
            .singleOrNull()
        }

    override suspend fun insertUser(
        name: String, email: String, country: String, lat: Double, lng: Double): User? = dbQuery {
        val insertStatement = Users.insert {
            it[Users.name] = name
            it[Users.email] = email
            it[Users.country] = country
            it[Users.lng] = lng
            it[Users.lat] = lat
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToUser)
    }

    override suspend fun deleteAllUsers() {
        Users.deleteAll()
    }

    override suspend fun loadMockData() {
        insertUser("Test name 1", "test1@gmail.com", "Croatia", 43.384066, 16.532069)
        insertUser("Test name 2", "test1@gmail.com", "Croatia", 43.38348108634552, 16.557709026017527)
        insertUser("Test name 3", "test1@gmail.com", "Croatia", 43.50859051462336, 16.45875606914964)
        insertUser("Test name 4", "test1@gmail.com", "Croatia", 43.1667548759157, 16.620425068405844)
        insertUser("Test name 5", "test1@gmail.com", "Croatia", 43.60937046452259, 15.93716396642017)
    }
}

