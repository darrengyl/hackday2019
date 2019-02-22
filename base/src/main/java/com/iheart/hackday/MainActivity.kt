package com.iheart.hackday

import android.animation.Animator
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.iheart.hackday.base.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.TransitionManager
import androidx.lifecycle.ViewModelProviders
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import io.reactivex.disposables.CompositeDisposable

class MainActivity : AppCompatActivity() {

    lateinit var viewModel : PodcastViewModel

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val lottieView = findViewById<LottieAnimationView>(R.id.lottieView)
        val introView = findViewById<FrameLayout>(R.id.introView)
        val episodeName = findViewById<TextView>(R.id.episodeName)
        val coverImageView  = findViewById<ImageView>(R.id.cover)
        val playButton = findViewById<FloatingActionButton>(R.id.playButton)
        lottieView.addAnimatorListener(object: Animator.AnimatorListener{
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                introView.visibility = View.GONE
                //animate()
                Log.d("lottieTime", System.currentTimeMillis().toString())
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

        })

        //installButton.setOnClickListener {  }

        viewModel = ViewModelProviders.of(this).get(PodcastViewModel::class.java)
        viewModel.setIntent(intent.data)
        viewModel.isPlayingObservable().subscribe {
            playButton.setImageResource(if (it) { R.drawable.ic_pause } else { R.drawable.ic_play })
        }
        viewModel.play()
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
