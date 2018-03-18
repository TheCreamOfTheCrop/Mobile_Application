package ecalle.com.bmybank

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * Created by Thomas Ecalle on 18/03/2018.
 */
class BmyBankApplication : Application()
{
    override fun onCreate()
    {
        super.onCreate()
        Realm.init(this)
        Realm.setDefaultConfiguration(RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build())
    }
}