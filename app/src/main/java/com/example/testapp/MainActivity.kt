package com.example.testapp

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.testapp.Constance.ANIMATION_DURATION
import com.example.testapp.Constance.SLEEP_HOLDER_DURATION
import com.example.testapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var bindingClass: ActivityMainBinding
    private var animationUpAndDownFromFirstPosition: AnimatorSet? = null
    private var animationUpAndDown: AnimatorSet? = null
    private val sleepHandler = android.os.Handler(Looper.getMainLooper())
    private var isSleepHandlerRunning = false
    private var animationCancelled = false



    @SuppressLint("ClickableViewAccessibility", "Recycle")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)

        hideSystemUI()
        bindingClass.tvGreeting.text = getString(R.string.Hello).uppercase()


        bindingClass.background.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {

                    bindingClass.tvGreeting.setTextColor(ContextCompat.getColor(this, R.color.greeting_text_color))
                    stopAnimation(animationUpAndDownFromFirstPosition)
                    stopAnimation(animationUpAndDown)

                    if (!isSleepHandlerRunning) {
                        isSleepHandlerRunning = true
                        sleepHandler.postDelayed({
                            textAnimation()
                            isSleepHandlerRunning = false
                        }, SLEEP_HOLDER_DURATION)
                    } else {
                        sleepHandler.removeCallbacksAndMessages(null)
                        sleepHandler.postDelayed({
                            textAnimation()
                        }, SLEEP_HOLDER_DURATION)
                    }

                    val x = event.x
                    val y = event.y

                    bindingClass.apply {
                        val text = tvGreeting.text.toString()
                        val textWidth = tvGreeting.paint.measureText(text)

                        if (x > background.width - textWidth) {
                            tvGreeting.x = (background.width - textWidth - (tvGreeting.width - textWidth) / 2)
                            tvGreeting.y = y
                        } else {
                            tvGreeting.x = x - (tvGreeting.width - textWidth) / 2
                            tvGreeting.y = y - tvGreeting.textSize
                        }
                    }
                }

            }
            true
        }
    }

    fun stopAnimationOnTextClick(view: View){
        stopAnimation(animationUpAndDown)
        stopAnimation(animationUpAndDownFromFirstPosition)
    }

    private fun textAnimation() {
        bindingClass.apply {
            val params = tvGreeting.layoutParams as ConstraintLayout.LayoutParams
            val verticalBias = params.verticalBias

            val currentTranslationY = tvGreeting.translationY
            val bottomOfScreen = background.height-tvGreeting.height/2-verticalBias*background.height+tvGreeting.textSize
            val topOfScreen = -verticalBias*background.height+tvGreeting.height/2-tvGreeting.textSize

            val animationDown = ObjectAnimator.ofFloat(tvGreeting, "translationY", currentTranslationY, bottomOfScreen)
            animationDown.duration = ANIMATION_DURATION

            val animationFromBottomToTop = ObjectAnimator.ofFloat(tvGreeting, "translationY", bottomOfScreen, topOfScreen)
            animationFromBottomToTop.duration = ANIMATION_DURATION

            val animationFromTopToBottom = ObjectAnimator.ofFloat(tvGreeting, "translationY", topOfScreen, bottomOfScreen)
            animationFromTopToBottom.duration = ANIMATION_DURATION


            animationUpAndDownFromFirstPosition = AnimatorSet()
            animationUpAndDownFromFirstPosition?.play(animationDown)?.before(animationFromBottomToTop)
            animationUpAndDownFromFirstPosition?.play(animationFromBottomToTop)?.before(animationFromTopToBottom)

            animationUpAndDown = AnimatorSet()
            animationUpAndDown?.play(animationFromBottomToTop)?.before(animationFromTopToBottom)

            val listenerForUpAndDownAnimationCancel = object : AnimatorListenerAdapter() {
                override fun onAnimationCancel(animation: Animator) {
                    animationCancelled = true

                }
            }

            val listenerForUpAndDownAnimation = object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    if (!animationCancelled) {
                        animationUpAndDown?.start()
                    }
                    animationCancelled=false
                }
            }

            animationUpAndDownFromFirstPosition?.addListener(listenerForUpAndDownAnimation)
            animationUpAndDown?.addListener(listenerForUpAndDownAnimation)
            animationUpAndDownFromFirstPosition?.addListener(listenerForUpAndDownAnimationCancel)
            animationUpAndDown?.addListener(listenerForUpAndDownAnimationCancel)

            animationUpAndDownFromFirstPosition?.start()
        }
    }

    private fun stopAnimation (animatorSet: AnimatorSet?){
        if (animatorSet?.isStarted == true) {
            animatorSet.cancel()
        }
    }

    private fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let { controller ->
                controller.hide(WindowInsets.Type.systemBars())
                controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        stopAnimation(animationUpAndDown)
        stopAnimation(animationUpAndDownFromFirstPosition)
        sleepHandler.removeCallbacksAndMessages(null)
        bindingClass.tvGreeting.x = (bindingClass.background.width/2 - bindingClass.tvGreeting.width/2).toFloat()
        bindingClass.tvGreeting.y = (bindingClass.background.height/2 - bindingClass.tvGreeting.height/2).toFloat()
    }
}

object Constance {
    const val ANIMATION_DURATION: Long = 3000
    const val SLEEP_HOLDER_DURATION: Long = 5000
}