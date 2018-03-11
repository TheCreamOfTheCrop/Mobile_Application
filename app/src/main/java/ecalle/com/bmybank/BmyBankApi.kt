package ecalle.com.bmybank

import ecalle.com.bmybank.bo.dummy.Course
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

/**
 * Created by thoma on 11/03/2018.
 */
interface BmyBankApi
{

    @GET("/courses")
    fun listCourses(): Call<List<Course>>

    companion object
    {
        private val url = "http://mobile-courses-server.herokuapp.com/"

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