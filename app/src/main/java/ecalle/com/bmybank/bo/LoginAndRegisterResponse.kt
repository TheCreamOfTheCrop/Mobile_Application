package ecalle.com.bmybank.bo

/**
 * Created by Thomas Ecalle on 18/03/2018.
 */
data class LoginAndRegisterResponse(val user: User,
                                    val message: String,
                                    val success: Boolean)