package ecalle.com.bmybank.services

import android.content.Context
import ecalle.com.bmybank.Constants
import ecalle.com.bmybank.services_responses_bo.*
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
    @POST("/note/listNote")
    fun getNotes(@Field("user_id") id: Int): Call<NoteListReponse>

    @POST("/note/listNoteMadeByUser")
    fun getCreatedNotes(): Call<NoteListReponse>

    @FormUrlEncoded
    @POST("/note/findLoanNote")
    fun getLoanNotes(@Field("loan_id") id: Int): Call<NoteListReponse>

    @FormUrlEncoded
    @POST("/note/add")
    fun addNote(
            @Field("note") note: Float,
            @Field("comments") comment: String,
            @Field("user_id") userId: Int,
            @Field("loan_id") id: Int): Call<AddNoteResponse>

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
    @POST("refund/add")
    fun addRefund(@Field("loan_id") idLoan: Int,
                  @Field("amount") amount: Float): Call<AddRefundsResponse>

    @FormUrlEncoded
    @POST("refund/list")
    fun getRefunds(@Field("loan_id") idLoan: Int?): Call<RefundsResponse>

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

    @FormUrlEncoded
    @POST("/user/delete")
    fun deleteUser(@Field("id") id: Int): Call<SImpleResponse>

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