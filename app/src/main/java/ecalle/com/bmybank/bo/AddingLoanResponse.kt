package ecalle.com.bmybank.bo

import ecalle.com.bmybank.realm.bo.Loan

/**
 * Created by Thomas Ecalle on 04/04/2018.
 */
data class AddingLoanResponse(val loan: Loan, val success: Boolean)