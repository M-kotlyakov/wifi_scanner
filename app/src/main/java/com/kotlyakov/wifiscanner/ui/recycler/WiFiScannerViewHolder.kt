package com.kotlyakov.wifiscanner.ui.recycler

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.kotlyakov.wifiscanner.databinding.WifiItemBinding
import com.kotlyakov.wifiscanner.wifi_manager.model.WiFiResultData

// Отвечает за связку данных с UI элементами
class WiFiScannerViewHolder(
    private val binding: WifiItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    @RequiresApi(Build.VERSION_CODES.P)
    fun bind(wiFiResultData: WiFiResultData) = with(binding) {
        macAddress.text = wiFiResultData.macAddress
        ssid.text = wiFiResultData.ssid
        rssi.text = wiFiResultData.rssi.toString()
    }
}
