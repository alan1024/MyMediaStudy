package com.alan.mymediastudy.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.alan.mymediastudy.constant.Constants

class CustomView(context: Context?, attrs: AttributeSet) : View(context, attrs) {
    private var paint: Paint = Paint()
    private var bitmap: Bitmap?


    init {
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        bitmap = BitmapFactory.decodeFile(Constants.PATH) // 获取bitmap
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        // 不建议在onDraw做任何分配内存的操作
        bitmap?.apply {
            canvas?.drawBitmap(this, 0f, 0f, paint)
        }
    }

}