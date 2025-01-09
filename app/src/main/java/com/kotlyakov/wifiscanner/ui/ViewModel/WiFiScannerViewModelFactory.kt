package com.kotlyakov.wifiscanner.ui.ViewModel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kotlyakov.wifiscanner.wifi_manager.WIFiScanManager

// Паттерн фабрики для создания класса MainActivityViewModel
// ViweModel этто специальной класс в Android, который переживает смену конфигурации телефона: смену ориентации экрана (поворот)
// смена языка в системе
// он позволяет не затирать Runtime данные, которые еще не были сохранены в постоянное хранилище при смене конфигурации телефона
// Этот паттенр с постфиксом Factory говорит о том, что  мы создаем какой-то класс, который зависит от других параметров
// в данном случае другие параметры это wiFiScannerManager: WIFiScanManager и preferences: SharedPreferences
class WiFiScannerViewModelFactory(
    private val wiFiScannerManager: WIFiScanManager,
    private val preferences: SharedPreferences,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            return MainActivityViewModel(
                wiFiScannerManager = wiFiScannerManager,
                preferences = preferences
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}