package com.example.colorblindhelper


import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.GridView
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList


enum class Gender {
    MALE,FEMALE
}
fun uploadDataToFirebase(context: Context,isGlasses :Boolean,gender: Gender,birthDate: Calendar) {

    val db = Firebase.firestore
    val userName = getUserName(context) ?: return
    val user = hashMapOf(
        "userName" to userName,
        "isGlasses" to isGlasses,
        "gender" to gender,
        "birthDate" to birthDate
    )
    db.collection("users")
        .add(user)
        .addOnSuccessListener { documentReference ->
            Toast.makeText(context,"The details saved",Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener { e ->
            Toast.makeText(context,"Failed to upload the details",Toast.LENGTH_SHORT).show()
        }
}

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
public fun viewImg (context: Context, storageRef: StorageReference, fileName:String, imageView: ImageView)
{
    val ref = storageRef.child(fileName);
    try {
        val localFile = File.createTempFile("Images", "bmp");
        ref.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            imageView.setImageBitmap(changeImg(bitmap,bitmap.width,bitmap.height))
        }.addOnFailureListener {
            val account = GoogleSignIn.getLastSignedInAccount(context)
            if(account!=null)
                imageView.setImageURI(account.photoUrl)
        }
    } catch (e: IOException) {
        e.printStackTrace();
    }
}

public fun downloadImgViewProfile (context: Context,userName:String,imageView: ImageView)
{
    val storageRef: StorageReference = FirebaseStorage.getInstance().getReference()
    var  riversRef =  storageRef.child("images/profiles" )
    viewImg (context, riversRef, userName, imageView)
}
public fun getfileNameList(userName: String, gridView: GridView?,context:Context,activity:Activity) {
    val listRef : StorageReference = FirebaseStorage.getInstance().reference.child("images/posts/$userName")
    val  fileNameList: ArrayList<String> = ArrayList<String>()
    listRef.listAll()
        .addOnSuccessListener {
            it.items.forEach{
                fileNameList.add(it.name)
            }
            val userName = getUserName(context)
            if(userName != null) {
                gridView?.adapter = ImageAdapter(activity, fileNameList,userName)
            }
        }

}
public fun getUserName(context: Context): String? {
    val account = GoogleSignIn.getLastSignedInAccount(context)
    return account?.email?.dropLastWhile { it != '@' }?.dropLast(1)
}
enum class uploadType{POST,PROFILE}

public fun uploadPictureToFirebaseStorage(context: Context, bitmap: Bitmap?,uri: Uri?,type :uploadType ) {
    val storage: FirebaseStorage = FirebaseStorage.getInstance()
    val storageRef: StorageReference = storage.getReference()
    var riversRef : StorageReference? = null
    if(type == uploadType.POST) {
        riversRef =
                storageRef.child("images/posts/" + getUserName(context) + "/" + generateNewFileName())
    }
    else
    {
        riversRef =  storageRef.child("images/profiles/" + getUserName(context) )

    }
    var uploadTask : UploadTask? = null
    if (bitmap != null) {
        uploadTask = uploadFromBitmap(bitmap, riversRef)
    }
    else if(uri != null)
    {
        uploadTask = uploadFromUri(uri,riversRef,type)
    }
    uploadTask?.addOnFailureListener(OnFailureListener {
        Toast.makeText(context,"The image wasn't uploaded. Try again later!",Toast.LENGTH_LONG).show()
    })?.addOnSuccessListener(OnSuccessListener<Any?> {
        Toast.makeText(context,"The image was uploaded",Toast.LENGTH_LONG).show()
    })
}
private fun uploadFromUri(uri: Uri, storageRef: StorageReference, type: uploadType): UploadTask {
    if(type == uploadType.POST) {
        return TODO()
    }
    return storageRef.putFile(uri)

}
private fun uploadFromBitmap(bitmap: Bitmap, storageRef:StorageReference): UploadTask {
    val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    val data: ByteArray = baos.toByteArray()
    return storageRef.putBytes(data)
}