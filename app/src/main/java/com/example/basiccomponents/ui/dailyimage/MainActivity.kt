package com.example.basiccomponents.ui.dailyimage

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import coil.load
import com.example.basiccomponents.R
import com.example.basiccomponents.network.repo.NasaRepository
import com.example.basiccomponents.ui.models.NasaDailyImage
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import java.util.*

class MainActivity : AppCompatActivity() {

    /**
     * Don't touch these 3 fields
     */
    private val nasaRepository by inject<NasaRepository>()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val exceptionHandler =
        CoroutineExceptionHandler { _, throwable -> handleError(throwable) }

    //TODO: initialize your views and other fields here
    private var nasaImage: ImageView? = null
    private var dateView: TextView? = null
    private var explainView: TextView? = null
    private var switchMaterial: SwitchMaterial? = null
    private var toolbar: androidx.appcompat.widget.Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        nasaImage = findViewById(R.id.image_view)
        dateView = findViewById(R.id.date_view)
        switchMaterial = findViewById(R.id.switchTheme)
        explainView = findViewById(R.id.explain_view)
        toolbar = findViewById(R.id.toolbar)
        fetchDailyImage(Date())
        switchMaterial?.setOnClickListener { changeTheme() }
    }


    private fun initViews(data: NasaDailyImage) {
        nasaImage?.load(data.imageUrl)
        dateView?.text = data.date
        explainView?.text = data.explanation
        toolbar?.title = data.title
        //TODO: apply data to your views
    }

    private fun handleError(t: Throwable) {
        Log.e(MainActivity::class.java.simpleName, "exception!", t)
        //TODO: handle request's errors here
    }

    /**
     * Don't edit this function. Use only for fetching data.
     */
    private fun fetchDailyImage(date: Date? = null) {
        coroutineScope.launch(exceptionHandler) {
            val result = nasaRepository.getDailyImage(date)
            Log.i(MainActivity::class.java.simpleName, "result: $result")

            withContext(Dispatchers.Main) {
                initViews(result)
            }
        }
    }

    /**
     * Changes the app theme to light or dark
     */
    private fun changeTheme() {
        var theme = AppCompatDelegate.getDefaultNightMode()
        theme = if (AppCompatDelegate.MODE_NIGHT_YES != theme)
            AppCompatDelegate.MODE_NIGHT_YES
        else
            AppCompatDelegate.MODE_NIGHT_NO

        AppCompatDelegate.setDefaultNightMode(theme)
    }

    override fun onStop() {
        super.onStop()
        coroutineScope.cancel()
    }
}