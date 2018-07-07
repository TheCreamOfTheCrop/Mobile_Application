package ecalle.com.bmybank.services_responses_bo

import ecalle.com.bmybank.realm.bo.Note

/**
 * Created by thomasecalle on 29/03/2018.
 */
data class NoteListReponse(val note: MutableList<Note>, val success: Boolean)