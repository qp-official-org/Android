package com.example.qp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.qp.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {

    lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchBackIv.setOnClickListener{
            startActivity(Intent(this@SearchActivity, MainActivity::class.java))
        }

    }

}