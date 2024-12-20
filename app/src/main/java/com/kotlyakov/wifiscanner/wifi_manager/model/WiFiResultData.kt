package com.kotlyakov.wifiscanner.wifi_manager.model

import android.net.MacAddress

/**
 * @author m.kotlykov
 */
data class WiFiResultData(
    val ssid: String,
    val rssi: Int,
    val macAddress: String
)
