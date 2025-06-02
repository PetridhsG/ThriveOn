package gr.aueb.thriveon

import android.app.Application
import com.google.firebase.FirebaseApp
import gr.aueb.thriveon.di.initializeKoin

class ThriveOnApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this@ThriveOnApplication)
        initializeKoin(this@ThriveOnApplication)
    }
}
