package com.example.colorblindhelper

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

public class ImageAdapter(
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
        }
        else {
            imgView = convertView as ImageView
        }
        viewImg (context,storageRef,fileNameList[position],imgView)
        return imgView
    }

    override fun getItem(position: Int): Any? {
        return fileNameList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return fileNameList.size
    }

}