package ecalle.com.bmybank.firebase.bo

import java.io.Serializable


/**
 * @author thomasecalle
 * @since 2018.05.18
 */
data class Message(
        val text: String = "",
        val id_sender: Int = 0,
        val date: String = ""
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