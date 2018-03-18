package ecalle.com.bmybank.services

import ecalle.com.bmybank.BuildConfig
import ecalle.com.bmybank.bo.LoginAndRegisterResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Thomas Ecalle on 11/03/2018.
 */
interface BmyBankApi
{

    @FormUrlEncoded
    @POST("user/login")
    fun login(@Field("email") email: String?, @Field("password") password: String?): Call<LoginAndRegisterResponse>

    @FormUrlEncoded
    @POST("user/register")
    fun register(@Field("email") email: String?,
                 @Field("password") password: String?,
                 @Field("lastname") lastname: String?,
                 @Field("firstname") firstname: String?,
                 @Field("description") description: String?,
                 @Field("isAccountValidate") isAccountValidate: Boolean?): Call<LoginAndRegisterResponse>

    companion object
    {
        private val url = "http://91.121.67.75:4100"

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