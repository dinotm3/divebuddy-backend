package algebra.mesaric.dt.security.token

import org.h2.command.Token

interface TokenService {
    fun generate(
        config: TokenConfig,
        vararg claims: TokenClaim
    ): String
}