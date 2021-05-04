package com.example.colorblindhelper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.util.*


private fun changeImg(bitmap: Bitmap, w: Int, h: Int) : Bitmap
{
    val result = Bitmap.createBitmap(w, h, bitmap.config)
    val pixels = IntArray(w * h)
    //get pixels
    bitmap.getPixels(pixels, 0, w, 0, 0, w, h)
    for (x in pixels.indices)
        if(Color.green(pixels[x]) >200 && Color.red(pixels[x]) > 200 && Color.blue(pixels[x]) >200)
            pixels[x] = Color.BLACK
    // create result bitmap output
    result.setPixels(pixels, 0, w, 0, 0, w, h)
    return result
}
public fun getEditedImg(
    bitmap: Bitmap,
    w: Int,
    h: Int,
    cameraPreview: ImageView?,
    editCameraPreview: ImageView
)
{


    cameraPreview?.setImageBitmap(bitmap)
    editCameraPreview.setImageBitmap(changeImg(bitmap, w, h))
}

public fun saveImgInStoarge(bitmap: Bitmap, context: Context)
{
    val resultBitmap = changeImg(bitmap, bitmap.width, bitmap.height)
    val root  = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString()+File.separator +"blindColorApp"
    File(root).mkdirs()
    val myDir =File(root)
    val file_name = generateNewFileName()
    val file = File(myDir, file_name);
    if (file.exists()) file.delete();
    Log.i("LOAD", root + file_name);
    try {
        val out =  FileOutputStream(file);
        resultBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
        out.flush();
        out.close();
        Toast.makeText(context, "The picture was saved", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        e.printStackTrace();
    }

}
private fun generateNewFileName(): String {
    val currentDateTime = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        LocalDateTime.now().year.toString()+"_"+LocalDateTime.now().monthValue.toString()+"_"+LocalDateTime.now().dayOfMonth.toString()+"_"+
                LocalDateTime.now().hour.toString()+"_"+
                LocalDateTime.now().minute.toString()+"_"+LocalDateTime.now().second.toString()
    } else {
        TODO("VERSION.SDK_INT < O")
    }
    return  "editedImg"+currentDateTime+".jpg";
}
private fun getUserName(context: Context): String? {
    val account = GoogleSignIn.getLastSignedInAccount(context)
    return account?.email?.dropLastWhile { it != '@' }?.dropLast(1)
}
public fun uploadPictureToFirebaseStorage(context: Context, bitmap: Bitmap?,uri: Uri?) {
    val storage: FirebaseStorage = FirebaseStorage.getInstance()
    val storageRef: StorageReference = storage.getReference()

    val riversRef =
        storageRef.child("images/posts/" + getUserName(context) + "/" + generateNewFileName())
    var uploadTask : UploadTask? = null
    if (bitmap != null) {
        uploadTask = uploadFromBitmap(bitmap, riversRef)
    }
    else if(uri != null)
    {
        uploadTask = uploadFromUri(uri,riversRef)
    }
    uploadTask?.addOnFailureListener(OnFailureListener {
        Toast.makeText(context,"The image wasn't uploaded. Try again later!",Toast.LENGTH_LONG).show()
    })?.addOnSuccessListener(OnSuccessListener<Any?> {
        Toast.makeText(context,"The image was uploaded",Toast.LENGTH_LONG).show()
    })
}
private fun uploadFromUri(uri: Uri, storageRef:StorageReference): UploadTask {
    val file = Uri.fromFile(File("path/to/images/rivers.jpg"))
    val riversRef = storageRef.child("images/${file.lastPathSegment}")
    return storageRef.putFile(file)

}
private fun uploadFromBitmap(bitmap: Bitmap, storageRef:StorageReference): UploadTask {
    val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    val data: ByteArray = baos.toByteArray()
    return storageRef.putBytes(data)
}