package com.example.colorblindhelper.Activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.example.colorblindhelper.ClassifyBlindness
import com.example.colorblindhelper.R
import com.example.colorblindhelper.getUserName
import com.example.colorblindhelper.updateBlindType
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class QuestionTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_test)

        val btnPrev = findViewById<Button>(R.id.btnPrev)
        val btnNext = findViewById<Button>(R.id.btnNext)
        val text = findViewById<TextView>(R.id.plate_text)
        val image = findViewById<ImageView>(R.id.plate_image)
        val answer = findViewById<EditText>(R.id.answer)

        val answers = arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1)
        val normal = arrayOf(8, 6, 29, 5, 3, 74, 2, 97, 45, 16, 7, 73, 42, 26)
        val deficiency = arrayOf(3, 5, 70, 2, 5, 21)

        // index 0 is score for normal vision, index 1 is score for deficiency vision
        val correct_score = arrayOf(0, 0)

        // btnPrev.visibility = View.INVISIBLE
        answer.visibility = View.INVISIBLE
        image.setImageResource(R.drawable.r1)
        image.setTag("plate1")

        // what happens when we click on 'Previous' button:
        btnPrev.setOnClickListener {
            when (image.tag) {
                "plate1" -> {
                    val returnIntent = Intent()
                    setResult(Activity.RESULT_CANCELED, returnIntent);
                    finish()
                }
                "plate2" -> {
                    image.setTag("plate1")
                    image.setImageResource(R.drawable.r1)
                    text.text = "Example: This number is 12. Click 'Next' to continue."
                    answer.visibility = View.INVISIBLE
                }
                "plate3" -> {
                    image.setTag("plate2")
                    image.setImageResource(R.drawable.r2)
                }
                "plate4" -> {
                    image.setTag("plate3")
                    image.setImageResource(R.drawable.r3)
                }
                "plate5" -> {
                    image.setTag("plate4")
                    image.setImageResource(R.drawable.r4)
                }
                "plate6" -> {
                    image.setTag("plate5")
                    image.setImageResource(R.drawable.r5)
                }
                "plate7" -> {
                    image.setTag("plate6")
                    image.setImageResource(R.drawable.r6)
                }
                "plate8" -> {
                    image.setTag("plate7")
                    image.setImageResource(R.drawable.r7)
                }
                "plate9" -> {
                    image.setTag("plate8")
                    image.setImageResource(R.drawable.r8)
                }
                "plate10" -> {
                    image.setTag("plate9")
                    image.setImageResource(R.drawable.r9)
                }
                "plate11" -> {
                    image.setTag("plate10")
                    image.setImageResource(R.drawable.a1)
                }
                "plate12" -> {
                    image.setTag("plate11")
                    image.setImageResource(R.drawable.a2)
                }
                "plate13" -> {
                    image.setTag("plate12")
                    image.setImageResource(R.drawable.a3)
                }
                "plate14" -> {
                    image.setTag("plate13")
                    image.setImageResource(R.drawable.a4)
                }
                "plate15" -> {
                    image.setTag("plate14")
                    image.setImageResource(R.drawable.a5)
                    btnNext.text = "NEXT"
                }
            }
        }

        // what happens when we click on 'Next' button:
        btnNext.setOnClickListener {
            when (image.tag) {
                "plate1" -> {
                    image.setTag("plate2")
                    image.setImageResource(R.drawable.r2)
                    text.text = "What is the number on the plate?"
                    answer.visibility = View.VISIBLE
                }
                "plate2" -> {
                    image.setTag("plate3")
                    image.setImageResource(R.drawable.r3)

                    // set answer:
                    if (!answer.text.isEmpty()) {
                        answers[0] = Integer.parseInt(answer.text.toString())
                        answer.text.clear()
                    } else {
                        answers[0] = -1
                    }
                }
                "plate3" -> {
                    image.setTag("plate4")
                    image.setImageResource(R.drawable.r4)

                    if (!answer.text.isEmpty()) {
                        answers[1] = Integer.parseInt(answer.text.toString())
                        answer.text.clear()
                    } else {
                        answers[1] = -1
                    }
                }
                "plate4" -> {
                    image.setTag("plate5")
                    image.setImageResource(R.drawable.r5)

                    if (!answer.text.isEmpty()) {
                        answers[2] = Integer.parseInt(answer.text.toString())
                        answer.text.clear()
                    } else {
                        answers[2] = -1
                    }
                }
                "plate5" -> {
                    image.setTag("plate6")
                    image.setImageResource(R.drawable.r6)

                    if (!answer.text.isEmpty()) {
                        answers[3] = Integer.parseInt(answer.text.toString())
                        answer.text.clear()
                    } else {
                        answers[3] = -1
                    }
                }
                "plate6" -> {
                    image.setTag("plate7")
                    image.setImageResource(R.drawable.r7)

                    if (!answer.text.isEmpty()) {
                        answers[4] = Integer.parseInt(answer.text.toString())
                        answer.text.clear()
                    } else {
                        answers[4] = -1
                    }
                }
                "plate7" -> {
                    image.setTag("plate8")
                    image.setImageResource(R.drawable.r8)

                    if (!answer.text.isEmpty()) {
                        answers[5] = Integer.parseInt(answer.text.toString())
                        answer.text.clear()
                    } else {
                        answers[5] = -1
                    }
                }
                "plate8" -> {
                    image.setTag("plate9")
                    image.setImageResource(R.drawable.r9)

                    if (!answer.text.isEmpty()) {
                        answers[6] = Integer.parseInt(answer.text.toString())
                        answer.text.clear()
                    } else {
                        answers[6] = -1
                    }
                }
                "plate9" -> {
                    image.setTag("plate10")
                    image.setImageResource(R.drawable.a1)

                    if (!answer.text.isEmpty()) {
                        answers[7] = Integer.parseInt(answer.text.toString())
                        answer.text.clear()
                    } else {
                        answers[7] = -1
                    }
                }
                "plate10" -> {
                    image.setTag("plate11")
                    image.setImageResource(R.drawable.a2)

                    if (!answer.text.isEmpty()) {
                        answers[8] = Integer.parseInt(answer.text.toString())
                        answer.text.clear()
                    } else {
                        answers[8] = -1
                    }
                }
                "plate11" -> {
                    image.setTag("plate12")
                    image.setImageResource(R.drawable.a3)

                    if (!answer.text.isEmpty()) {
                        answers[9] = Integer.parseInt(answer.text.toString())
                        answer.text.clear()
                    } else {
                        answers[9] = -1
                    }
                }
                "plate12" -> {
                    image.setTag("plate13")
                    image.setImageResource(R.drawable.a4)

                    if (!answer.text.isEmpty()) {
                        answers[10] = Integer.parseInt(answer.text.toString())
                        answer.text.clear()
                    } else {
                        answers[10] = -1
                    }
                }
                "plate13" -> {
                    image.setTag("plate14")
                    image.setImageResource(R.drawable.a5)

                    if (!answer.text.isEmpty()) {
                        answers[11] = Integer.parseInt(answer.text.toString())
                        answer.text.clear()
                    } else {
                        answers[11] = -1
                    }
                }
                "plate14" -> {
                    image.setTag("plate15")
                    image.setImageResource(R.drawable.a6)
                    btnNext.text = "FINISH"

                    if (!answer.text.isEmpty()) {
                        answers[12] = Integer.parseInt(answer.text.toString())
                        answer.text.clear()
                    } else {
                        answers[12] = -1
                    }
                }
                "plate15" -> {
                    if (!answer.text.isEmpty()) {
                        answers[13] = Integer.parseInt(answer.text.toString())
                        answer.text.clear()
                    } else {
                        answers[13] = -1
                    }

                    // calculate correct score and classify the vision:
                    for (i in 0..5) {
                        if (answers[i] == normal[i])
                            correct_score[0]++
                        if (answers[i] == deficiency[i] || answers[i] == -1)
                            correct_score[1]++
                    }
                    for (i in 6..11) {
                        if (answers[i] == normal[i])
                            correct_score[0]++
                        else
                            correct_score[1]++
                    }

                    var classification = "Not classified"
                    var classificationText = "UNCLASSIFIED"
                    if (correct_score[0] / 12.0 >= 0.8 && answers[12] == normal[12] && answers[13] == normal[13]){
                        classification = "Normal"
                        classificationText = "NORMAL"
                    }
                    if (correct_score[1] / 12.0 >= 0.8) {
                        if (answers[12] == 2 && answers[13] == 6) {
                            classification = "Red-Blind/Protanopia"
                            classificationText = "RED_BLIND"
                        }
                        if (answers[12] == 4 && answers[13] == 2) {
                            classification = "Green-Blind/Deuteranopia"
                            classificationText = "GREEN_BLIND"
                        }
                    }

                    // checks if vision is Monochromacy (black-white blindness)
                    var check = 0
                    for (e in answers) {
                        if (e != -1)
                            check = 1
                    }
                    if (check == 0)
                        classification = "Monochromacy"
                        classificationText = "BLACK_WHITE_BLIND"
                    // show result dialog:
                    updateClassificationOnFirebase(classification)
                    updateBlindType(classificationText,this)
                    showDialog(classification)


                }
            }
        }

    }

    private fun updateClassificationOnFirebase(blindType: String) {
        Firebase.firestore.collection("users").document(getUserName(applicationContext)!!).update("blindType",getClassification(blindType))
    }

    private fun getClassification(blindType: String): ClassifyBlindness {
        when(blindType)
        {
            "Normal" -> return ClassifyBlindness.NORMAL
            "Monochromacy" -> return ClassifyBlindness.BLACK_WHITE_BLIND
            "Green-Blind/Deuteranopia" -> return ClassifyBlindness.GREEN_BLIND
            "Red-Blind/Protanopia" -> return ClassifyBlindness.RED_BLIND
            "Not classified" -> return ClassifyBlindness.UNCLASSIFIED
        }
        return ClassifyBlindness.UNCLASSIFIED
    }

    public fun showDialog(typeBlind: String)
    {
        val dialog : Dialog = Dialog(this)
        dialog.setContentView(R.layout.result)
        dialog.findViewById<TextView>(R.id.resText).text = typeBlind
        dialog.findViewById<Button>(R.id.btnPopup).setOnClickListener(View.OnClickListener{
            dialog.dismiss()
            val returnIntent = Intent()
            setResult(Activity.RESULT_OK, returnIntent);
            //TODO: update data in firestore !
            finish()
        })
        dialog.show()
    }
}