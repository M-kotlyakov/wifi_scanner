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
    private val wiFiScannerManager: WIFiScanManager, // передаем WIFiScanManager
    private val preferences: SharedPreferences,      // и передаем SharedPreferences из
) : ViewModel() {

    companion object {

        // константы, которые служат в качестве ключей для хранения данных по ним в SharedPreferences
        private const val PREF_KEY = "wifi_result_data"
        private const val PREF_KEY_LOCATION = "location"
    }

    // Работает как Publisher - Subscriber. Отправляет данные и на UI слое идет подписка и отображаются данные на UI
    // переменная с нижним подчеркиванием служит как внутрення изменяемая переменная, которая недоступна за пределами этого класса
    // за счет инкапсуляции (области видимости) - private
    private val _wifiScannerResultFlow = MutableStateFlow<Pair<String, List<WiFiResultData>>>(Pair("", emptyList()))
    // переменная с таким же названием без нижнего подчеркивания вначале является пубюличной и доступной за пределами этого класса
    // но она неизменяемя ии служит для того, чтобы подписываться на изменения _wifiScannerResultFlow без возможности
    // менять это значение за пределами этого класса, он служит как наблюдатель
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
        // метод apply() сохраняет изменения в SharedPreferences
        preferences.edit().putString(PREF_KEY_LOCATION, location).apply()
        // здесь в SharedPreferences сохраняем список сетей
        preferences.edit().putString(PREF_KEY, listWifiResult.convertToJson()).apply()
    }

    // Получаем из SharedPreferences данные
    fun getFromSharedPref(): Pair<String, List<WiFiResultData>> {
        // асинхронно, не замораживая UI, отправляем данные на UI
        viewModelScope.launch { _dataFromScan.emit(false) }
        // получаем данные о локации по ключу из SharedPreferences
        val location = preferences.getString(PREF_KEY_LOCATION, "") ?: ""
        // получаем список объектов, конвертируя из формата JSON в формат List<WiFiResultData>
        val list = preferences.getString(PREF_KEY, null)?.convertToWiFiResultData() ?: emptyList()
        // Сохраняем данные в виде пары
        val pair = Pair(location, list)
        // асинхронно, не замораживая UI, отправляем данные на на UI слой и там идет подписка на эти изменения
        viewModelScope.launch { _wifiScannerResultFlow.emit(pair) }
        // возвращаем объект Pair, который хранит в себе локацию(где были сканированы сети) и список сетей
        return pair
    }

    // метод отвечает за начало сканирования сетей
    fun startScanWifi(location: String) {
        viewModelScope.launch {
            // отправляем на UI, что данные отображаются от сканирования, а не из SharedPref
            _dataFromScan.emit(true)
            // отправляем на UI, что идет загрузка
            _isLoading.emit(true)
            // выдерживаем загрузку в 2 секунды
            delay(2_000)
            // отправляем на UI, что загрузка заверешна
            _isLoading.emit(false)
            // получаем данные с написанного нами класса WiFiScannerManager - список сканированных сетей
            val wifiResult = wiFiScannerManager.getWifiResults()
            // снова сохраняем все в виде пары
            val pair = Pair(location, wifiResult)
            // отправляем эти данные на UI слой
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