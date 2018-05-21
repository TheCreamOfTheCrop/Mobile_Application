package ecalle.com.bmybank.firebase.bo

import java.io.Serializable


/**
 * @author thomasecalle
 * @since 2018.05.18
 */
data class ListMessages(
        val id: Int = 0,
        var messages: MutableList<Message> = mutableListOf()
) : Serializable
{
    override fun equals(other: Any?): Boolean
    {
        if (other?.javaClass != javaClass) return false

        other as ListMessages

        if (id != other.id) return false

        return true
    }

}