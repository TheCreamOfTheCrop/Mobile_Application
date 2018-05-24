package ecalle.com.bmybank.firebase.bo

import com.squareup.moshi.Json
import java.io.Serializable


/**
 * @author thomasecalle
 * @since 2018.05.18
 */
data class Channel(
        @Json(name = "id_loan") val id_loan: Int? = null,
        @Json(name = "id_user_1") val id_user_1: Int = 0,
        @Json(name = "id_user_2") val id_user_2: Int = 0,
        @Json(name = "list_messages_id") val list_messages_id: Int = -1,
        @Json(name = "last_message") var last_message: String = ""
) : Serializable