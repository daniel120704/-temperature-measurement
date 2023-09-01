package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

class MainActivity : AppCompatActivity() {
    private val temperatureFile = arrayOf("/sys/class/thermal/thermal_zone0/temp",
    "/sys/class/thermal/thermal_zone19/temp","/sys/class/thermal/thermal_zone75/temp","/sys/class/thermal/thermal_zone78/temp")
    private val updateIntervalMillis = 1000L // Aktualisierungsintervall in Millisekunden
    private lateinit var temperatureTextView: TextView
    private val sensorNames = arrayOf("Allgemein", "CPU", "Kamera", "Akku")

    private val handler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            updateTemperature()
            handler.postDelayed(this, updateIntervalMillis)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        temperatureTextView = findViewById(R.id.temperatureTextView)
        temperatureTextView = findViewById(R.id.temperatureTextView1)
        temperatureTextView = findViewById(R.id.temperatureTextView2)
        temperatureTextView = findViewById(R.id.temperatureTextView3)
        handler.post(updateRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop the temperature update when the activity is destroyed
        handler.removeCallbacks(updateRunnable)
    }

    private fun updateTemperature() {
        val temperatureString = buildTemperatureString()
        temperatureTextView.text = temperatureString

    }

    private fun buildTemperatureString(): String {
        val stringBuilder = StringBuilder()

        for ((index, temperatureFile) in temperatureFile.withIndex()) {
            val temperature = readTemperature(temperatureFile)
            val formattedTemperature = getString(R.string.temperature_format, temperature)
            val sensorName = sensorNames[index]
            stringBuilder.append(" $sensorName: $formattedTemperature\n")
        }

        return stringBuilder.toString()
    }

    private fun readTemperature(filePath: String): Double {
        var temperature = 0.0

        try {
            val temperatureFile = File(filePath)
            if (temperatureFile.exists()) {
                val reader = BufferedReader(FileReader(temperatureFile))
                val line = reader.readLine()
                reader.close()

                val temp = line?.toDoubleOrNull()
                if (temp != null) {
                    // The temperature is in millidegree Celsius, so divide by 1000
                    temperature = temp / 1000.0
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return temperature
    }
}

