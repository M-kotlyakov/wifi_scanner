package com.kotlyakov.wifiscanner.ui.ViewModel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kotlyakov.wifiscanner.wifi_manager.WIFiScanManager
import com.kotlyakov.wifiscanner.wifi_manager.model.WiFiResultData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Класс отвечает за логику обработки данных для UI
class MainActivityViewModel(
    private val wiFiScannerManager: WIFiScanManager,
    private val preferences: SharedPreferences,
) : ViewModel() {

    companion object {

        private const val PREF_KEY = "wifi_result_data"
        private const val PREF_KEY_LOCATION = "location"
    }

    // Работает как Publisher - Subscriber. Отправляет данные и на UI слое идет подписка и отображаются данные на UI
    private val _wifiScannerResultFlow = MutableStateFlow<Pair<String, List<WiFiResultData>>>(Pair("", emptyList()))
    val wifiScannerResultFlow: StateFlow<Pair<String, List<WiFiResultData>>> = _wifiScannerResultFlow

    // Работает как Publisher - Subscriber. Отправляет данные и на UI слое идет подписка и отображаются данные на UI
    private val _dataFromScan = MutableStateFlow(false)
    val dataFromScan: StateFlow<Boolean> =  _dataFromScan

    // Работает как Publisher - Subscriber. Отправляет данные и на UI слое идет подписка и отображаются данные на UI
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // сохраняем данные в SharedPreferences
    fun saveIntoSharedPref(
        location: String,
        listWifiResult: List<WiFiResultData>
    ) {
        // здесь в SharedPreferences сохраняем локацию, которую ввели
        preferences.edit().putString(PREF_KEY_LOCATION, location).apply()
        // здесь в SharedPreferences сохраняем список сетей
        preferences.edit().putString(PREF_KEY, listWifiResult.convertToJson()).apply()
    }

    // Получаем из SharedPreferences данные
    fun getFromSharedPref(): Pair<String, List<WiFiResultData>> {
        viewModelScope.launch { _dataFromScan.emit(false) }
        val location = preferences.getString(PREF_KEY_LOCATION, "") ?: ""
        val list = preferences.getString(PREF_KEY, null)?.convertToWiFiResultData() ?: emptyList()
        val pair = Pair(location, list)
        viewModelScope.launch { _wifiScannerResultFlow.emit(pair) }
        return pair
    }

    // метод отвечает за начало сканирования сетей
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

    // парсинг списка объект в json формат для сохранения результата в виде строки
    private fun List<WiFiResultData>.convertToJson(): String {
        return Gson().toJson(this)
    }

    // конвертирует строку json обратно в список объектов
    private fun String.convertToWiFiResultData(): List<WiFiResultData> {
        val type = object : TypeToken<List<WiFiResultData>>() {}.type
        return Gson().fromJson(this, type)
    }
}