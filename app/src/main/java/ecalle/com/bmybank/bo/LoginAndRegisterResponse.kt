package ecalle.com.bmybank.bo

import ecalle.com.bmybank.realm.bo.User

/**
 * Created by Thomas Ecalle on 18/03/2018.
 */
data class LoginAndRegisterResponse(val user: User,
                                    val message: String,
                                    val success: Boolean)