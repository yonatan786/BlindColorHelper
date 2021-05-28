package com.example.colorblindhelper.ui.Tabs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.colorblindhelper.*
import com.example.colorblindhelper.Activities.viewOtherProfileActivity
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [searchUser.newInstance] factory method to
 * create an instance of this fragment.
 */
class searchUser : Fragment(), AdapterView.OnItemClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var rvUsersList: RecyclerView? = null
    private var gridView: GridView? = null
    var adapter: FirestoreRecyclerAdapter<UserModel, ViewHolder>? = null
    var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onStart() {
        super.onStart()
        firebaseSearch("")


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_search_user, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val searchView = view?.findViewById<SearchView>(R.id.searchView)
        rvUsersList = view?.findViewById(R.id.rvUsersList)
        gridView = view?.findViewById<GridView>(R.id.gridView)
        gridView?.setOnItemClickListener(this)
        firebaseSearch("")

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                firebaseSearch(query)
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                firebaseSearch(query)
                return true
            }
        });
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment searchUser.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            searchUser().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun firebaseSearch(searchText: String?) {
        if (searchText == "") {
            gridView?.visibility = View.VISIBLE
            rvUsersList?.visibility = View.GONE
            showFeedGridView(gridView,requireContext(),requireActivity())
            return
        }
        rvUsersList?.visibility = View.VISIBLE
        gridView?.visibility = View.GONE
        val query = FirebaseFirestore.getInstance().collection("users").whereNotEqualTo(
            "userName",
            getUserName(requireContext())
        ).orderBy("userName").startAt(searchText).endAt(searchText + "\uf8ff")
        val options = FirestoreRecyclerOptions.Builder<UserModel>()
            .setQuery(query, UserModel::class.java)
            .setLifecycleOwner(this)
            .build()
        adapter = object : FirestoreRecyclerAdapter<UserModel, ViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                return ViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.search_row, parent, false)
                )
            }

            override fun onBindViewHolder(holder: ViewHolder, position: Int, model: UserModel) {
                holder.tvUserName?.text = model.getUserName()
                holder.tvFullName?.text = model.getFullName()
                holder.tvStatus?.text = "Not Friends"
                holder.tvStatus?.let {
                    getStatusText(model.getUserName(), getUserName(requireContext())!!, it)
                }
                context?.let {
                    downloadImgViewProfile(
                        it,
                        model.getUserName(),
                        holder.imgViewProfile!!
                    )
                }
                holder.itemView.setOnClickListener {
                    val intent = Intent(context, viewOtherProfileActivity::class.java)
                    intent.putExtra("userNameProfile", model.getUserName())
                    startActivity(intent)
                }
            }
        }
        rvUsersList?.setLayoutManager(LinearLayoutManager(activity));
        rvUsersList?.adapter = adapter
    }

    private fun getStatusText(userName: String, currentUserName: String,tvStatus:TextView){
        Firebase.firestore.collection("requests").document(userName).collection("newRequests").document(currentUserName)
            .addSnapshotListener { snapshot, e ->
                if (snapshot?.exists() == true) {
                    if (snapshot["status"] == "WAITING") {
                        tvStatus.text = "Waiting"
                    } else if (snapshot["status"] == "FRIENDS")
                    {
                        tvStatus.text = "Friends"

                    }
                }

            }
        Firebase.firestore.collection("requests").document(currentUserName).collection("newRequests").document(userName)
            .addSnapshotListener{ snapshot, e ->
                if (snapshot?.exists() == true && snapshot["status"] == "WAITING") {
                    tvStatus.text="Requested Sent"
                }
            }
    }


    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val item = parent?.getItemAtPosition(position).toString().split("/")
        showDialog(
            position, item[item.size - 1], requireContext(), requireActivity(),
            item[item.size - 2]
        )
    }
}
class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {


    public var tvUserName : TextView? = null
    public var tvFullName : TextView? = null
    public var tvStatus : TextView? = null
    public var imgViewProfile: ImageView? = null
    init{
        tvUserName = view.findViewById(R.id.tvUserName)
        imgViewProfile = view.findViewById(R.id.ImgViewProfile)
        tvFullName = view.findViewById(R.id.tvFullName)
        tvStatus = view.findViewById(R.id.tvStatus)
    }
}
