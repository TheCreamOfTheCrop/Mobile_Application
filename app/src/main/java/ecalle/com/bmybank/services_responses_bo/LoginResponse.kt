package ecalle.com.bmybank.services_responses_bo

import ecalle.com.bmybank.realm.bo.User

/**
 * Created by Thomas Ecalle on 03/04/2018.
 */
data class LoginResponse(
        val result: Result,
        val message: String,
        val success: Boolean)

data class Result(
        val token: String,
        val user: User
        )