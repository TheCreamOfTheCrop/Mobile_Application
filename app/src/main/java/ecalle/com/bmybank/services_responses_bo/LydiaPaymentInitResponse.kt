package ecalle.com.bmybank.services_responses_bo

import java.io.Serializable

/**
 * Created by Thomas Ecalle on 23/05/2018.
 */
data class LydiaPaymentInitResponse(val error: String, val data: Data)

data class Data(val remote_transaction_pub_uuid: String, val url: String)

data class Payment(
        val payerId: String = "",
        val payerName: String = "",
        val receiverId: String = "",
        val receiverName: String = "",
        val date: Long = -1,
        val amount: String = "",
        val success: Boolean = false) : Serializable