package algebra.mesaric.dt.plugins

import algebra.mesaric.dt.security.token.TokenConfig
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.response.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.http.*
import io.ktor.server.sessions.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureSecurity(config: TokenConfig) {

    authentication {
        jwt {
            realm = this@configureSecurity.environment.config.property("jwt.realm").getString()
            verifier(
                    JWT
                            .require(Algorithm.HMAC256(config.secret))
                            .withAudience(config.audience)
                            .withIssuer(config.issuer)
                            .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(config.audience))
                    JWTPrincipal(credential.payload) else null
            }
        }
    }
}

