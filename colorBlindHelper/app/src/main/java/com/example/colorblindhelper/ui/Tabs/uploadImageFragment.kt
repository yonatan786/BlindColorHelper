package com.example.colorblindhelper.ui.Tabs

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import com.example.colorblindhelper.*
import com.example.colorblindhelper.Activities.MainActivity


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [uploadImageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
public var imageUri: Uri? = null
private var imgViewUpload:ImageView? = null
private var imgViewEditCamera:ImageView? = null
private var btnUploadPicture :Button? = null
private var btnSavePicture :Button? = null
private var tvSelectPicture : TextView? = null
private var isSave = true
private var bitmap: Bitmap? = null
class uploadImageFragment : Fragment(), View.OnClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload_image, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment uploadImageFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            uploadImageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        imgViewUpload = view?.findViewById<ImageView>(R.id.cameraView)
        imgViewEditCamera = view?.findViewById<ImageView>(R.id.editCameraView)
        btnUploadPicture = view?.findViewById<Button>(R.id.btnUploadPicture)
        tvSelectPicture = view?.findViewById<TextView>(R.id.tvSelectPicture)
        btnSavePicture = view?.findViewById<Button>(R.id.btnSavePicture)
        btnUploadPicture?.setOnClickListener(this)
        tvSelectPicture?.setOnClickListener(this)
        imgViewUpload?.setOnClickListener(this)
        btnSavePicture?.setOnClickListener(this)
    }
    private fun addPicture()
    {
        if(!isSave) {
            var isSure : Boolean = true
            val alert = AlertDialog.Builder(context)
                .setTitle("Delete image")
                .setMessage("Are you sure you want to change the previous image without save it") // Specifying a listener allows you to take an action before dismissing the dialog.
                .setNegativeButton(android.R.string.yes,
                    DialogInterface.OnClickListener { dialog, which ->
                        val photoPickerIntent = Intent(Intent.ACTION_PICK)
                        photoPickerIntent.type = "image/*"
                        startActivityForResult(photoPickerIntent, 1)
                    })
                .setPositiveButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert).show()
        }
        else
        {
            isSave = false
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, 1)
        }

    }
    override  fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1)
            if (resultCode == Activity.RESULT_OK) {
                imageUri = data?.data
                imgViewUpload?.setImageURI(imageUri)
                viewEditedImg()

            }
    }
    private fun viewEditedImg(){
        bitmap = MediaStore.Images.Media.getBitmap(context?.getContentResolver(), imageUri)
        imgViewEditCamera?.visibility = View.VISIBLE
        if(bitmap != null)
            getEditedImg(bitmap!!, bitmap!!.width, bitmap!!.height, null, imgViewEditCamera!!,requireContext())

    }

    override fun onClick(v: View?) {
        if(v == tvSelectPicture || v == imgViewUpload)
            addPicture()
        else if (v == btnSavePicture) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                1
            )
            if (checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED) {
                bitmap?.let { saveImgInStorage(it, requireActivity()) }
            }
        }
        else if(v == btnUploadPicture)
        {
            imageUri?.let{uploadPictureToFirebaseStorage(requireContext(), null, imageUri,uploadType.POST)}
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra("tab",2)
            startActivity(intent)
        }
    }


}


