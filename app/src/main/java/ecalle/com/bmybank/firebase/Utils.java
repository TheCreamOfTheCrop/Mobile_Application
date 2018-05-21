package ecalle.com.bmybank.firebase;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by thoma on 19/01/2018.
 */

public class Utils
{
    private static FirebaseDatabase database;

    /*
        in Order to assure that setPersistence is call
     */
    public static FirebaseDatabase getDatabase()
    {
        if (database == null)
        {
            database = FirebaseDatabase.getInstance();
            database.setPersistenceEnabled(true);
        }

        return database;

    }
}
