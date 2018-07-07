package ecalle.com.bmybank.services_responses_bo

import ecalle.com.bmybank.realm.bo.Negociation

/**
 * Created by Thomas Ecalle on 11/04/2018.
 */
data class AddingNegociationResponse(val negociation: Negociation, val success: Boolean)