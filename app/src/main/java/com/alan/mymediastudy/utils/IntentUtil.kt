package com.alan.mymediastudy.utils

import android.app.Activity
import android.content.Context
import android.content.Intent


/**
 * 启动一个Activity
 */
fun startAC(context: Context, cls: Class<out Activity>) {
    val intent = Intent().apply {
        setClass(context, cls)
    }
    context.startActivity(intent)
}