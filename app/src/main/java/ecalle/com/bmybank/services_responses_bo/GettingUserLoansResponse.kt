package ecalle.com.bmybank.services_responses_bo

import ecalle.com.bmybank.realm.bo.Loan

/**
 * Created by Thomas Ecalle on 07/04/2018.
 */
data class GettingUserLoansResponse(val loans: List<Loan>, val success: Boolean)