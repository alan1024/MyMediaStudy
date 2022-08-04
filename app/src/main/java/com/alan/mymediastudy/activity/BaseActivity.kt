package com.alan.mymediastudy.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<Binding : ViewBinding> : AppCompatActivity() {
    protected lateinit var binding: Binding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bindLayout())

        init()
    }

    private fun bindLayout(): View {
        binding = bindViewBinding()
        return binding.root
    }

    /**
     * 绑定ViewBinding
     * @return Binding
     */
    abstract fun bindViewBinding(): Binding

    abstract fun init()
}