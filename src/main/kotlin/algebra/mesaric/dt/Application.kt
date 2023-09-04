package algebra.mesaric.dt

import algebra.mesaric.dt.data.factory.DatabaseFactory
import algebra.mesaric.dt.data.model.dao.DAOFacadeImpl
import algebra.mesaric.dt.plugins.*
import io.ktor.client.plugins.websocket.*
import io.ktor.network.sockets.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.util.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    DatabaseFactory.init()
    // DB is initialized so I do not need supply anything to the function
    val db = DAOFacadeImpl()
    configureSockets()
    configureSecurity()
    configureHTTP()
    configureSerialization()
    configureRouting(db)
}


