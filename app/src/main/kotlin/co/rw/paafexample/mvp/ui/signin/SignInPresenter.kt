package co.rw.paafexample.mvp.ui.signin

import co.rw.paafexample.mvp.base.BasePresenter

interface SignInPresenter : BasePresenter<SignInView> {
    fun onSignInButtonClicked()
}
