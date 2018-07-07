package ecalle.com.bmybank.realm.bo

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.Serializable

/**
 * Created by Thomas Ecalle on 11/04/2018.
 */
open class Note(
        id: Int = 0,
        note: Float = 0f,
        comments: String = "",
        user_requester_id: Int = -1,
        user_provider_id: Int = -1
) : RealmObject(), Serializable
{
    @PrimaryKey
    open var id: Int = 0
    open var note: Float = 0f
    open var comments: String = ""
    open var user_requester_id: Int = -1
    open var user_provider_id: Int = -1


    init
    {
        this.id = id
        this.note = note
        this.comments = comments
        this.user_requester_id = user_requester_id
        this.user_provider_id = user_provider_id
    }
}