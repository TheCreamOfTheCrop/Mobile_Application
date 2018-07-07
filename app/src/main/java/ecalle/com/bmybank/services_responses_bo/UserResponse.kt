package ecalle.com.bmybank.services_responses_bo

import ecalle.com.bmybank.realm.bo.User

/**
 * Created by Thomas Ecalle on 18/03/2018.
 */
data class UserResponse(val user: User,
                        val message: String,
                        val success: Boolean)