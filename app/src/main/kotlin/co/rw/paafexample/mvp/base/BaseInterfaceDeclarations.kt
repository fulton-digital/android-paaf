package co.rw.paafexample.mvp.base

interface BaseView

interface BasePresenter<in T : BaseView> {
    fun onAttach(view: T)
    fun onDetach()
}

