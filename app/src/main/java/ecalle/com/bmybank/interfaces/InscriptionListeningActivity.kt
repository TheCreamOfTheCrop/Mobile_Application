package ecalle.com.bmybank.interfaces

import ecalle.com.bmybank.bo.User

/**
 * Created by Thomas Ecalle on 18/03/2018.
 */
interface InscriptionListeningActivity
{
    fun onUserInformationsValidated(user: User)
    fun onAvatarSelected(uri: String)
    fun onAccountValidation(isValid: Boolean)
    fun getActualUser(): User?
}