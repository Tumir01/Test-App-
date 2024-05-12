package com.example.testapp

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.MotionEvent
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.testapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var bindingClass: ActivityMainBinding
    private var animationUpAndDownFromFirstPosition: AnimatorSet? = null
    private var animationUpAndDown: AnimatorSet? = null
    private val sleepHandler = android.os.Handler(Looper.getMainLooper())


    @SuppressLint("ClickableViewAccessibility", "Recycle")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)

        hideSystemUI()
        bindingClass.tvGreeting.setTextColor(ContextCompat.getColor(this, R.color.greeting_text_color))
        bindingClass.tvGreeting.text = getString(R.string.Hello).uppercase()

        bindingClass.background.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    stopAnimation(animationUpAndDownFromFirstPosition)
                    stopAnimation(animationUpAndDown)
                    val x = event.x
                    val y = event.y
                    bindingClass.tvGreeting.x = x
                    bindingClass.tvGreeting.y = y

                    sleepHandler.postDelayed({
                        textAnimation()
                    }, 5000)
                }
            }
            true
        }
    }

    private fun textAnimation() {
        bindingClass.apply {
            val params = tvGreeting.layoutParams as ConstraintLayout.LayoutParams
            val verticalBias = params.verticalBias

            val currentTranslationY = tvGreeting.translationY
            val bottomOfScreen = background.height-tvGreeting.height/2-verticalBias*background.height.toFloat()
            val topOfScreen = -verticalBias*background.height+tvGreeting.height/2.toFloat()

            val animationDown = ObjectAnimator.ofFloat(tvGreeting, "translationY", currentTranslationY, bottomOfScreen)
            animationDown.duration = 3000

            val animationFromBottomToTop = ObjectAnimator.ofFloat(tvGreeting, "translationY", bottomOfScreen, topOfScreen)
            animationFromBottomToTop.duration = 3000

            val animationFromTopToBottom = ObjectAnimator.ofFloat(tvGreeting, "translationY", topOfScreen, bottomOfScreen)
            animationFromTopToBottom.duration = 3000


            animationUpAndDownFromFirstPosition = AnimatorSet()
            animationUpAndDownFromFirstPosition?.play(animationDown)?.before(animationFromBottomToTop)
            animationUpAndDownFromFirstPosition?.play(animationFromBottomToTop)?.before(animationFromTopToBottom)

            animationUpAndDown = AnimatorSet()
            animationUpAndDown?.play(animationFromBottomToTop)?.before(animationFromTopToBottom)

            val listenerForUpAndDownAnimation = object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    animationUpAndDown?.start()
                }
            }
            animationUpAndDownFromFirstPosition?.addListener(listenerForUpAndDownAnimation)
            animationUpAndDown?.addListener(listenerForUpAndDownAnimation)

            animationUpAndDownFromFirstPosition?.start()
        }
    }

    private fun stopAnimation (animatorSet: AnimatorSet?){
        if (animatorSet?.isStarted == true) {
            animatorSet.pause()
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
}
