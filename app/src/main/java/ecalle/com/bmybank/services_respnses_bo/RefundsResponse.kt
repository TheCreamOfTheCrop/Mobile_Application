package ecalle.com.bmybank.services_respnses_bo

import ecalle.com.bmybank.realm.bo.Refund

/**
 * Created by Thomas Ecalle on 18/03/2018.
 */
data class RefundsResponse(val refunds: MutableList<Refund>,
                           val success: Boolean)