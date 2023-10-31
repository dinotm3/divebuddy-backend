package algebra.mesaric.dt.security.hashing

data class SaltedHash(
    val hash: String,
    val salt: String
)
