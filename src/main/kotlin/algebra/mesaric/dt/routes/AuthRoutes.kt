package algebra.mesaric.dt.routes

import algebra.mesaric.dt.data.model.User
import algebra.mesaric.dt.data.model.Users
import algebra.mesaric.dt.data.model.dao.DAOFacade
import algebra.mesaric.dt.data.model.dao.DAOFacadeImpl
import algebra.mesaric.dt.data.requests.AuthRequest
import algebra.mesaric.dt.data.responses.AuthResponse
import algebra.mesaric.dt.security.hashing.HashingService
import algebra.mesaric.dt.security.hashing.SaltedHash
import algebra.mesaric.dt.security.token.JwtTokenService
import algebra.mesaric.dt.security.token.TokenClaim
import algebra.mesaric.dt.security.token.TokenConfig
import algebra.mesaric.dt.security.token.TokenService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.signUp(
    hashingService: HashingService,
    db: DAOFacade,
) {
    post("/signup") {
        val request = call.receiveOrNull<AuthRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val areFieldsBlank = request.username.isBlank() || request.password.isBlank()
        val isPwTooShort = request.password.length < 8
        if (areFieldsBlank || isPwTooShort) {
            call.respond(HttpStatusCode.Conflict)
            return@post
        }

        val saltedHash = hashingService.generateSaltedHash(request.password)

        val wasAcknowledged = db.insertUser(
            request.username,
            "test@email.com",
            "Test",
            43.38348108634552,
            16.557709026017527,
            25,
            saltedHash.salt,
            saltedHash.hash
        )
    }
}

fun Route.signIn(
    hashingService: HashingService,
    db: DAOFacade,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    post("/signin") {
        val request = call.receiveOrNull<AuthRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val user = db.getUserByUsername(request.username)
        if (user == null) {
            call.respond(HttpStatusCode.Conflict, "Incorrect username or password")
            return@post
        }

        val isValidPassword = hashingService.verify(
            value = request.password,
            saltedHash = SaltedHash(
                hash = user.password,
                salt = user.salt
            )
        )

        if (!isValidPassword) {
            call.respond(HttpStatusCode.Conflict, "Incorrect username or password")
            return@post
        }

        val token = tokenService.generate(
            config = tokenConfig,
            TokenClaim(
                name = "userId",
                value = user.id.toString()
            )
        )

        call.respond(
            status = HttpStatusCode.OK,
            message = AuthResponse(
                token = token
            )
        )
    }
}

fun Route.authenticate() {
    authenticate() {
        get("authenticate") {
            call.respond(HttpStatusCode.OK)
        }
    }
}

fun Route.getSecretInfo() {
    authenticate {
        get("secret") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            call.respond(HttpStatusCode.OK, "Your userId is $userId")
        }
    }
}