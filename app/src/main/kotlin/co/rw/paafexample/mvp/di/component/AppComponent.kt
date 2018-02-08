package co.rw.paafexample.mvp.di.component

import co.rw.paafexample.PaafApplication
import co.rw.paafexample.mvp.di.module.AppModule
import co.rw.paafexample.mvp.di.module.PresenterModule
import co.rw.paafexample.mvp.ui.signin.SignInActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, PresenterModule::class])
interface AppComponent {

    fun inject(target: PaafApplication)
    fun inject(target: SignInActivity)
}
