package ecalle.com.bmybank

/**
 * Created by Thomas Ecalle on 18/03/2018.
 */
class Constants
{
    companion object
    {
        const val SERIALIZED_OBJECT_KEY = "serializedObjectKey"
        const val FRAGMENT_TAG = "fragmentTag"
        const val SHARED_PREFERENCES = "sharedPreferences"
        const val FAILURE_ANIMATION = "failure.json"
        const val SUCCESS_ANIMATION = "success.json"
        const val LOADING_ANIMATION = "loading.json"
        const val USER_UUID_PREFERENCES_KEY = "uid"
        const val DESCRIPTION_MAX_LENGTH = 255
        const val TOKEN_PREFERENCES_KEY = "tokenPreferencesKey"
        const val PENDING_LOANS = "En attente"
        const val IN_PROGRESS_LOANS = "En cours"
        const val IN_NEGOCIATION_LOANS = "En négociation"
        const val FINISHED_LOANS = "finis"
        const val FINISHED_LOANS_LABEL = "Terminés"
        const val BAD_RATE = 3.0
        const val NORMAL_RATE = 10.0
        const val LYDIA_PROVIDER_TOKEN = "5afeed242c336502991747 "
        const val PROFILE_PICTURES_NODE = "profile_pictures"
        const val PUBLIC_LOAN = "public"
        const val PRIVATE_LOAN = "prive"
        const val STATE_WAITING = "en attente"
        const val STATE_IN_PROGRESS = "en cours"
        const val ADMIN_EMAIL_ADRESS = "bmybank.presta@gmail.com"
    }
}