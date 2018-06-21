package ecalle.com.bmybank.realm.bo

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.Serializable

/**
 * Created by Thomas Ecalle on 04/04/2018.
 */
open class Loan(
        uid: String = "",
        creationdate: String = "",
        id: Int = 0,
        amount: Float = 0F,
        totalRefunded: Float = 0F,
        description: String = "",
        rate: Float = 0F,
        loan_type: String = "",
        state_id: String = "",
        user_requester_id: Int = 0,
        user_provider_id: Int? = null,
        delay: Int = 0
) : RealmObject(), Serializable
{
    @PrimaryKey
    open var id: Int = 0
    open var uid: String = ""
    open var creationdate: String = ""
    open var amount: Float = 0F
    open var totalRefunded: Float = 0F
    open var rate: Float = 0F
    open var description: String? = null
    open var loan_type: String = ""
    open var state_id: String = ""
    open var user_requester_id: Int = 0
    open var user_provider_id: Int? = null
    open var delay: Int = 0

    enum class LOANTYPE(type: String)
    {
        PUBLIC("public"),
        PRIVATE("private"),
    }

    enum class STATE_ID(type: String)
    {
        PENDING("en attente"),
        IN_NEGOCIATION("en négociation"),
        IN_PROGRESS("en cours"),
        FINISHED("finis"),
    }


    init
    {
        this.id = id
        this.uid = uid
        this.creationdate = creationdate
        this.amount = amount
        this.totalRefunded = totalRefunded
        this.rate = rate
        this.loan_type = loan_type
        this.state_id = state_id
        this.description = description
        this.user_requester_id = user_requester_id
        this.user_provider_id = user_provider_id
        this.delay = delay
    }

    fun getNeededRefund(): Float = amount + (amount * (rate / 100))
}

