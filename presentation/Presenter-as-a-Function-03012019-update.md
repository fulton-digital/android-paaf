Presenter as a Function: Structured Concurrency Edition
=======================================================

Since my [previous post][1], Kotlin has hit version 1.3 and coroutines have hit 1.0. These new releases have now brought [Structured Concurrency][2] into the fold.  I'm not going to cover in detail the changes to the language and frameworks here but I did want to show an updated example from my previous post. By utilizing [Structured Concurrency][2] we can improve on our previous example in a number of ways.

### Note

This post is not covering things such as debouncing clicks and other common UI paradigms.  You can check the [UI Coroutines Guide][3] for examples and ideas of how to properly handle your specific UI interaction needs.

## Coroutine Scope

With the introduction to coroutine scope, we no longer need manually manage our coroutine `Jobs`. Our Previous activity looked like this:

```kotlin
class SignInActivity : AppCompatActivity() {

    private val clickEventChannel = Channel<SignInClickEvent>()
    private var viewModel: SignInActionChannels? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        email_sign_in_button.onClick {
            clickEventChannel.send(SignInClickEvent.SignInButton)
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel = signInPresenter(clickEventChannel)

        launch(UI) {
            viewModel?.signInActionChannel?.let {
                for (signInAction in it) {
                    when (signInAction) {
                        SignInAction.SignInSuccessful -> {
                            toast("Sign In Success!").show()
                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel = null
    }
}
```

In hindsight, this had some nasty pitfalls. In the example above we were just throwing away our channels instead of properly cancelling the coroutines we were using.

Let's take a look at an updated version utilizing `CoroutineScope`. This will allow us to easily track and manage our presenter coroutines:

```kotlin
class SignInActivity() : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = SupervisorJob() + Dispatchers.Main

    private val clickEventChannel = Channel<SignInClickEvent>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        email_sign_in_button.onClick(coroutineContext) {
            clickEventChannel.send(SignInClickEvent.SignInButton)
        }
    }

    override fun onResume() {
        super.onResume()

        launch {
            signInPresenter(clickEventChannel).signInActionChannel.let {
                for (signInAction in it) {
                    when (signInAction) {
                        SignInAction.SignInSuccessful -> {
                            toast("Sign In Success!").show()
                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        coroutineContext.cancel()  // CoroutineScope.cancel
    }
}
```

There are a few items of note in this new example. First, our `SignInActivity` is now implementing `CoroutineScope`.  Because of this, we now need to override `coroutineContext` in order to provide a coroutineContext for our activity.

We can now take advantage of calls to coroutine builders such as `launch` which will automatically use the proper coroutine context. Further there is now a cascading effect on our presenter functions. Any function that utilizes these builders will all be tracked together via one coroutine context.

Let's take a look at our updated presenter function:

```kotlin
fun CoroutineScope.signInPresenter(clickEventChannel: ReceiveChannel<SignInClickEvent>): SignInChannels {
    val signInActionChannel = Channel<SignInAction>()

    // We don't want our presenter running on the main thread
    launch(Dispatchers.IO) {
        for (signInClickEvent in clickEventChannel) {
            when (signInClickEvent) {
                SignInClickEvent.SignInButton -> {
                    // Do some real sign in, for the example we will just delay
                    delay(timeMillis = 1000)
                    signInActionChannel.send(SignInAction.SignInSuccessful)
                }
            }
        }
    }

    return SignInChannels(signInActionChannel)
}
```

The major change here is that our presenter function is now an extension function on `CoroutineScope`. This allows us to call `signInPresenter` from classes implementing `CoroutineScope`.  This will launch our `signInPresenter` in the scope of it's caller, so we'll get automatic propagation of scope cancellation from our activity. Another way to implement this is to provide a `CoroutineScope` as a parameter to `signInPresenter`. After evaluating these two patters internally we chose the first option.

I hope this was a useful update to my previous post on Presenter as a Function, and sheds light on how the updates brought along with Kotlin 1.3 and Coroutines 1.0 have really improved our ability to manage our coroutine jobs.

You can see the updated code [here][4].


[1]: https://medium.com/@rocketwagon/presenter-as-a-function-reactive-mvp-for-android-using-kotlin-coroutines-442fc4c77119
[2]: https://medium.com/@elizarov/structured-concurrency-722d765aa952
[3]: https://github.com/Kotlin/kotlinx.coroutines/blob/master/ui/coroutines-guide-ui.md#basic-ui-coroutines
[4]: https://github.com/rocketwagon/android-paaf/tree/kotlin_1_3
