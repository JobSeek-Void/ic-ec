package team.jsv.icec.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.view.ViewTreeObserver
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import team.jsv.icec.base.BaseActivity
import team.jsv.icec.base.startActivityWithAnimation
import team.jsv.icec.ui.main.start.StartActivity
import team.jsv.icec.util.dp
import team.jsv.icec.util.getCustomTypefaceSpan
import team.jsv.icec.util.gone
import team.jsv.icec.util.hideSystemUI
import team.jsv.icec.util.setSystemUITransparent
import team.jsv.icec.util.showSystemUI
import team.jsv.icec.util.visible
import team.jsv.presentation.R
import team.jsv.presentation.databinding.ActivitySplashBinding

class SplashActivity : BaseActivity<ActivitySplashBinding>(R.layout.activity_splash) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSystemUITransparent()
        hideSystemUI()
        setSplashScreen()
    }

    private fun setSplashScreen() {
        binding.ivSplashLogo.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.ivSplashLogo.viewTreeObserver.removeOnGlobalLayoutListener(this)

                val endX = binding.ivSplashLogo.x + binding.ivSplashLogo.width + REC_INDICATOR_LEFT_MARGIN.dp
                val endY = binding.ivSplashLogo.y - binding.ivRecIndicator.height - REC_INDICATOR_BOTTOM_MARGIN.dp
                val xAnimator = ObjectAnimator.ofFloat(binding.ivRecIndicator, "x", endX)
                val yAnimator = ObjectAnimator.ofFloat(binding.ivRecIndicator, "y", endY)
                val animatorSet = AnimatorSet()

                animatorSet.apply {
                    playTogether(xAnimator, yAnimator)
                    startDelay = REC_INDICATOR_START_DELAY
                    duration = REC_INDICATOR_DURATION
                    doOnStart { onTransitionStart() }
                    doOnEnd { onTransitionEnd() }
                }.start()
            }
        })
    }

    private fun onTransitionStart() {
        val transitionDrawable = TransitionDrawable(arrayOf(
            ContextCompat.getDrawable(this@SplashActivity, R.color.black),
            ContextCompat.getDrawable(this@SplashActivity, R.drawable.bg_splash_image)
        ))

        binding.root.background = transitionDrawable

        lifecycleScope.launch {
            delay(REC_INDICATOR_START_DELAY)
            showSystemUI()
            binding.viewFrameLine.root.gone()
            binding.tvRecText.gone()
            transitionDrawable.startTransition(TRANSITION_BACKGROUND_DURATION)
        }
    }

    private fun onTransitionEnd() {
        lifecycleScope.launch {
            delay(EXPLANATION_TEXT_DELAY)
            setExplanationText()
            delay(CHANGE_ACTIVITY_DELAY)
            startActivityWithAnimation<StartActivity>(withFinish = true)
        }
    }

    private fun setExplanationText() {
        binding.tvExplanation.apply {
            text = getFormattedExplanationText()
            alpha = 0f
            visible()
        }

        ObjectAnimator.ofFloat(binding.tvExplanation, "alpha", 1f).apply {
            duration = EXPLANATION_TEXT_DURATION
            start()
        }
    }

    private fun getFormattedExplanationText(): SpannableString {
        return SpannableString(EXPLANATION_TEXT).apply {
            setSpan(getCustomTypefaceSpan(R.font.pretendard_medium), 0, 24, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(getCustomTypefaceSpan(R.font.pretendard_bold), 26, 37, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    companion object {
        const val REC_INDICATOR_LEFT_MARGIN = 2
        const val REC_INDICATOR_BOTTOM_MARGIN = 2
        const val REC_INDICATOR_START_DELAY = 2000L
        const val REC_INDICATOR_DURATION = 500L
        const val EXPLANATION_TEXT_DURATION = 500L
        const val EXPLANATION_TEXT_DELAY = 500L
        const val TRANSITION_BACKGROUND_DURATION = 1000
        const val CHANGE_ACTIVITY_DELAY = 1000L
        const val EXPLANATION_TEXT = "Easy and Detailed Edit\nby Auto Mosaic"
    }

}
