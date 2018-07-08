package ecalle.com.bmybank

/**
 * Created by Thomas Ecalle on 18/03/2018.
 */
class Constants
{
    companion object
    {
        val SERIALIZED_OBJECT_KEY = "serializedObjectKey"
        val FRAGMENT_TAG = "fragmentTag"
        val SHARED_PREFERENCES = "sharedPreferences"
        val FAILURE_ANIMATION = "failure.json"
        val SUCCESS_ANIMATION = "success.json"
        val LOADING_ANIMATION = "loading.json"
        val USER_UUID_PREFERENCES_KEY = "uid"
        val DESCRIPTION_MAX_LENGTH = 255
        val TOKEN_PREFERENCES_KEY = "tokenPreferencesKey"
        val PENDING_LOANS = "En attente"
        val IN_PROGRESS_LOANS = "En cours"
        val IN_NEGOCIATION_LOANS = "En négociation"
        val FINISHED_LOANS = "finis"
        val FINISHED_LOANS_LABEL = "Terminés"
        val BAD_RATE = 3.0
        val NORMAL_RATE = 10.0
        val LYDIA_PROVIDER_TOKEN = "5afeed242c336502991747 "
        val PROFILE_PICTURES_NODE = "profile_pictures"
        val PUBLIC_LOAN = "public"
        val PRIVATE_LOAN = "prive"
        val STATE_WAITING = "en attente"
        val STATE_IN_PROGRESS = "en cours"
    }
}