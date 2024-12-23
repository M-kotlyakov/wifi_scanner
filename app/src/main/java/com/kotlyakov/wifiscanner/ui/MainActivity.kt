package com.kotlyakov.wifiscanner.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlyakov.wifiscanner.R
import com.kotlyakov.wifiscanner.databinding.ActivityMainBinding
import com.kotlyakov.wifiscanner.ui.ViewModel.MainActivityViewModel
import com.kotlyakov.wifiscanner.ui.ViewModel.WiFiScannerViewModelFactory
import com.kotlyakov.wifiscanner.ui.recycler.WiFiScannerAdapter
import com.kotlyakov.wifiscanner.wifi_manager.WIFiScanManagerImpl
import kotlinx.coroutines.launch

// основной класс экрана
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel

    private val wifiScannerAdapter by lazy {
        WiFiScannerAdapter()
    }
    private val wifiManager by lazy { WIFiScanManagerImpl(this) }
    private val pref by lazy { getPreferences(MODE_PRIVATE) }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecycler()
        initViewModel()
        observeWiFiResult()
        observeLoadingState()
        handleSaveClickButton()
        handleSeeClickButton()
        handleScanClickButton()
        handleSaveInputtedText()
        observeFromDataState()
    }

    // системный метод для запроса необходимых разрешений для полного функционирования приложения
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                viewModel.startScanWifi(binding.location.text.toString())
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // настройка списка. Устанавливаем нужный лэйаут
    private fun setupRecycler() = with(binding.recycler) {
        adapter = wifiScannerAdapter
        layoutManager = LinearLayoutManager(this@MainActivity)
    }

    // инициализация MainActivityViewModel
    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            WiFiScannerViewModelFactory(wifiManager, pref)
        )[MainActivityViewModel::class.java]
    }

    // обрабатываем нажатие на кнопку при сохранении локации
    private fun handleSaveInputtedText() {
        binding.saveTextBtn.setOnClickListener {
            if (binding.portEditText.text.isNotEmpty()) {
                binding.location.visibility = View.VISIBLE
                val inputtedText = binding.portEditText.text
                binding.location.text = getString(R.string.title_inputted_port, inputtedText)
            } else {
                Toast.makeText(this, "Заполните локацию", Toast.LENGTH_SHORT).show()
            }

        }
    }

    // обрабатываем нажатие на кнопку при сохранении всех данных в SharedPreferences
    private fun handleSaveClickButton() {
        binding.saveButton.setOnClickListener {
            val listWifiResult = wifiScannerAdapter.getListScannerData()
            if (binding.portEditText.text.isNotEmpty() && listWifiResult.isNotEmpty()) {
                viewModel.saveIntoSharedPref(binding.location.text.toString(), listWifiResult)
                Toast.makeText(this, "Данные сохранены", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Введите локацию и сканируйте сеть", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // обрабатываем нажатие на кнопку для сканирования сетей
    private fun handleScanClickButton() {
        binding.scannerButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
            } else {
                viewModel.startScanWifi(binding.location.text.toString())
            }
        }
    }

    // обрабатываем нажатие на кнопку для просмотра информации
    private fun handleSeeClickButton() {
        binding.location.visibility = View.VISIBLE
        binding.recycler.visibility = View.VISIBLE
        binding.checkButton.setOnClickListener {
            val listSavedResult = viewModel.getFromSharedPref()
            if (listSavedResult.second.isNotEmpty() && listSavedResult.first.isNotEmpty()) {
                binding.location.text = listSavedResult.first
                wifiScannerAdapter.setData(listSavedResult.second)
                wifiScannerAdapter.notifyDataSetChanged()
            } else {
                Toast.makeText(this, R.string.empty_text_wifi, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // подписываемся на публикуемые данные и отображаем список полученных сетей
    private fun observeWiFiResult() {
        lifecycleScope.launch {
            this@MainActivity.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.wifiScannerResultFlow.collect { wifiResult ->
                    binding.location.text = wifiResult.first
                    wifiScannerAdapter.setData(wifiResult.second)
                }
            }
        }
    }

    // подписываемся на публикуемые данные и смотрим, если загрузка сетей идет или нет
    private fun observeLoadingState() {
        lifecycleScope.launch {
            this@MainActivity.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoading.collect { loading ->
                    with(binding) {
                        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
                        recycler.visibility = if (loading) View.GONE else View.VISIBLE
                    }
                }
            }
        }
    }

    // подписываемся на публикуемые данные и проверяем откуда отображаются данные: из SharedPreferences или от сканирования
    private fun observeFromDataState() {
        lifecycleScope.launch {
            this@MainActivity.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dataFromScan.collect { isFromScan ->
                    val text = if (isFromScan) R.string.title_data_from_scan
                        else R.string.title_data_from_shared_pref
                    binding.dataFromText.text = getString(text)
                }
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1
    }
}
