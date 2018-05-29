package ecalle.com.bmybank.services

import android.content.Context
import ecalle.com.bmybank.Constants
import ecalle.com.bmybank.services_respnses_bo.*
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

/**
 * Created by Thomas Ecalle on 11/03/2018.
 */
interface BmyBankApi
{

    @POST("https://us-central1-bmybank-2146c.cloudfunctions.net/sendChatMessage")
    fun sendChatMessage(
            @Query("current_user_id") currentUserId: Int,
            @Query("other_user_id") otherUserId: Int,
            @Query("message") messageToJson: String,
            @Query("channel") channelToJson: String): Call<SImpleResponse>

    @POST("https://us-central1-bmybank-2146c.cloudfunctions.net/sendChatMessage")
    fun see(@Body body: RequestBody): Call<SImpleResponse>

    @FormUrlEncoded
    @POST("https://homologation.lydia-app.com/api/payment/init.json")
    fun initLydiaPayment(
            @Field("provider_token") providerToken: String = Constants.LYDIA_PROVIDER_TOKEN,
            @Field("success_url") successUrl: String,
            @Field("fail_url") failUrl: String,
            @Field("recipient") otherUserEmailOrPhone: String,
            @Field("payer_info") currentUserEmailOrPhone: String,
            @Field("amount") amount: String,
            @Field("currency") currency: String = "EUR",
            @Field("message") message: String): Call<LydiaPaymentInitResponse>

    @FormUrlEncoded
    @POST("negociate/add")
    fun addNegociation(@Field("id_loan") idLoan: Int?,
                       @Field("amount") amount: Float?,
                       @Field("rate") rate: Float?,
                       @Field("delay") delay: Int?): Call<AddingNegociationResponse>

    @FormUrlEncoded
    @POST("loan/update")
    fun updateLoan(@Field("id_loan") idLoan: Int?,
                   @Field("amount") amount: Float?,
                   @Field("rate") rate: Float?,
                   @Field("delay") delay: Int?,
                   @Field("description") description: String?,
                   @Field("loan_type") loanType: String? = Constants.PUBLIC_LOAN,
                   @Field("user_provider_id") providerId: Int? = null

    ): Call<SImpleResponse>

    @FormUrlEncoded
    @POST("loan/accept")
    fun acceptDirectLoan(@Field("id_loan") idLoan: Int?): Call<SImpleResponse>

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

    @FormUrlEncoded
    @POST("/loan/searchLoan")
    fun findLoanById(@Field("id_loan") id: Int): Call<AddingLoanResponse>

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