package ecalle.com.bmybank.services_responses_bo

import ecalle.com.bmybank.realm.bo.Refund

/**
 * Created by Thomas Ecalle on 18/03/2018.
 */
data class AddRefundsResponse(val refund: Refund,
                              val success: Boolean)