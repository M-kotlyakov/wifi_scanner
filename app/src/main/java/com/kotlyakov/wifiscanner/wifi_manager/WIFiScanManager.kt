package com.kotlyakov.wifiscanner.wifi_manager

import android.content.Context
import android.net.wifi.WifiManager
import com.kotlyakov.wifiscanner.wifi_manager.model.WiFiResultData

// интерфейс здесь служит как контракт, это сделано для удобства
interface WIFiScanManager {

    // в интерфейсе определяется метод
    suspend fun getWifiResults(): List<WiFiResultData>
}

// класс WiFiManager для управления работы сканирования и получения результата доступных сетей
// данный класс реализует интерфейс выше
// в дальнейшем использовании будет использоваться экземпаляр интерфейса WIFiScanManager, но реализация будет подставляться WIFiScanManagerImpl
internal class WIFiScanManagerImpl(private val context: Context) : WIFiScanManager {

    // переменна, которая инициализируется с помощью системного апи, то есть системного класса WifiManager
    private val wifiManager: WifiManager by lazy {
        context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    // метод определяет если WiFi включен, если нет, то выставляет значение в true, тем самым включая WiFi
    // для дальнейшего сканирования
    override suspend fun getWifiResults(): List<WiFiResultData> {
        val results = mutableListOf<WiFiResultData>()

        if (!wifiManager.isWifiEnabled) {
            wifiManager.isWifiEnabled = true
        }

        // производится сканирование сети
        wifiManager.startScan()
        // присваивается результат в виде списка
        val scanResults = wifiManager.scanResults
        // затем в цикле перебираются все элементы списка и записываются в нужный нам объект с требуемыми параметрами для отображения на UI
        for (scanResult in scanResults) {
            results.add(
                WiFiResultData(
                    ssid = scanResult.SSID,
                    rssi = scanResult.level,
                    macAddress = scanResult.BSSID
                )
            )
        }
        // возвращаем сам список объектов наших сетей
        return results
    }
}