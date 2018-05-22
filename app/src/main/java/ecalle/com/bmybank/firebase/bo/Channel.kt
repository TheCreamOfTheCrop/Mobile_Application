package ecalle.com.bmybank.firebase.bo

import java.io.Serializable


/**
 * @author thomasecalle
 * @since 2018.05.18
 */
data class Channel(
        val id_loan: Int? = null,
        val id_user_1: Int = 0,
        val id_user_2: Int = 0,
        val list_messages_id: Int = -1,
        var last_message: String = ""
) : Serializable