package ecalle.com.bmybank.fragments.inscription_steps

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.stepstone.stepper.Step
import com.stepstone.stepper.VerificationError
import ecalle.com.bmybank.R
import ecalle.com.bmybank.extensions.log
import ecalle.com.bmybank.interfaces.InscriptionListeningActivity
import org.jetbrains.anko.find
import permissions.dispatcher.*


/**
 * Created by Thomas Ecalle on 04/03/2018.
 */

@RuntimePermissions
class AvatarStep : Fragment(), Step, View.OnClickListener
{

    lateinit private var avatarImageView: ImageView

    companion object
    {
        val REQUEST_TAKE_PHOTO = 0
        val REQUEST_SELECT_IMAGE_IN_ALBUM = 1
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_avatar_step, container, false)

        val takeFromGallery = view.find<Button>(R.id.takeFromGallery)
        val takeFromCamera = view.find<Button>(R.id.takeFromCamera)


        takeFromGallery.setOnClickListener(this)
        takeFromCamera.setOnClickListener(this)

        avatarImageView = view.find<ImageView>(R.id.avatarImageView)

        return view
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // NOTE: delegate the permission handling to generated function
        onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        if (requestCode == AvatarStep.REQUEST_SELECT_IMAGE_IN_ALBUM && resultCode == Activity.RESULT_OK)
        {
            try
            {
                val capturedImageUri = data?.data
                log("getting image from gallery, uri is : $capturedImageUri")

                avatarImageView.setImageURI(capturedImageUri)

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
            avatarImageView.setImageBitmap(photo)
            setupBitmap()
        }
    }

    fun setupBitmap()
    {
        val listeningActivity = activity as InscriptionListeningActivity

        avatarImageView.setDrawingCacheEnabled(true)
        avatarImageView.buildDrawingCache()
        val bitmap = (avatarImageView.drawable as BitmapDrawable).bitmap


        listeningActivity.onAvatarSelected(bitmap)

    }

    override fun onSelected()
    {
    }

    override fun verifyStep(): VerificationError?
    {
        return null
    }

    override fun onError(error: VerificationError)
    {
    }

    override fun onClick(view: View?)
    {
        when (view?.id)
        {
            R.id.takeFromGallery -> selectImageInAlbum()
            R.id.takeFromCamera -> takePhotoWithPermissionCheck()
            else -> log("clicked on everything else")
        }
    }

    private fun selectImageInAlbum()
    {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(activity?.packageManager) != null)
        {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
        }
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    fun takePhoto()
    {
        log("launching camera")
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(activity?.packageManager) != null)
        {
            startActivityForResult(intent, REQUEST_TAKE_PHOTO)
        }
    }

    @OnShowRationale(Manifest.permission.CAMERA)
    fun showRationaleForCamera(request: PermissionRequest)
    {
        val alertDialogBuilder = AlertDialog.Builder(context!!)
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

    @OnPermissionDenied(Manifest.permission.CAMERA)
    fun onCameraDenied()
    {
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA)
    fun onCameraNeverAskAgain()
    {
    }

}