package ecalle.com.bmybank.services

import ecalle.com.bmybank.BuildConfig
import ecalle.com.bmybank.bo.User
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by thoma on 11/03/2018.
 */
interface BmyBankApi
{

    @FormUrlEncoded
    @POST("/user/login")
    fun login(@Field("email") email: String?, @Field("password") password: String?): Call<User>

    companion object
    {
        private val url = "http://127.0.0.1:9001"

        fun getInstance(): BmyBankApi
        {
            val client = OkHttpClient().newBuilder()
                    //.cache(cache)
                    .addInterceptor(HttpLoggingInterceptor().apply
                    {
                        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
                    })
                    .build()

            val retrofit = Retrofit.Builder()
                    .addConverterFactory(MoshiConverterFactory.create())
                    .client(client)
                    .baseUrl(url)
                    .build()

            return retrofit.create(BmyBankApi::class.java)
        }
    }
}