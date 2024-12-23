package com.kotlyakov.wifiscanner.ui.recycler

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.kotlyakov.wifiscanner.databinding.WifiItemBinding
import com.kotlyakov.wifiscanner.wifi_manager.model.WiFiResultData

class WiFiScannerAdapter : RecyclerView.Adapter<WiFiScannerViewHolder>() {

    // в режиме рантайма (реального времени работы приложения, пока оно запущено) хранится список сетей
    private var wifiScannerData: List<WiFiResultData> = emptyList()

    // Здесь создается элемент строчки из таблицы
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WiFiScannerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = WifiItemBinding.inflate(inflater)
        return WiFiScannerViewHolder(binding)
    }

    // связываем данные с элементами UI
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(holder: WiFiScannerViewHolder, position: Int) {
        holder.bind(wifiScannerData[position])
    }

    // Это возвращает размер списка
    override fun getItemCount(): Int = wifiScannerData.size

    // после сканирования вызываем этот метод, чтобы добавить новые данные
    fun setData(listScannerData: List<WiFiResultData>) {
        wifiScannerData = listScannerData
    }

    // Возвращаем сам спиоск сетей
    fun getListScannerData(): List<WiFiResultData> {
        return wifiScannerData
    }
}
