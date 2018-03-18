package ecalle.com.bmybank.realm

import android.support.annotation.WorkerThread
import ecalle.com.bmybank.Constants
import ecalle.com.bmybank.realm.bo.User
import io.realm.Realm


/**
 * Created by Thomas Ecalle on 18/03/2018.
 */
object RealmServices
{
    @WorkerThread
    fun saveCurrentuser(user: User)
    {
        val realm = Realm.getDefaultInstance()
        realm.use {
            it.executeTransaction {
                it.copyToRealmOrUpdate(user)
            }
        }
    }

    @WorkerThread
    fun getCurrentUser(uid: String): User?
    {
        val realm = Realm.getDefaultInstance()
        realm.use {
            val user = it.where(User::class.java).equalTo(Constants.USER_UUID_PREFERENCES_KEY, uid).findFirst()
            return if (user != null)
            {
                it.copyFromRealm(user)
            }
            else
            {
                null
            }
        }
    }

    @WorkerThread
    fun deleteCurrentUser(uid: String?)
    {
        val realm = Realm.getDefaultInstance()
        realm.use {
            val user = it.where(User::class.java).equalTo(Constants.USER_UUID_PREFERENCES_KEY, uid).findFirst()
            it.executeTransaction{
                user?.deleteFromRealm()
            }
        }
    }
}