package ecalle.com.bmybank.services

import android.content.Context
import ecalle.com.bmybank.bo.AddingLoanResponse
import ecalle.com.bmybank.bo.GettingUserLoansResponse
import ecalle.com.bmybank.bo.LoginResponse
import ecalle.com.bmybank.bo.UserResponse
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.PUT

/**
 * Created by Thomas Ecalle on 11/03/2018.
 */
interface BmyBankApi
{

    @FormUrlEncoded
    @POST("loan/add")
    fun addLoan(@Field("amount") amount: Float?,
                @Field("description") description: String?,
                @Field("rate") rate: Float?,
                @Field("id") userId: Int?,
                @Field("delay") delay: Int? = 400): Call<AddingLoanResponse>

    @FormUrlEncoded
    @POST("loan/findLoan")
    fun findPersonalLoans(@Field("state_id") filter: String = ""): Call<GettingUserLoansResponse>

    @POST("/loan/listPublic")
    fun findPublicLoans(): Call<GettingUserLoansResponse>

    @FormUrlEncoded
    @POST("user/login")
    fun login(@Field("email") email: String?, @Field("password") password: String?): Call<LoginResponse>

    @FormUrlEncoded
    @POST("user/register")
    fun register(@Field("email") email: String?,
                 @Field("password") password: String?,
                 @Field("lastname") lastname: String?,
                 @Field("firstname") firstname: String?,
                 @Field("description") description: String?,
                 @Field("isAccountValidate") isAccountValidate: Boolean?): Call<UserResponse>

    @FormUrlEncoded
    @PUT("/user")
    fun updateUser(@Field("id") id: Int?,
                   @Field("email") email: String? = null,
                   @Field("previousPassword") previousPassword: String? = null,
                   @Field("newPassword") newPassword: String? = null,
                   @Field("lastname") lastname: String? = null,
                   @Field("firstname") firstname: String? = null,
                   @Field("description") description: String? = null): Call<UserResponse>

    @FormUrlEncoded
    @POST("/user")
    fun findUserById(@Field("id") id: Int): Call<UserResponse>

    companion object
    {
        private val url = "https://still-cove-11874.herokuapp.com/"

        fun getInstance(context: Context?): BmyBankApi
        {
            val client = OkHttpClient().newBuilder()
                    .addInterceptor(CustomInterceptor(context))
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