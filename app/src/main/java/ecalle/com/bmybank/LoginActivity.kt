package ecalle.com.bmybank

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import ecalle.com.bmybank.bo.dummy.Course
import ecalle.com.bmybank.extensions.customAlert
import ecalle.com.bmybank.extensions.log
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity(), View.OnClickListener
{

    companion object
    {
        val INSCRIPTION_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        passwordForgotten.setOnClickListener(this)
        inscription.setOnClickListener(this)
        validate.setOnClickListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        if (requestCode == INSCRIPTION_REQUEST)
        {
            when (resultCode)
            {
                Activity.RESULT_OK -> customAlert(message = R.string.validated_inscription_message)
            }
        }
    }

    override fun onClick(view: View?)
    {
        when (view?.id)
        {
            passwordForgotten.id -> startActivity<PasswordForgottenActivity>()
            inscription.id -> startActivityForResult<InscriptionActivity>(INSCRIPTION_REQUEST)
            validate.id -> fakeLogin()
        }
    }

    private fun fakeLogin()
    {
        log("start loading function")
        val api = BmyBankApi.getInstance()
        val coursesRequest = api.listCourses()


        coursesRequest.enqueue(object : Callback<List<Course>>
        {
            override fun onResponse(call: Call<List<Course>>, response: Response<List<Course>>)
            {
                val allCourse = response.body()
                if (allCourse != null)
                {
                    toast("all courses from api are : $allCourse")
                }
            }

            override fun onFailure(call: Call<List<Course>>, t: Throwable)
            {
                toast("Failure getting courses from server")
            }
        })

        log("end loading function")

    }
}

