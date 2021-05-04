package com.example.colorblindhelper.ui.Tabs

import android.Manifest
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import com.example.colorblindhelper.R
import com.example.colorblindhelper.colorDetector

import androidx.lifecycle.ViewModel
import com.example.colorblindhelper.ViewMyProfile

var x : colorDetector? = null
class LiveChangeViewModel : ViewModel() {
    // TODO: Implement the ViewModel
}
class LiveChangeFragment : Fragment() {

    companion object {
        fun newInstance() = LiveChangeFragment()
    }

    private lateinit var viewModel: LiveChangeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.livechange_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LiveChangeViewModel::class.java)
        val uploadView = view?.findViewById<ImageView>(R.id.cameraView)
        val editCameraView = view?.findViewById<ImageView>(R.id.editCameraView)
        ActivityCompat.requestPermissions(
            activity!!,
            arrayOf(Manifest.permission.CAMERA),
            1)
        val btnStop = view?.findViewById<Button>(R.id.btnStop)
        val btnSavePicture = view?.findViewById<Button>(R.id.btnSavePicture)
        val btnUploadPicture = view?.findViewById<Button>(R.id.btnUploadPicture)

        var isStop = true
        x = colorDetector(activity!!,
            uploadView,
            editCameraView
        )
        btnUploadPicture?.setOnClickListener{
            x!!.uploadImageToFirebase()
            val intent = Intent(context, ViewMyProfile::class.java)
            startActivity(intent)
        }
        btnSavePicture?.setOnClickListener{
            x!!.saveImageToStorage()
        }
        btnStop?.setOnClickListener {
            if(!isStop) {
                btnStop.text = "continue"
                btnSavePicture?.visibility = View.VISIBLE
                btnUploadPicture?.visibility = View.VISIBLE
                x!!.stop()
            }
            else {
                btnStop.text = "stop"
                btnSavePicture?.visibility = View.INVISIBLE
                btnUploadPicture?.visibility = View.GONE
                x!!.start(activity!!)
            }
            isStop = !isStop
        }

    }

    override fun onStop() {
        super.onStop()
        x?.stop()
    }

}


