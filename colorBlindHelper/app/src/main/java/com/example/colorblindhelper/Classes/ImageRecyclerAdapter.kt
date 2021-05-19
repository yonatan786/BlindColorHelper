package com.example.colorblindhelper.Classes

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.example.colorblindhelper.viewImg
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

public class ImageRecyclerAdapter(
    private val context: Context,
    private val fileNameList: ArrayList<String> = ArrayList<String>(),
    userName: String?
) : BaseAdapter()
{
    private val storageRef : StorageReference = FirebaseStorage.getInstance().reference.child("images/posts/"+ userName)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var imgView: ImageView? = null
        if(convertView == null)
        {
            imgView = ImageView(context)
            //imgView.layoutParams = GridView.LayoutPa
            imgView.scaleType = ImageView.ScaleType.CENTER_CROP
            val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            imgView.setLayoutParams(params)
            imgView.layoutParams.height = 600
        }
        else {
            imgView = convertView as ImageView
        }
        viewImg (context,storageRef,fileNameList[position],imgView)
        return imgView
    }

    override fun getItem(position: Int): Any {
        return fileNameList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return fileNameList.size
    }

}