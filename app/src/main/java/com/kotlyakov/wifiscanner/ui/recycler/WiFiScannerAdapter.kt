package com.kotlyakov.wifiscanner.ui.recycler

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.kotlyakov.wifiscanner.databinding.WifiItemBinding
import com.kotlyakov.wifiscanner.wifi_manager.model.WiFiResultData

class WiFiScannerAdapter : RecyclerView.Adapter<WiFiScannerViewHolder>() {

    private var wifiScannerData: List<WiFiResultData> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WiFiScannerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = WifiItemBinding.inflate(inflater)
        return WiFiScannerViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(holder: WiFiScannerViewHolder, position: Int) {
        holder.bind(wifiScannerData[position])
    }

    override fun getItemCount(): Int = wifiScannerData.size

    fun setData(listScannerData: List<WiFiResultData>) {
        wifiScannerData = listScannerData
    }

    fun getListScannerData(): List<WiFiResultData> {
        return wifiScannerData
    }
}
