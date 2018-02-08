package co.rw.paafexample.mvp

import co.rw.paafexample.mvp.ui.signin.SignInActivity
import co.rw.paafexample.mvp.ui.signin.SignInPresenterImpl
import com.nhaarman.mockito_kotlin.verify
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class MvpExampleTest {

    val signInPresenter = SignInPresenterImpl()

    @Mock
    lateinit var signInActivity: SignInActivity

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun testPresenterCallsOnSignInSuccessWhenSignInClickedIsCalled() {

        Mockito.`when`(signInActivity.onSignInSuccess()).then { }


        signInPresenter.onAttach(signInActivity)
        signInPresenter.onSignInButtonClicked()

        verify(signInActivity).onSignInSuccess()
    }
}
