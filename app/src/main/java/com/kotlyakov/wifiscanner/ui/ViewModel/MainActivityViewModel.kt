package com.kotlyakov.wifiscanner.ui.ViewModel

import android.content.SharedPreferences
import android.net.MacAddress
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kotlyakov.wifiscanner.wifi_manager.WIFiScanManager
import com.kotlyakov.wifiscanner.wifi_manager.model.WiFiResultData
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * @author m.kotlykov
 */
class MainActivityViewModel(
    private val wiFiScannerManager: WIFiScanManager,
    private val preferences: SharedPreferences,
) : ViewModel() {

    companion object {

        private const val PREF_KEY = "wifi_result_data"
        private const val PREF_KEY_LOCATION = "location"
    }

    private val _wifiScannerResultFlow = MutableStateFlow<Pair<String, List<WiFiResultData>>>(Pair("", emptyList()))
    val wifiScannerResultFlow: StateFlow<Pair<String, List<WiFiResultData>>> = _wifiScannerResultFlow

    private val _dataFromScan = MutableStateFlow(false)
    val dataFromScan: StateFlow<Boolean> =  _dataFromScan

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun saveIntoSharedPref(
        location: String,
        listWifiResult: List<WiFiResultData>
    ) {
        preferences.edit().putString(PREF_KEY_LOCATION, location).apply()
        preferences.edit().putString(PREF_KEY, listWifiResult.convertToJson()).apply()
    }

    fun getFromSharedPref(): Pair<String, List<WiFiResultData>> {
        viewModelScope.launch { _dataFromScan.emit(false) }
        val location = preferences.getString(PREF_KEY_LOCATION, "") ?: ""
        val list = preferences.getString(PREF_KEY, null)?.convertToWiFiResultData() ?: emptyList()
        val pair = Pair(location, list)
        viewModelScope.launch { _wifiScannerResultFlow.emit(pair) }
        return pair
    }

    fun startScanWifi(location: String) {
        viewModelScope.launch {
            _dataFromScan.emit(true)
            _isLoading.emit(true)
            delay(2_000)
            _isLoading.emit(false)
            val wifiResult = wiFiScannerManager.getWifiResults()
            val pair = Pair(location, wifiResult)
            _wifiScannerResultFlow.emit(pair)
        }
    }

    private fun List<WiFiResultData>.convertToJson(): String {
        return Gson().toJson(this)
    }

    private fun String.convertToWiFiResultData(): List<WiFiResultData> {
        val type = object : TypeToken<List<WiFiResultData>>() {}.type
        return Gson().fromJson(this, type)
    }
}