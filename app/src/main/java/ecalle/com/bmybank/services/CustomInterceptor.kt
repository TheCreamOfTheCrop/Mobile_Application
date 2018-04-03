package ecalle.com.bmybank.services

import android.content.Context
import ecalle.com.bmybank.Constants
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by Thomas Ecalle on 03/04/2018.
 */
class CustomInterceptor(val context: Context?) : Interceptor
{
    override fun intercept(chain: Interceptor.Chain?): Response
    {
        val token = context?.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)?.getString(Constants.TOKEN_PREFERENCES_KEY, null)
        val request = chain?.request()
        val authenticatedRequest =
                request?.newBuilder()
                        ?.header("Authorization", if (token !== null) token else "")?.build()
        return chain!!.proceed(authenticatedRequest)
    }

}