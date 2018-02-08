package co.rw.paafexample.mvp.ui.signin

class SignInPresenterImpl : SignInPresenter {

    private var signInView: SignInView? = null

    override fun onAttach(view: SignInView) {
        signInView = view
    }

    override fun onDetach() {
        signInView = null
    }

    override fun onSignInButtonClicked() {
        signInView?.onSignInSuccess()
    }
}
