package com.kotlyakov.wifiscanner.wifi_manager.model

// объект WiFi сети, в котором есть необходимиая информация для отображения их на UI
// свойства в этом клдассе будут отображаться на UI
data class WiFiResultData(
    val ssid: String,
    val rssi: Int,
    val macAddress: String
)
