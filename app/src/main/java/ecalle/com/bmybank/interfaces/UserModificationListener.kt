package ecalle.com.bmybank.interfaces

import ecalle.com.bmybank.realm.bo.User

/**
 * Created by Thomas Ecalle on 03/04/2018.
 */
interface UserModificationListener
{
    fun userModified(user: User)
}