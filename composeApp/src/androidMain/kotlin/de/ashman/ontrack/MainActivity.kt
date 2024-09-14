package de.ashman.ontrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import de.ashman.ontrack.di.initKoin
import de.ashman.ontrack.navigation.NavigationGraph
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.stopKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initKoin {
            androidContext(applicationContext)
        }

        setContent {
            NavigationGraph()
            //App()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopKoin()
    }
}
