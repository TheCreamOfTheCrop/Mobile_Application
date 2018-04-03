package ecalle.com.bmybank.bo

import ecalle.com.bmybank.realm.bo.User

/**
 * Created by Thomas Ecalle on 18/03/2018.
 */
data class RegisterResponse(val user: User,
                            val message: String,
                            val success: Boolean)