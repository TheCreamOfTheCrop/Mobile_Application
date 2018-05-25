package ecalle.com.bmybank.firebase.bo

import com.squareup.moshi.Json
import java.io.Serializable

/**
 * Created by Thomas Ecalle on 25/05/2018.
 */
data class Notification(
        @Json(name = "recipient_id") var recipient_id: Int = -1,
        @Json(name = "title") var title: String = "",
        @Json(name = "message") var message: String = ""
) : Serializable