package com.iheart.hackday

import android.animation.Animator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.iheart.hackday.base.R
import androidx.lifecycle.ViewModelProviders
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import com.cleveroad.audiovisualization.SpeechRecognizerDbmHandler
import com.squareup.picasso.Callback


class MainActivity : AppCompatActivity() {

    lateinit var viewModel: PodcastViewModel

    private val compositeDisposable = CompositeDisposable()

    private var speechRecognizerDbmHandler: SpeechRecognizerDbmHandler? = null

    private val scheduler = Executors.newScheduledThreadPool(1)

    private var scheduledFuture: ScheduledFuture<Any>? = null

    private var audioVisualization: AudioVisualization? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val lottieView = findViewById<LottieAnimationView>(R.id.lottieView)
        val introView = findViewById<FrameLayout>(R.id.introView)
        val episodeName = findViewById<TextView>(R.id.episodeName)
        val coverImageView = findViewById<ImageView>(R.id.cover)
        val playButton = findViewById<FloatingActionButton>(R.id.playButton)
        val seekBar = findViewById<SeekBar>(R.id.seekBar)
        val rewindButton = findViewById<FloatingActionButton>(R.id.rewind)
        val forwardButton = findViewById<FloatingActionButton>(R.id.forward)
        audioVisualization = findViewById<GLAudioVisualizationView>(R.id.visualizerView) as AudioVisualization

        speechRecognizerDbmHandler = DbmHandler.Factory.newSpeechRecognizerHandler(this).apply {
            audioVisualization!!.linkTo(this)

        }
        lottieView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                introView.visibility = View.GONE
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

        })

        viewModel = ViewModelProviders.of(this).get(PodcastViewModel::class.java)
        viewModel.setIntent(intent.data).subscribe({
            episodeName.text = it.title

            //artistiName.text = it.description
            Picasso.Builder(this).listener { picasso, uri, exception -> exception.printStackTrace() }.loggingEnabled(true).build()
                .load(Uri.parse(it.imageUrl))
                .into(coverImageView)
        }, { Log.d("Error", it.localizedMessage)})

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        val runnable = Runnable {
            seekBar.progress = viewModel.getProgress()
        }

        scheduler.scheduleAtFixedRate(runnable, 0, 1000, TimeUnit.MILLISECONDS)

        playButton.setOnClickListener {
            if (viewModel.isPlaying()) {
                viewModel.stop()
                playButton.setImageResource(R.drawable.ic_play)
                speechRecognizerDbmHandler!!.stopListening()
            } else {
                viewModel.play()
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
                }
                speechRecognizerDbmHandler!!.innerRecognitionListener(object: RecognitionListener{
                    override fun onReadyForSpeech(params: Bundle?) {

                    }

                    override fun onRmsChanged(rmsdB: Float) {
                    }

                    override fun onBufferReceived(buffer: ByteArray?) {
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {
                    }

                    override fun onBeginningOfSpeech() {
                    }

                    override fun onEndOfSpeech() {
                    }

                    override fun onError(error: Int) {
                    }

                    override fun onResults(results: Bundle?) {
                    }

                })
                speechRecognizerDbmHandler!!.startListening(intent)
                playButton.setImageResource(R.drawable.ic_pause)
            }
        }

        rewindButton.setOnClickListener {
            if (viewModel.isPlaying()) {
                viewModel.goBack15Secds()
            }
        }

        forwardButton.setOnClickListener {
            if (viewModel.isPlaying()) {
                viewModel.skipAhead30Secs()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        audioVisualization?.onResume()
    }

    override fun onPause() {
        audioVisualization?.onPause()
        super.onPause()
    }

    override fun onStop() {
        compositeDisposable.clear()
        scheduledFuture?.cancel(true)
        super.onStop()
    }

    override fun onDestroy() {
        audioVisualization?.release()
        super.onDestroy()
    }
//    fun animate() {
//        val set = ConstraintSet()
//        set.clone(root)
//        val constraint2 = ConstraintSet()
//        constraint2.clone(this, R.layout.activity_main)
//        constraint2.applyTo(root)
//        val transition = androidx.transition.AutoTransition()
//        TransitionManager.beginDelayedTransition(root, transition)
//        Log.d("lottieTime", System.currentTimeMillis().toString())
//
//    }
}
