package ecalle.com.bmybank

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.moshi.Moshi
import de.hdodenhof.circleimageview.CircleImageView
import ecalle.com.bmybank.custom_components.BeMyDialog
import ecalle.com.bmybank.extensions.*
import ecalle.com.bmybank.firebase.GlideApp
import ecalle.com.bmybank.fragments.inscription_steps.AvatarStep
import ecalle.com.bmybank.realm.RealmServices
import ecalle.com.bmybank.realm.bo.User
import ecalle.com.bmybank.services.BmyBankApi
import ecalle.com.bmybank.services_responses_bo.SImpleResponse
import ecalle.com.bmybank.services_responses_bo.UserResponse
import org.jetbrains.anko.find
import permissions.dispatcher.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

/**
 * Created by Thomas Ecalle on 17/04/2018.
 */
@RuntimePermissions
class ProfileModificationActivity : AppCompatActivity(), View.OnClickListener
{

    lateinit private var scrollView: ScrollView
    lateinit private var email: EditText
    lateinit private var previousPassword: EditText
    lateinit private var password: EditText
    lateinit private var confirmPassword: EditText
    lateinit private var firstName: EditText
    lateinit private var lastName: EditText
    lateinit private var description: EditText
    lateinit private var save: FloatingActionButton
    lateinit private var gallery: Button
    lateinit private var camera: Button
    lateinit private var image: CircleImageView

    lateinit private var user: User
    private var loadingDialog: BeMyDialog? = null
    private var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_modification)
        scrollView = find(R.id.scrollView)
        gallery = find(R.id.gallery)
        camera = find(R.id.camera)
        email = find(R.id.email)
        previousPassword = find(R.id.previousPassword)
        password = find(R.id.password)
        confirmPassword = find(R.id.confirmPassword)
        firstName = find(R.id.firstName)
        lastName = find(R.id.lastName)
        description = find(R.id.description)
        save = find(R.id.save)
        image = find(R.id.image)

        save.setOnClickListener(this)
        camera.setOnClickListener(this)
        gallery.setOnClickListener(this)


        user = RealmServices.getCurrentUser(this) ?: return

        fillInformations()


        description.makeEditTextScrollableInScrollview()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        if (requestCode == AvatarStep.REQUEST_SELECT_IMAGE_IN_ALBUM && resultCode == Activity.RESULT_OK)
        {
            try
            {
                val capturedImageUri = data?.data
                log("getting image from gallery, uri is : $capturedImageUri")

                image.setImageURI(capturedImageUri)

                setupBitmap()

            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }
        }
        else if (requestCode == AvatarStep.REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK)
        {
            val photo = data?.extras?.get("data") as Bitmap
            image.setImageBitmap(photo)
            setupBitmap()
        }
    }


    @SuppressLint("NeedOnRequestPermissionsResult")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // NOTE: delegate the permission handling to generated function
        onRequestPermissionsResult(requestCode, grantResults)
    }

    private fun fillInformations()
    {
        GlideApp.with(this)
                .load(getImageFirebaseReference())
                .placeholder(R.drawable.default_profile)
                .error(R.drawable.default_profile)
                .diskCacheStrategy(DiskCacheStrategy.NONE) // <= ADDED
                .skipMemoryCache(true)
                .into(image)

        firstName.setText(user.firstname)
        lastName.setText(user.lastname)
        email.setText(user.email)
        description.setText(user.description)
    }

    private fun getImageFirebaseReference(): StorageReference
    {
        var emailWithoutSpecialCharacters = user.email.replace("@", "")
        emailWithoutSpecialCharacters = emailWithoutSpecialCharacters.replace(".", "")
        emailWithoutSpecialCharacters = emailWithoutSpecialCharacters.plus(".jpg")


        val firebaseStorage = FirebaseStorage.getInstance()
        return firebaseStorage.reference.child("${Constants.PROFILE_PICTURES_NODE}/$emailWithoutSpecialCharacters")

    }


    override fun onClick(view: View?)
    {
        when (view?.id)
        {
            R.id.save -> saveUser()
            R.id.gallery -> selectImageInAlbum()
            R.id.camera -> takePhotoWithPermissionCheck()
        }
    }

    fun setupBitmap()
    {

        image.isDrawingCacheEnabled = true
        image.buildDrawingCache()
        bitmap = (image.drawable as BitmapDrawable).bitmap

    }

    private fun selectImageInAlbum()
    {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(packageManager) != null)
        {
            startActivityForResult(intent, AvatarStep.REQUEST_SELECT_IMAGE_IN_ALBUM)
        }
    }

    private fun saveUser()
    {
        when
        {
            isNotValidEmailAdress() -> showInformation(getString(R.string.not_valid_email_adress))
            passwordAreDifferent() -> showInformation(getString(R.string.passwords_not_equals))
            namesAreNotWellFormat() -> showInformation(getString(R.string.not_well_format_names))
            descriptionIsNotWellFormat() -> showInformation(getString(R.string.not_well_format_description))
            else ->
            {
                var needToUpdatePassword = false
                var needToUpdateEmail = false
                var previousPasswordValue = ""

                if (email.textValue != user.email)
                {
                    needToUpdateEmail = true
                    user.email = email.textValue
                }

                if (!password.textValue.isEmpty())
                {
                    needToUpdatePassword = true
                    previousPasswordValue = previousPassword.textValue
                }

                user.lastname = lastName.textValue
                user.firstname = firstName.textValue
                user.description = description.textValue

                loadingDialog = customAlert(message = R.string.user_updating_loading, type = BeMyDialog.TYPE.LOADING)


                val api = BmyBankApi.getInstance(this)

                val updateRequest =
                        api.updateUser(id = user.id,
                                email = if (needToUpdateEmail) user.email else null,
                                previousPassword = if (needToUpdatePassword) previousPasswordValue else null,
                                newPassword = if (needToUpdatePassword) password.textValue else null,
                                lastname = user.lastname,
                                firstname = user.firstname,
                                description = user.description)

                if (bitmap != null)
                {
                    sendAvatarToFirebase(bitmap, user)
                }

                updateRequest.enqueue(object : Callback<UserResponse>
                {
                    override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>)
                    {
                        when
                        {
                            response.code() == 404 -> showInformation(getString(R.string.server_issue))
                            response.code() == 400 ->
                            {
                                if (response.errorBody() != null)
                                {
                                    val stringResponse = response.errorBody()!!.string()
                                    val moshi = Moshi.Builder().build()
                                    val jsonAdapter = moshi.adapter(SImpleResponse::class.java)
                                    val response = jsonAdapter.fromJson(stringResponse)

                                    showInformation(response.message)

                                }
                                else
                                {
                                    showInformation(getString(R.string.impossible_user_update))
                                }
                                loadingDialog?.dismiss()
                            }
                            else ->
                            {
                                showInformation(information = getString(R.string.user_update_success), error = false)

                                RealmServices.saveCurrentuser(user)

                                loadingDialog?.dismiss()
                            }
                        }

                        fillInformations()

                    }

                    override fun onFailure(call: Call<UserResponse>, t: Throwable)
                    {
                        //toast("Failure getting user from server, throwable message : ${t.message}")
                        loadingDialog?.dismiss()
                        showInformation(getString(R.string.not_internet))
                    }
                })

            }
        }
    }

    private fun showInformation(information: String = "", show: Boolean = true, error: Boolean = true)
    {
        if (error && show)
        {
            alertError(information)
        }
        else
        {
            if (show)
            {
                alertInfo(information)
            }
        }

        scrollView.fullScroll(ScrollView.FOCUS_UP)
    }

    private fun passwordAreDifferent(): Boolean
    {
        return (password.textValue != confirmPassword.textValue)
                || (!previousPassword.textValue.isEmpty() && password.isEmpty())
                || (previousPassword.textValue.isEmpty() && !password.isEmpty())
    }

    private fun isNotValidEmailAdress(): Boolean
    {
        return !android.util.Patterns.EMAIL_ADDRESS.matcher(email.textValue).matches();
    }

    private fun namesAreNotWellFormat(): Boolean
    {
        return !firstName.hasOnlyLetters() || !lastName.hasOnlyLetters()
    }

    private fun descriptionIsNotWellFormat(): Boolean
    {
        return description.textValue.length >= Constants.DESCRIPTION_MAX_LENGTH
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    fun takePhoto()
    {
        log("launching camera")
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null)
        {
            startActivityForResult(intent, AvatarStep.REQUEST_TAKE_PHOTO)
        }
    }

    @OnShowRationale(Manifest.permission.CAMERA)
    fun showRationaleForCamera(request: PermissionRequest)
    {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage(getString(R.string.askForCamera))
        alertDialogBuilder.setPositiveButton(R.string.autorization, { _, _ ->
            request.proceed()
        })
        alertDialogBuilder.setNegativeButton(R.string.refuse, { _, _ ->
            request.cancel()
        })
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.show()
    }

    private fun sendAvatarToFirebase(bitmap: Bitmap?, user: User?)
    {
        var emailWithoutSpecialCharacters = user?.email?.replace("@", "")
        emailWithoutSpecialCharacters = emailWithoutSpecialCharacters?.replace(".", "")
        emailWithoutSpecialCharacters = emailWithoutSpecialCharacters.plus(".jpg")

        if (bitmap != null && emailWithoutSpecialCharacters != null)
        {
            val firebaseStorage = FirebaseStorage.getInstance()

            val reference = firebaseStorage.getReference(Constants.PROFILE_PICTURES_NODE).child(emailWithoutSpecialCharacters)

            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            val uploadTask = reference.putBytes(data)
            uploadTask.addOnFailureListener(OnFailureListener {
            }).addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> {
            })
        }
    }

    @OnPermissionDenied(Manifest.permission.CAMERA)
    fun onCameraDenied()
    {
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA)
    fun onCameraNeverAskAgain()
    {
    }


}