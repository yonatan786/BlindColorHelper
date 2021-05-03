package com.example.colorblindhelper.ui.Tabs

import android.Manifest
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
        var isStop = false
        val btnStop = activity?.findViewById<Button>(R.id.btnStop)
        ActivityCompat.requestPermissions(
            activity!!,
            arrayOf(Manifest.permission.CAMERA),
            1);
        val x = colorDetector(activity!!,  uploadView, editCameraView);
        x.start(activity!!)
        btnStop?.setOnClickListener(){
            if(isStop == false) {
                btnStop.setText("continue")
                x.stop()
            }
            else {
                btnStop.setText("stop")
                x.start(activity!!)
            }
            isStop = !isStop
        }
    }

}


