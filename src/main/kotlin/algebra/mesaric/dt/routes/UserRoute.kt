package algebra.mesaric.dt.routes

import algebra.mesaric.dt.data.model.dao.DAOFacade
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*



fun Route.getUser(db: DAOFacade) {
    get("/user") {
        //call.parameters["userId"]
        call.respond(
            HttpStatusCode.OK,
            db.allUsers().random()
        )
    }
}

fun Route.getAllUsers(db: DAOFacade) {
    get("/users") {
        //call.parameters["userId"]
        call.respond(
            HttpStatusCode.OK,
            db.allUsers()
        )
    }
}