package algebra.mesaric.dt

import algebra.mesaric.dt.data.factory.DatabaseFactory
import algebra.mesaric.dt.data.model.dao.DAOFacadeImpl
import algebra.mesaric.dt.plugins.*
import algebra.mesaric.dt.security.hashing.SHA256HashingService
import algebra.mesaric.dt.security.token.JwtTokenService
import algebra.mesaric.dt.security.token.TokenConfig
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
    val tokenService = JwtTokenService()
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 365L * 1000L * 60L * 60L * 24L,
        secret = System.getenv("JWT_SECRET")
    )

    val hashingService = SHA256HashingService()

    configureSockets()
    configureSecurity(tokenConfig)
    configureHTTP()
    configureSerialization()
    configureRouting(db, hashingService, tokenService, tokenConfig)
}


