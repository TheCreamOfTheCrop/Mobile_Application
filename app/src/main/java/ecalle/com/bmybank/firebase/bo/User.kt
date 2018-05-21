package ecalle.com.bmybank.firebase.bo

import java.io.Serializable


/**
 * @author thomasecalle
 * @since 2018.05.18
 */
data class User(
        val id: Int = 0,
        var channels: List<Int> = emptyList()
) : Serializable