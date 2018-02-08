package co.rw.paafexample

import android.app.Application
import co.rw.paafexample.mvp.di.component.AppComponent
import co.rw.paafexample.mvp.di.component.DaggerAppComponent

class PaafApplication : Application() {

    lateinit var mvpAppComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        mvpAppComponent = initDagger(this)
    }

    private fun initDagger(app: PaafApplication): AppComponent {
        return DaggerAppComponent.builder()
            .build()
    }
}
