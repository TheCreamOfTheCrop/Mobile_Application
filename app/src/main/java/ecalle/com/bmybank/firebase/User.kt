package ecalle.com.bmybank.firebase


/**
 * @author thomasecalle
 * @since 2018.05.18
 */
data class User(
    val id: Int = 0,
    val channels: List<Int> = emptyList()
)