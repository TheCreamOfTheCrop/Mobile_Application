package ecalle.com.bmybank.bo

import java.io.Serializable

/**
 * Created by Thomas Ecalle on 14/03/2018.
 */
data class User(val id: Int = 0,
                val uid: String = "",
                val email: String,
                var password: String,
                var lastname: String,
                var firstname: String,
                var avatar: String = "",
                var description: String,
                var isAccountValidate: Boolean = true) : Serializable