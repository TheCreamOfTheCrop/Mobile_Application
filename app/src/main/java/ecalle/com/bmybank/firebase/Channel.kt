package ecalle.com.bmybank.firebase


/**
 * @author thomasecalle
 * @since 2018.05.18
 */
data class Channel(
    val id: Int = 0,
    val id_loan: Int? = null,
    val id_user_1: Int = 0,
    val id_user_2: Int = 0,
    val messages: List<Message> = emptyList()
)