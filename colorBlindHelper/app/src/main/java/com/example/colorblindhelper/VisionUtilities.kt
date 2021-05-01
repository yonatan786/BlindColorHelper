package com.example.colorblindhelper

import android.graphics.Bitmap
import android.graphics.Color
import android.widget.ImageView
import java.util.*

public fun getEditedImg(bitmap: Bitmap,w:Int,h:Int,cameraPreview:ImageView?,editCameraPreview:ImageView)
{
    val result = Bitmap.createBitmap(w, h, bitmap.config)
    val pixels = IntArray(w * h)
    //get pixels
    bitmap.getPixels(pixels, 0, w, 0, 0, w, h)
    for (x in pixels.indices)
        if(Color.green(pixels[x]) >200 && Color.red(pixels[x]) > 200 && Color.blue(pixels[x]) >200)
            pixels[x] = Color.BLACK
    // create result bitmap output
    result.setPixels(pixels, 0, w, 0, 0, w, h)
    cameraPreview?.setImageBitmap(bitmap)
    editCameraPreview.setImageBitmap(result)
}