package ecalle.com.bmybank.interfaces

import android.graphics.Bitmap
import ecalle.com.bmybank.realm.bo.User

/**
 * Created by Thomas Ecalle on 18/03/2018.
 */
interface InscriptionListeningActivity
{
    fun onUserInformationsValidated(user: User)
    fun onAvatarSelected(avatarBitMap: Bitmap)
    fun onAccountValidation(isValid: Boolean)
    fun getActualUser(): User?
    fun getAvatar(): Bitmap?
}