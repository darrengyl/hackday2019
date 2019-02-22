package com.iheart.hackday

import android.animation.Animator
import android.os.Bundle
import android.os.Handler
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

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: PodcastViewModel

    private val compositeDisposable = CompositeDisposable()

    private val mHandler = Handler()

    private val scheduler = Executors.newScheduledThreadPool(1)

    private var scheduledFuture: ScheduledFuture<Any>? = null

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
        viewModel.setIntent(intent.data)

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
            } else {
                viewModel.play()
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

        viewModel.podcastInfoObservable().subscribe {
            episodeName.text = it.title

            //artistiName.text = it.description
            Picasso.Builder(this).loggingEnabled(true).build()
                .load(it.imageUrl)
                .into(coverImageView)
        }


    }

    override fun onStop() {
        compositeDisposable.clear()
        scheduledFuture?.cancel(true)
        super.onStop()
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
