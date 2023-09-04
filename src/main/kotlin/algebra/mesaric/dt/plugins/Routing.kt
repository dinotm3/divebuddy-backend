package algebra.mesaric.dt.plugins

import algebra.mesaric.dt.data.model.dao.DAOFacade
import algebra.mesaric.dt.routes.getAllUsers
import algebra.mesaric.dt.routes.getUser
import io.ktor.network.sockets.*
import io.ktor.server.routing.*
import io.ktor.server.http.content.*
import io.ktor.server.application.*

fun Application.configureRouting(db: DAOFacade) {

    routing {
        getUser(db)// Static plugin. Try to access `/static/index.html`
        getAllUsers(db)
        static("/static") {
            resources("static")
        }
    }
}

