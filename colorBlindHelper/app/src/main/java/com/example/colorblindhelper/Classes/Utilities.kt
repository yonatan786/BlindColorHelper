package com.example.colorblindhelper


import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.colorblindhelper.Activities.MainActivity
import com.example.colorblindhelper.Activities.RC_REGISTER
import com.example.colorblindhelper.Activities.Register_Activity
import com.example.colorblindhelper.Activities.ViewImage
import com.example.colorblindhelper.Classes.ImageRecyclerAdapter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.awaitAll
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDateTime
import kotlin.collections.ArrayList


enum class Gender {
    MALE,FEMALE
}

fun uploadDataToFirebase(context: Context, isGlasses:Boolean, gender: Gender, birthDate: String) {

    val db = Firebase.firestore
    val userName = getUserName(context) ?: return
    val user = UserModel(userName,isGlasses,gender,birthDate)
    db.collection("users").document(getUserName(context)!!).set(user)
        .addOnSuccessListener { documentReference ->
            Toast.makeText(context,"The details are saved",Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener { e ->
            Toast.makeText(context,"Failed to upload the details",Toast.LENGTH_SHORT).show()
        }
}
public fun showDialog(pos: Int, item: String?,context:Context,activity: Activity,userName: String)
{
    val storageRef : StorageReference = FirebaseStorage.getInstance().reference.child("images/posts/"+ userName)

    val dialog : Dialog = Dialog(context)
    dialog.setContentView(R.layout.activity_view_image)
    if (item != null) {
        viewImg(dialog.context,storageRef,item,dialog.findViewById<ImageView>(R.id.imgViewPost))
    }
    dialog.findViewById<EditText>(R.id.etComment).visibility = View.GONE
    dialog.findViewById<TextView>(R.id.tvSend).visibility = View.GONE
    dialog.findViewById<Button>(R.id.btnClose).setOnClickListener(View.OnClickListener{
        dialog.dismiss()
    })
    dialog.findViewById<Button>(R.id.btnFull).setOnClickListener(View.OnClickListener{
        val intent = Intent(context, ViewImage::class.java)
        intent.putExtra("username", userName)
        intent.putExtra("fileName", item)
        activity.startActivityForResult(intent,100)
    })
    dialog.show()
}
fun checkReadWritePermissions(activity: Activity, context: Context): Boolean {
    ActivityCompat.requestPermissions(
        activity,
        arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ),
        1
    )
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED) {
        return true
    }
    return false
}
//private fun changeImg(bitmap: Bitmap, w: Int, h: Int) : Bitmap
//{
//    val result = Bitmap.createBitmap(w, h, bitmap.config)
//    val pixels = IntArray(w * h)
//    //get pixels
//    bitmap.getPixels(pixels, 0, w, 0, 0, w, h)
//    for (x in pixels.indices)
//        if(Color.green(pixels[x]) >200 && Color.red(pixels[x]) > 200 && Color.blue(pixels[x]) >200 )
//            pixels[x] = Color.BLACK
//    // create result bitmap output
//    result.setPixels(pixels, 0, w, 0, 0, w, h)
//    return result
//}
private fun colorLimit(x: Double) : Int
{
    return minOf(maxOf(x.toInt(),0),255)
}
private fun changeImg(bitmap: Bitmap,blindType: ClassifyBlindness) : Bitmap
{
    if(blindType == ClassifyBlindness.UNCLASSIFIED || blindType == ClassifyBlindness.NORMAL)
        return bitmap
    val result = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
    val pixels = IntArray(bitmap.width * bitmap.height)
    val filterMatrix = getFilterMatrix(blindType)
    bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
    for (x in pixels.indices) {
        val r =
            colorLimit((Color.red(pixels[x]) * filterMatrix[0]) + (Color.green(pixels[x]) * filterMatrix[1]) + (Color.blue(pixels[x]) * filterMatrix[2]))
        val g =
            colorLimit((Color.red(pixels[x]) * filterMatrix[3]) + (Color.green(pixels[x]) * filterMatrix[4]) + (Color.blue(pixels[x]) * filterMatrix[5]))
        val b =
            colorLimit((Color.red(pixels[x]) * filterMatrix[6]) + (Color.green(pixels[x]) * filterMatrix[7]) + (Color.blue(pixels[x]) * filterMatrix[8]));
        pixels[x] = Color.rgb(r,g,b)
    }
        // create result bitmap output
        result.setPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        return result
}

fun getFilterMatrix(blindType: ClassifyBlindness): Array<Double> {
//    return when(blindType) {
//        ClassifyBlindness.UNCLASSIFIED -> arrayOf(1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0)
//        ClassifyBlindness.NORMAL -> arrayOf(1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0)
//        ClassifyBlindness.RED_BLIND -> arrayOf(1.04871, -0.06124, -0.016149, 0.4999456,0.4018163,0.0670727,0.622638,-0.7142829,1.1090876)
//        ClassifyBlindness.GREEN_BLIND -> arrayOf(1.207, -0.40164, 0.1923, 0.050346, 0.96232, -0.0024459, -0.1272146, 0.16927233, 0.9832832)
//        ClassifyBlindness.BLACK_WHITE_BLIND -> arrayOf(0.1942258, 0.652747, 0.1484325, 0.2435959, 0.660768, 0.1364899, 0.201588, 0.6132569,0.21213619)
//    }
    return when(blindType) {
        ClassifyBlindness.UNCLASSIFIED -> arrayOf(1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0)
        ClassifyBlindness.NORMAL -> arrayOf(1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0)
        ClassifyBlindness.RED_BLIND -> arrayOf(1.048, -0.061, -0.016, 0.499,0.401,0.067,0.622,-0.714,1.109)
        ClassifyBlindness.GREEN_BLIND -> arrayOf(1.207, -0.401, 0.192, 0.050, 0.962, -0.002, -0.127, 0.169, 0.983)
        ClassifyBlindness.BLACK_WHITE_BLIND -> arrayOf(0.194, 0.652, 0.148, 0.243, 0.660, 0.136, 0.201, 0.613,0.212)
    }
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
    editCameraPreview.setImageBitmap(changeImg(bitmap,getBlindType()))
}

fun getBlindType(): ClassifyBlindness {
//    val userName = getUserName(context)
//    var blindType: ClassifyBlindness = ClassifyBlindness.NORMAL
////        rootRef.collection("users").document(username!!).
////            .addOnSuccessListener { documentSnapshot ->
////                blindType = documentSnapshot.toObject(ClassifyBlindness::class.java)!!
////            }.addOnFailureListener{
////
////            }
//
//    val rootRef = Firebase.firestore.collection("users").whereEqualTo("userName", userName)
//        .addSnapshotListener { snapshot, e ->
//            if (!snapshot?.isEmpty!!) {
//                blindType = when (snapshot.documents[0].get("blindType")) {
//                    "RED_BLIND" ->ClassifyBlindness.RED_BLIND
//                    "NORMAL"->ClassifyBlindness.NORMAL
//                    "GREEN_BLIND"->ClassifyBlindness.GREEN_BLIND
//                    "BLACK_WHITE_BLIND"->ClassifyBlindness.BLACK_WHITE_BLIND
//                    "UNCLASSIFIED"->ClassifyBlindness.UNCLASSIFIED
//                    else -> ClassifyBlindness.UNCLASSIFIED
//                }
//            }
//        }
//    return blindType
    return ClassifyBlindness.BLACK_WHITE_BLIND
}
public fun saveImgInStorage(bitmap: Bitmap, context: Context)
{
    val resultBitmap = changeImg(bitmap,getBlindType())
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
            imageView.setImageBitmap(changeImg(bitmap,getBlindType()))
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
            gridView?.adapter = ImageRecyclerAdapter(activity, fileNameList,userName)
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
        //return TODO()
    }
    return storageRef.putFile(uri)

}
private fun uploadFromBitmap(bitmap: Bitmap, storageRef:StorageReference): UploadTask {
    val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    val data: ByteArray = baos.toByteArray()
    return storageRef.putBytes(data)
}

fun RejectFriendRequest(context: Context,userSentRequest : String,userGotRequest :String) {
    //TODO("adding notification to userSend")
    updateStatus(context,userSentRequest,userGotRequest,Status.REJECTED)

}

fun AcceptFriendRequest(context: Context,userSentRequest : String,userGotRequest :String) {
    //TODO("adding notification to userSend")
    updateStatus(context,userSentRequest,userGotRequest,Status.FRIENDS)
}
private fun updateStatus(context: Context,userSentRequest : String,userGotRequest :String,status: Status)
{
    val db = Firebase.firestore
    val request = RequestFriendship(status,userSentRequest, userGotRequest)
    db.collection("requests").document(userSentRequest).collection("newRequests").document(
        userGotRequest).set(request)
    db.collection("requests").document(userGotRequest).collection("newRequests").document(
        userSentRequest).set(request)
}