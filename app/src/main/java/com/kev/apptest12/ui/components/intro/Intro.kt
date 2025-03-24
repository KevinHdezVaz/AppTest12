package com.kev.apptest12.ui.components.intro

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.kev.apptest12.MainActivity
import com.kev.apptest12.R
import com.kev.apptest12.databinding.ActivityIntroBinding
import kotlin.math.nextUp

class IntroActivity : AppCompatActivity() {

    private lateinit var mViewPager: ViewPager2
    private var _binding: ActivityIntroBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityIntroBinding.inflate(layoutInflater)
        window.statusBarColor = android.graphics.Color.parseColor("#1976D2") // Darker blue
        _binding = ActivityIntroBinding.inflate(layoutInflater)

        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("myPreferencesFile", MODE_PRIVATE)

        if (!isFirstTime()) {
             startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        val myViewPager: ViewPager2 = binding.viewPagerMainActivity
        mViewPager = myViewPager

        myViewPager.adapter = MyViewPagerAdapter()
        val myMotionLayout: MotionLayout = binding.layoutMainMotionLayout
        val buttonNextPage: ImageButton = binding.buttonNextPage
        val progressIndicator: CircularProgressIndicator = binding.mainProgressInidicator

        buttonNextPage.setOnClickListener {
            if (myViewPager.currentItem != 2) {
                myViewPager.setCurrentItem(myViewPager.currentItem + 1, true)
            }
        }

        myViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                val currProgress = (position + positionOffset) / 2
                myMotionLayout.progress = currProgress
                progressIndicator.progress =
                    (((myViewPager.currentItem + 1) / 3f).nextUp() * 100).toInt()

                when (position) {
                    2 -> {
                        buttonNextPage.setImageResource(R.drawable.ic_done)
                        binding.buttonNextPage.setOnClickListener {
                             val editor = sharedPreferences.edit()
                            editor.putBoolean("RanBefore4", true)
                            editor.apply()
                            startActivity(Intent(this@IntroActivity, MainActivity::class.java))
                            finish()
                        }
                    }
                    else -> buttonNextPage.setImageResource(R.drawable.ic_next)
                }
            }
        })
    }

    private fun isFirstTime(): Boolean {
        val ranBefore = sharedPreferences.getBoolean("RanBefore4", false)
        return !ranBefore
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}