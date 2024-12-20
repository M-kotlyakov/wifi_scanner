package com.kotlyakov.wifiscanner.wifi_manager

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.net.MacAddress
import android.net.wifi.WifiManager
import com.kotlyakov.wifiscanner.wifi_manager.model.WiFiResultData

/**
 * @author m.kotlyakov
 */
interface WIFiScanManager {

    suspend fun getWifiResults(): List<WiFiResultData>
}

internal class WIFiScanManagerImpl(private val context: Context) : WIFiScanManager {

    private val wifiManager: WifiManager by lazy {
        context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    override suspend fun getWifiResults(): List<WiFiResultData> {
        val results = mutableListOf<WiFiResultData>()

        if (!wifiManager.isWifiEnabled) {
            wifiManager.isWifiEnabled = true
        }

        wifiManager.startScan()
        val scanResults = wifiManager.scanResults
        for (scanResult in scanResults) {
            results.add(
                WiFiResultData(
                    ssid = scanResult.SSID,
                    rssi = scanResult.level,
                    macAddress = scanResult.BSSID
                )
            )
        }
        return results
    }
}