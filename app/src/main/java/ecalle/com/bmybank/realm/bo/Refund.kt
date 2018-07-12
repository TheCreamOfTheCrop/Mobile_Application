package ecalle.com.bmybank.realm.bo

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.Serializable

/**
 * Created by Thomas Ecalle on 14/03/2018.
 */
open class Refund(
        id: Int = 0,
        amount: Float = 0f,
        loan_id: Int = 0,
        creationdate: String = ""
) : RealmObject(), Serializable
{
    @PrimaryKey
    open var id: Int = 0
    open var amount: Float = 0f
    open var loan_id: Int = 0
    open var creationdate: String = ""

    init
    {
        this.id = id
        this.amount = amount
        this.loan_id = loan_id
        this.creationdate = creationdate
    }
}