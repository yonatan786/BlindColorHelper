package com.example.colorblindhelper.ui.Tabs

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.colorblindhelper.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
//yonatan
/**
 * A simple [Fragment] subclass.
 * Use the [Profile.newInstance] factory method to
 * create an instance of this fragment.
 */
class Profile : Fragment(), AdapterView.OnItemClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var ImgViewProfile : ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val userName = getUserName(requireContext())
        view?.findViewById<TextView>(R.id.tvNoPhotos)?.let {
            showProfileGridView(userName!!,view?.findViewById<GridView>(R.id.gradeView),requireContext(),requireActivity(),
                it,view?.findViewById<Button>(R.id.btnUploadPhoto))
        }
        view?.findViewById<TextView>(R.id.tvUserName)?.text = getUserName(requireContext())
        view?.findViewById<GridView>(R.id.gradeView)?.onItemClickListener = this
        ImgViewProfile = view?.findViewById<ImageView>(R.id.ImgViewProfile)
        downloadImgViewProfile(requireContext(), getUserName(requireContext())!!,ImgViewProfile!!)
        view?.findViewById<TextView>(R.id.tvChangeProfilePhoto)?.setOnClickListener{
            if(checkReadWritePermissions(requireActivity(),requireContext())){
                val photoPickerIntent = Intent(Intent.ACTION_PICK)
                photoPickerIntent.type = "image/*"
                startActivityForResult(photoPickerIntent, 1)
            }
        }
    }
    override  fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1)
            if (resultCode == Activity.RESULT_OK) {
                imageUri = data?.data
                ImgViewProfile?.setImageURI(imageUri)
                uploadPictureToFirebaseStorage(requireContext(),null, imageUri,uploadType.PROFILE)

            }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment settings.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Profile().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val item = parent?.getItemAtPosition(position).toString().split("/")
        showDialog(position, item[item.size -1],requireContext(),requireActivity(),
            getUserName(requireContext())!!
        )
    }


}