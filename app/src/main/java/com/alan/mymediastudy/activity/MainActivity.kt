package com.alan.mymediastudy.activity

import android.Manifest
import androidx.activity.result.contract.ActivityResultContracts
import com.alan.mymediastudy.activity.base.MediaPicActivity
import com.alan.mymediastudy.databinding.ActivityMainBinding
import com.alan.mymediastudy.utils.startAC

class MainActivity : BaseActivity<ActivityMainBinding>() {


    override fun bindViewBinding(): ActivityMainBinding {
        binding = ActivityMainBinding.inflate(layoutInflater)
        return binding
    }

    override fun init() {
        val launcher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {

            }


        binding.btPermission.setOnClickListener {
            val arrayList = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            )
            launcher.launch(arrayList)
        }

        binding.btPic.setOnClickListener {
            startAC(this, MediaPicActivity::class.java)
        }

    }


}