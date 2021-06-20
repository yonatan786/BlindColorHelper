package com.example.colorblindhelper.Classes

//import com.example.colorblind.VisionUtilities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Build
import android.util.Log
import android.util.SparseArray
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.example.colorblindhelper.getEditedImg
import com.example.colorblindhelper.saveImgInStorage
import com.example.colorblindhelper.uploadPictureToFirebaseStorage
import com.example.colorblindhelper.uploadType
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Frame
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.ByteBuffer



class colorDetector(activity: Activity, cameraPreview: ImageView?, editCameraPreview: ImageView?) {

    private val TAG = "colorDetector"
    private var  bitmap:Bitmap? = null
    private val FPS: Number = 20

    private var cameraSource: CameraSource? = null
    private var cameraSourceCustomDetector: CustomDetector? = null
    private var editCameraPreview: ImageView? = null
    //private var visionUtilities: VisionUtilities? = null
    private var activity: Activity? = null
    private var cameraPreview: ImageView? = null

    init {
        this.activity = activity
        this.cameraPreview = cameraPreview
        this.editCameraPreview = editCameraPreview
        cameraSourceCustomDetector = CustomDetector()
        setupCameraSource()
    }

    private fun setupCameraSource() {
        cameraSource = CameraSource.Builder(activity, cameraSourceCustomDetector)
            .setAutoFocusEnabled(true).setRequestedFps(10F)
            .setFacing(CameraSource.CAMERA_FACING_BACK).build()
    }

    fun start(context: Context) {
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                cameraSource?.start()
            }
        } catch (e: IOException) {
            Log.d(TAG, "Couldn't start camera")
        }
    }

    fun stop() {
        cameraSource!!.stop()
    }

    fun saveImageToStorage() {
        saveImgInStorage(rotate(bitmap!!, 90F)!!,activity!!)
    }
    fun uploadImageToFirebase(type: uploadType) {
        uploadPictureToFirebaseStorage(activity!!,rotate(bitmap!!, 90F)!!,null,type)
    }
    inner class CustomDetector : Detector<Point>() {

        @RequiresApi(Build.VERSION_CODES.O)
        override fun detect(frame: Frame?): SparseArray<Point>? {

            val byteBuffer: ByteBuffer = frame!!.grayscaleImageData
            val bytes: ByteArray = byteBuffer.array()
            val w = frame.metadata.width
            val h = frame.metadata.height
            val yuvimage = YuvImage(bytes, ImageFormat.NV21, w, h, null)
            val baos = ByteArrayOutputStream()
            yuvimage.compressToJpeg(
                Rect(0, 0, w, h),
                100,
                baos
            ) // Where 100 is the quality of the generated jpeg

            val jpegArray = baos.toByteArray()
             bitmap = BitmapFactory.decodeByteArray(jpegArray, 0, jpegArray.size)
            activity?.runOnUiThread(Runnable{ getEditedImg(rotate(bitmap!!, 90F)!!,w,h, cameraPreview!!, editCameraPreview!!,
                activity!!
            ) })

            return null

        }


    }

    fun rotate(bitmap: Bitmap, degree: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(degree)
        val resized_bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.width, bitmap.width, false)
        return Bitmap.createBitmap(resized_bitmap, 0, 0, resized_bitmap.width, resized_bitmap.height, matrix, true)
    }
}



