package algebra.mesaric.dt.plugins

import algebra.mesaric.dt.data.model.dao.DAOFacade
import algebra.mesaric.dt.routes.*
import algebra.mesaric.dt.security.hashing.HashingService
import algebra.mesaric.dt.security.token.TokenConfig
import algebra.mesaric.dt.security.token.TokenService
import io.ktor.network.sockets.*
import io.ktor.server.routing.*
import io.ktor.server.http.content.*
import io.ktor.server.application.*

fun Application.configureRouting(
    db: DAOFacade,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {

    routing {
        getUser(db)// Static plugin. Try to access `/static/index.html`
        getAllUsers(db)
        static("/static") {
            resources("static")
        }
        signIn(hashingService, db, tokenService, tokenConfig)
        signUp(hashingService, db)
        authenticate()
        getSecretInfo()
    }
}

