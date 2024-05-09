package com.example.testapp

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.testapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var bindingClass: ActivityMainBinding
    private var animatorSet: AnimatorSet? = null
    private val handler = android.os.Handler()

    @SuppressLint("ClickableViewAccessibility", "Recycle")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)

        bindingClass.tvGreeting.setTextColor(resources.getColor(R.color.greeting_text_color))

        bindingClass.background.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val x = event.x
                    val y = event.y
                    bindingClass.tvGreeting.x = x
                    bindingClass.tvGreeting.y = y
                    bindingClass.tvGreeting.visibility = View.VISIBLE

                    handler.postDelayed({
                        startTextAnimation()
                    }, 5000)
                }
            }
            true
        }
    }

    private fun startTextAnimation() {
        animatorSet?.cancel()
        textAnimation()
    }

    private fun textAnimation() {
        val screenHeight = bindingClass.background.height.toFloat()
        val currentTranslationY = bindingClass.tvGreeting.translationY

        val animationDown = ObjectAnimator.ofFloat(bindingClass.tvGreeting, "translationY", currentTranslationY, screenHeight)
        animationDown.duration = 3000

        val animationFromBottomToTop = ObjectAnimator.ofFloat(bindingClass.tvGreeting, "translationY", screenHeight, 0f)
        animationFromBottomToTop.duration = 3000

        val animationFromTopToBottom = ObjectAnimator.ofFloat(bindingClass.tvGreeting, "translationY", 0f, screenHeight)
        animationFromTopToBottom.duration = 3000

        animatorSet = AnimatorSet()
        animatorSet?.play(animationDown)?.before(animationFromBottomToTop)
        animatorSet?.play(animationFromBottomToTop)?.before(animationFromTopToBottom)

        animatorSet?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                textAnimation()
            }
        })

        animatorSet?.start()
    }
}
