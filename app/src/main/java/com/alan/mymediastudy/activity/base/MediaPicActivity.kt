package com.alan.mymediastudy.activity.base

import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.view.SurfaceHolder
import com.alan.mymediastudy.activity.BaseActivity
import com.alan.mymediastudy.constant.Constants
import com.alan.mymediastudy.databinding.ActivityMediaPicBinding


/**
 * @Auth: xujm
 * @Date: 2022/8/3
 * @Desc: Android 音视频开发(一) : 通过三种方式绘制图片
 */
class MediaPicActivity : BaseActivity<ActivityMediaPicBinding>() {

    override fun bindViewBinding(): ActivityMediaPicBinding {
        binding = ActivityMediaPicBinding.inflate(layoutInflater)
        return binding
    }

    override fun init() {
        val bitmap = BitmapFactory.decodeFile(Constants.PATH)
        binding.imageView.setImageBitmap(bitmap)

        binding.surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                val paint = Paint().apply {
                    isAntiAlias = true
                    style = Paint.Style.STROKE
                }

                //1、先锁定当前surfaceView的画布
                val canvas: Canvas = holder.lockCanvas()
                //2、执行绘制操作
                canvas.drawBitmap(bitmap, 0f, 0f, paint)
                //3、解除锁定并显示在界面上
                holder.unlockCanvasAndPost(canvas)
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {

            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {

            }

        })


    }

}