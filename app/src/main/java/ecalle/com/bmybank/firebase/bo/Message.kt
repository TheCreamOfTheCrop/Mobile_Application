package ecalle.com.bmybank.firebase.bo

import com.squareup.moshi.Json
import java.io.Serializable


/**
 * @author thomasecalle
 * @since 2018.05.18
 */
data class Message(
        @Json(name = "text") val text: String = "",
        @Json(name = "id_sender") val id_sender: Int = 0,
        @Json(name = "date") val date: String = "",
        @Json(name = "senderName") val senderName: String = ""
) : Serializable
{
    override fun equals(other: Any?): Boolean
    {
        if (other?.javaClass != javaClass) return false

        other as Message

        if (text != other.text) return false
        if (id_sender != other.id_sender) return false
        if (date != other.date) return false

        return true
    }
}