package com.kotlyakov.wifiscanner.ui.ViewModel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kotlyakov.wifiscanner.wifi_manager.WIFiScanManager

/**
 * @author m.kotlykov
 */
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