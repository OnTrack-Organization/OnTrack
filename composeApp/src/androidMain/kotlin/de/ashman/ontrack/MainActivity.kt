package de.ashman.ontrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import de.ashman.ontrack.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.stopKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initKoin {
            androidContext(applicationContext)
        }

        enableEdgeToEdge()

        setContent {
            App()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopKoin()
    }
}
