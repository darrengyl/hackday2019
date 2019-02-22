package com.iheart.hackday

import android.animation.Animator
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
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
import androidx.palette.graphics.Palette
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    lateinit var viewModel: PodcastViewModel

    private val compositeDisposable = CompositeDisposable()

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
        val background = findViewById<LinearLayout>(R.id.lowerBackground)
        background.visibility = View.GONE
        val artistiName = findViewById<TextView>(R.id.artistiName)
        val installButton = findViewById<Button>(R.id.installButton)

        installButton.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.clearchannel.iheartradio.controller&hl=en_US"))
            startActivity(browserIntent)
        }

        lottieView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                introView.visibility = View.GONE
                rewindButton.visibility = View.VISIBLE
                forwardButton.visibility = View.VISIBLE
                playButton.visibility = View.VISIBLE
                installButton.visibility = View.VISIBLE

            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

        })

        viewModel = ViewModelProviders.of(this).get(PodcastViewModel::class.java)


        compositeDisposable.add(viewModel.setIntent(intent.data).subscribe({
            episodeName.text = it.title
            artistiName.text = it.description
            Picasso.Builder(this).listener { picasso, uri, exception -> exception.printStackTrace() }.loggingEnabled(true).build()
                .load(Uri.parse(it.imageUrl))
                .into(coverImageView, object: Callback {
                    override fun onSuccess() {
                        val bitmap = (coverImageView.drawable as BitmapDrawable).bitmap
                        Palette.from(bitmap).generate {
                            it?.let {
                                val defaultColor = getColor(R.color.colorPrimary)
                                val vibrant = it.getVibrantColor(defaultColor)
                                val lightVibrant = it.getLightVibrantColor(defaultColor)
                                val muted = it.getMutedColor(defaultColor)
                                val darkMuted = it.getDarkMutedColor(defaultColor)
                                val mutedLight = it.getLightMutedColor(defaultColor)
                                artistiName.setTextColor(darkMuted)
                                episodeName.setTextColor(darkMuted)
                                background.setBackgroundColor(vibrant)
                                background.visibility = View.VISIBLE
                                seekBar.progressDrawable.setColorFilter(muted, PorterDuff.Mode.SRC_ATOP)
                                seekBar.thumb.setColorFilter(darkMuted, PorterDuff.Mode.SRC_ATOP)
                            }

                        }
                    }

                    override fun onError() {
                    }

                })
        }, { Log.d("Error", it.localizedMessage)}))

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
    }


    override fun onStop() {
        compositeDisposable.clear()
        scheduledFuture?.cancel(true)
        super.onStop()
    }
}
