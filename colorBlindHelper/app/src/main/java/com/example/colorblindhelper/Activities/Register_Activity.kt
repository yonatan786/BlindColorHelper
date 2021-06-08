package com.example.colorblindhelper.Activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.example.colorblindhelper.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*


enum class RequestType {SIGN_IN,UPDATE}
val RC_TEST = 100
class Register_Activity : AppCompatActivity(), View.OnClickListener {
    val requestType = arrayOf(RequestType.SIGN_IN, RequestType.UPDATE)
    private var requestIntentFlag : RequestType? = null
    private var etFullName : EditText? = null
    private var btnNext: Button? = null
    private var radioIsGlasses: RadioGroup? = null
    private var radioGender: RadioGroup? = null
    private var datePicker: DatePicker? = null
    private var switchNotify: SwitchCompat? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_)

        btnNext = findViewById(R.id.btnNext)
        datePicker = findViewById(R.id.datePicker)
        radioIsGlasses = findViewById(R.id.radioIsGlasses)
        radioGender = findViewById(R.id.radioGender)
        etFullName = findViewById(R.id.etFullName)
        btnNext!!.setOnClickListener(this)
        val i = intent.getIntExtra("requestCode",-1)
        requestIntentFlag = requestType[i]
        getUserDetails()
        switchNotify = findViewById<SwitchCompat>(R.id.notifySwitch)
    }

    private fun updateAllFields(date: String?, gender: Gender?, isGlasses: Boolean?,fullNameText:String?, sStatus: Boolean?) {
        radioGender?.check( if(gender == Gender.MALE){R.id.radioMale}else{R.id.radioFemale} )
        radioIsGlasses?.check(if(isGlasses==true){R.id.radioGlassesYes}else{R.id.radioGlassesYes})
        etFullName?.setText(fullNameText)
        switchNotify?.isChecked = sStatus == true
    }

    private fun getUserDetails() {
        val username = getUserName(applicationContext)
        val rootRef = FirebaseFirestore.getInstance()
        rootRef.collection("users").document(username!!).get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject(UserModel::class.java)
                if(user != null)
                    updateAllFields(user?.getBirthDate(),user?.getGender(),user?.getisGlasses(),user?.getFullName(), user?.getSwitchStatus())
                else{
                    val account = GoogleSignIn.getLastSignedInAccount(applicationContext)
                    updateAllFields(null,null,null,account?.givenName +" "+ account?.familyName,
                    null)
                }
            }.addOnFailureListener{
                val account = GoogleSignIn.getLastSignedInAccount(applicationContext)
                updateAllFields(null,null,null,account?.givenName + account?.familyName,
                null)
            }
    }


    override fun onClick(v: View?) {
        if (v == btnNext) {
            nextRegisterStage()
        }
    }

    private fun nextRegisterStage()
    {
        val fullNameText = etFullName?.text.toString()
        if( fullNameText == "")
        {
            etFullName?.error = "You must write a name"
            return
        }
        findViewById<RadioButton>(R.id.radioGlassesNo).error = null
        findViewById<RadioButton>(R.id.radioFemale).error= null
        val isGlasses = getIsGlasses(radioIsGlasses!!.checkedRadioButtonId,
            R.id.radioGlassesNo,
            R.id.radioGlassesYes
        )
        val gender = getGender(radioGender!!.checkedRadioButtonId, R.id.radioMale, R.id.radioFemale)
        if(isGlasses == null || gender == null)
            return
        val isSwitched = switchNotify?.isChecked == true
        val birthDate = getDate(datePicker!!).toString()
            uploadDataToFirebase(applicationContext,isGlasses,gender,birthDate,fullNameText, isSwitched)
            if(requestIntentFlag!! == RequestType.UPDATE) {
                val returnIntent = Intent()
                setResult(Activity.RESULT_OK, returnIntent);
                finish()
            }
        if (switchNotify?.isChecked == true) {
            updateNotificationSwitch(true, this)
        } else {
            updateNotificationSwitch(false, this)
        }
        val intent = Intent(this, TestActivity::class.java)
        startActivityForResult(intent, RC_TEST)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_TEST && resultCode == Activity.RESULT_OK)
        {
            val returnIntent = Intent()
            setResult(Activity.RESULT_OK, returnIntent);
            finish()
        }
    }

    private fun getIsGlasses(idRadioIsGlasses: Int, idRadioGlassesNo: Int, idRadioGlassesYes: Int): Boolean? {
        when(idRadioIsGlasses){
            idRadioGlassesNo -> return false
            idRadioGlassesYes -> return true
            -1 -> findViewById<RadioButton>(R.id.radioGlassesNo).error = "You must choose an option"
        }
        return null
    }



    private fun getGender(idRadioGender: Int, idRadioMale: Int, idRadioFemale: Int): Gender? {
        when(idRadioGender){
            idRadioMale -> return Gender.MALE
            idRadioFemale ->return Gender.FEMALE
            -1 -> findViewById<RadioButton>(R.id.radioFemale).error = "You must choose an option"
        }
        return null
    }


    private fun getDate(datePicker: DatePicker): Calendar {
        val day = datePicker.dayOfMonth
        val month = datePicker.month
        val year = datePicker.year

        val calendar = Calendar.getInstance()
        calendar[year, month] = day
        return calendar
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        menu?.findItem(R.id.itemDetails)?.isVisible   = false
        menu?.findItem(R.id.itemNewTest)?.isVisible   = false
        menu?.findItem(R.id.itemLog_out)?.isVisible   = false
        menu?.findItem(R.id.itemGoBack)?.isVisible   = true
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.itemGoBack -> {
                if(requestIntentFlag!! == RequestType.SIGN_IN)
                {
                    Toast.makeText(applicationContext,"Registration incomplete!",Toast.LENGTH_LONG).show()
                }
                val returnIntent = Intent()
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish()
            }
        }
        return true
    }
}