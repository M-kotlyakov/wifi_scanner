package com.kotlyakov.wifiscanner.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kotlyakov.wifiscanner.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
//    private val wifiScannerAdapter by lazy { WiFiScannerAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}