package ecalle.com.bmybank.bo

import java.io.Serializable

/**
 * Created by Thomas Ecalle on 14/03/2018.
 */
data class User(val id: Int,
                val uid: String,
                val email: String,
                val password: String,
                val lastname: String,
                val firstname: String,
                val avatar: String,
                val description: String,
                val isAccountValidate: Boolean) : Serializable