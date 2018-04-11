package ecalle.com.bmybank.realm.bo

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.Serializable

/**
 * Created by Thomas Ecalle on 11/04/2018.
 */
open class Negociation(
        id: Int = 0,
        uid: String = "",
        id_loan: Int = 0,
        id_user_negociate: Int = 0,
        amount: Float = 0F,
        rate: Float = 0F,
        delay: Int = 0
) : RealmObject(), Serializable
{
    @PrimaryKey
    open var id: Int = 0
    open var uid: String = ""
    open var id_loan: Int = 0
    open var id_user_negociate: Int = 0
    open var amount: Float = 0F
    open var rate: Float = 0F
    open var user_requester_id: Int = 0
    open var delay: Int = 0


    init
    {
        this.id = id
        this.uid = uid
        this.amount = amount
        this.id_loan = id_loan
        this.id_user_negociate = id_user_negociate
        this.rate = rate
        this.user_requester_id = user_requester_id
        this.delay = delay
    }
}