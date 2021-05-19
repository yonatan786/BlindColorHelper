package com.example.colorblindhelper.ui.Tabs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.colorblindhelper.*
import com.example.colorblindhelper.Activities.viewOtherProfileActivity
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [searchUser.newInstance] factory method to
 * create an instance of this fragment.
 */
class searchUser : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var rvUsersList : RecyclerView? = null
    var adapter : FirestoreRecyclerAdapter<UserModel, ViewHolder>? = null
    var mAuth : FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onStart() {
        super.onStart()
        showFriendsList()

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
        if (searchText == "")
            showFriendsList()
        val query  = FirebaseFirestore.getInstance().collection("users").whereNotEqualTo("userName",
            getUserName(requireContext())).orderBy("userName").startAt(searchText).endAt(searchText+"\uf8ff")
        val options = FirestoreRecyclerOptions.Builder<UserModel>()
            .setQuery(query,UserModel::class.java)
            .setLifecycleOwner(this)
            .build()
        adapter = object : FirestoreRecyclerAdapter<UserModel, ViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                return ViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.search_row, parent, false))
            }

            override fun onBindViewHolder(holder: ViewHolder, position: Int, model: UserModel) {
                holder.tvUserName?.text = model.getUserName()
                context?.let { downloadImgViewProfile(it,model.getUserName(),holder.imgViewProfile!!) }
                holder.itemView.setOnClickListener {
                    val intent = Intent(context, viewOtherProfileActivity::class.java)
                    intent.putExtra("userNameProfile",model.getUserName())
                    startActivity(intent)
                }
            }
        }
        rvUsersList?.setLayoutManager(LinearLayoutManager(activity));
        rvUsersList?.adapter = adapter
    }


    private fun showFriendsList() {
        val query = FirebaseFirestore.getInstance().collection("requests/"+getUserName(requireContext())+"/newRequests")
            .whereEqualTo("status", "FRIENDS")
        val options = FirestoreRecyclerOptions.Builder<RequestFriendship>()
            .setQuery(query, RequestFriendship::class.java)
            .setLifecycleOwner(this)
            .build()
        val adapter = object : FirestoreRecyclerAdapter<RequestFriendship, ViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                return ViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.search_row, parent, false))
            }

            override fun onBindViewHolder(holder: ViewHolder, position: Int, model: RequestFriendship) {
                val userName = if (model.userSend == getUserName(requireContext())) {
                    model.userGet
                } else {
                    model.userSend
                }
                holder.tvUserName?.text = userName
                context?.let { downloadImgViewProfile(it,userName,holder.imgViewProfile!!) }
                holder.itemView.setOnClickListener {
                    val intent = Intent(context, viewOtherProfileActivity::class.java)
                    intent.putExtra("userNameProfile",userName)
                    startActivity(intent)
                }
            }
        }
        rvUsersList?.setLayoutManager(LinearLayoutManager(activity));
        rvUsersList?.adapter = adapter
    }

}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {


    public var tvUserName : TextView? = null
    public var imgViewProfile: ImageView? = null
    init{
        tvUserName = view.findViewById(R.id.tvUserName)
        imgViewProfile = view.findViewById(R.id.ImgViewProfile)

    }
}
