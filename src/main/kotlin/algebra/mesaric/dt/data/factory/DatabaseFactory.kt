package algebra.mesaric.dt.data.factory

import algebra.mesaric.dt.data.model.Users
import algebra.mesaric.dt.data.model.dao.DAOFacade
import algebra.mesaric.dt.data.model.dao.DAOFacadeImpl
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*
import org.jetbrains.exposed.sql.transactions.experimental.*

object DatabaseFactory {
    fun init() {
        val driverClassName = "org.h2.Driver"
        val jdbcURL = "jdbc:h2:file:./build/db"
        val database = Database.connect(jdbcURL, driverClassName)
        transaction(database) {
            SchemaUtils.create(Users)
            val dao: DAOFacade = DAOFacadeImpl().apply {
                runBlocking {
                    if (allUsers().isNotEmpty()) {
                        deleteAllUsers()
                    }
                    loadMockData()
                }
            }
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}