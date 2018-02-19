Presenter as a Function: Reactive MVP for Android Using Kotlin Coroutines
=========================================================================

Here at Rocket Wagon, the Android team is always looking for ways to make our code cleaner, simpler, and more efficient.  The MVP pattern and it's slew of boilerplate was grating on us, and we've been looking for solutions to get away from all the extra interfaces while improving testability. For reference, the standard MVP pattern on android is often implemented through a series of interfaces for both views and presenters.  We end up with a smattering of interfaces in order to nicely decouple and define things. A pretty common pattern is the following:

```kotlin
interface BaseView

interface BasePresenter<in T : BaseView> {
    // Shared presenter method definitions
}

interface SomeView: BaseView {
    // SomeView specific method definitions
}

interface SomeViewPresenter: BasePresenter<SomeView> {
    // SomeViewPresenter method definitions
}
```

All this is needed just to begin writing both `view` and `presenter` implementations, and there has to be a better way.

 The team recently stumbled across [this blogpost](https://rongi.github.io/kotlin-blog/rx-presenter.html) about implementing presenters as a function using Rx and it really clicked with us, and with the advent of Kotlin coroutines we wanted to take it a step further and implement this pattern without the use of Rx; instead using Kotlin's `Channel` in place of Rx's `Observable` streams.  We saw an opportunity to really improve our Android code and ended up discovering that this architecture provided the following benefits:

* Removal of the need for View and Presenter interfaces
* An even stronger decoupling of View and Presenter
* Clear and concise presenter logic
* Straight fowrard view implementations
* Simple and robust presenter testing

By moving to a reactive `PaaF`, we can toss all of the boilerplate intefaces out of the window. At it's core, a presenter takes in some events from the view, and responds to those events by performing actions, updating the view's model, or telling the view to do something. The presenter is _reacting_ to events from the view.  It only makes sense then that we could break down a presenter class into a function that takes some number of event streams and returns a view model containing some number of action streams.  So our presenter definition becomes something like this:

```kotlin
fun someViewPresenter(/* Event channel(s) */): SomeViewModel {
    // Code to transform events to actions
}

data class SomeViewModel(/* Action channel(s) the view reacts to */)
```

So one can see from the example here that our presenter takes some number of event channels that need to be converted into actions the view can listen for.  What are these `events` and `actions` we keep referring to?

* `Events` are view events that the view will push down it's channel(s) to our presenter to be converted into `Actions`.
* `Actions` are events pushed to the view via the channel(s) returned with the `ViewModel` that the view needs to listen to in order to know what to do and when.

Let's look at an example event definition:

```kotlin
// One type of event a view can emit

sealed class SomeViewClick {
    object PositiveButton : SomeViewClick()
}

sealed class SomeViewAction {
    object GoToNextActivity: SomeViewAction()
}
```

So you can see from above that we're using sealed classes to represent both the _type_ of event a given channel will emit as well as the _type_ of action a view can expect to recieve.  In this limited example, `SomeView` could emit a `PositiveButton` event, meaning the views positive button was clicked.  The view then would expect to receive a `GoToNextActivity` event from it's presenter channel.  Here's what a presenter would look like given the above examples so far:

```kotlin
fun someViewPresenter(clickEventChannel: ReceiveChannel<SomeViewClick>): SomeViewModel {
    val someViewActionChannel: Channel<SomeViewAction> = Channel()

    launch {
        for (someViewClickEvent in clickEventChannel) {
            when (someViewClickEvent) {
                SomeViewClick.PositiveButton -> {
                    someViewActionChannel.send(SomeViewAction.GoToNextActivity)
                    false
                }
            }
        }
    }

    return SomeViewModel(someViewActionChannel)
}
```

So we have a coroutine launched that is listening for events to arrive on our event channel `clickEventChannel`, and when we get an item of `SomeViewClick.PositiveButton` we then send an event telling our view to go to the next activity.  What's great about this implementation is that it's very clear.  It also protects us from new click events being added to `SomeViewClick`, because we will get compiler errors on the `when` block if it's not exhaustive.

One key thing to note is the `false` up above, which tells our `whileSelect` to stop looping.  If our presenter receives an event and expects to receive no more events, in this case because we've left the current view, we should return false to end the `whileSelect`.  However if the presenter receives an event and would expect more events, lets say a user entering values into a text field, then we would want to keep listening, and would return `true` from our `when` condition.

Here's the corresponding activity code:

```kotlin
class SomeViewActivity : AppCompatActivity() {

    private var someViewModel: SomeViewModel? = null
    private val clickEventChannel = Channel<SomeViewClick>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // set up view

        // synthetic property + Anko coroutines library onClick
        // The onClick in this case starts a coroutine so launch is not required here
        positive_button.onClick {
            clickEventChannel.send(SomeViewClick.PositiveButton)
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel = someViewModelPresenter(clickEventChannel)

        launch(UI) {
            viewModel?.someViewActionChannel?.let {
                for (someViewAction in ) {
                    when (someViewAction) {
                        SomeViewAction.GoToNextActivity -> {
                            // handle starting new activity
                            false
                        }
                    }
                }
            }
        }
    }


    override fun onPause() {
        super.onPause()
        someViewModel = null
    }
}
```

Again here we see how clean and concise the `PaaF` style is here.  When our activity resumes, we give our presenter function the view's click event channel, and the activity get's back a view model containing the action channel(s) it needs to listen to.  One thing to note here is the nullability of the view model.  Whenever our activity is paused we want to make sure that our view is not reacting to any possible delayed events in the background, so we clear our reference to the view model, and simply request a new view model whenever our activity resumes.

Finally lets take a look at the simplicity of testing that `PaaF` brings with it. Here's an example unit test for the code above:

```kotlin
class SomePresenterTests {
    private val clickEventChannel = Channel<SomeViewClick>()
    private val someViewModel = someViewPresenter(clickEventChannel)

    @Test
    fun testShouldReceiveGoToNextActivityActionOnPositiveClick() {
        runBlocking {
            clickEventChannel.send(SomeViewClick.PositiveButton)

            val action = someViewModel.someActionChannel.receive()

            assertEquals(SomeViewAction.GoToNextActivity, action)
        }
    }
}
```

That's all there is to it!  We merely need to validate that when we fire a particular event down the click event channel, we should receive the corresponding action event.

The Android team at Rocket Wagon believes it's pretty clear from the above example code that there is a large benefit in reducing complexity, improving clarity, and increasing the ease of testing when moving to a `PaaF` style architecture on Android.  When combined with the removal of the Rx requirement, we get the added benefit of less 3rd party dependencies.
