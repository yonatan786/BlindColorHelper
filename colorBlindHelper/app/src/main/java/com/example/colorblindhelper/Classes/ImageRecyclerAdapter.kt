package com.example.colorblindhelper.Classes

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.example.colorblindhelper.viewImg

public class ImageRecyclerAdapter(
    private val context: Context,
    private val filePath: ArrayList<String> = ArrayList<String>(),

) : BaseAdapter()
{

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
        viewImg (context,filePath[position],imgView)
        return imgView
    }

    override fun getItem(position: Int): Any {
        //TODO()
        return filePath[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return filePath.size
    }

}