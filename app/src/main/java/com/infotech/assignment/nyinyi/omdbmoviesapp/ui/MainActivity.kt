package com.infotech.assignment.nyinyi.omdbmoviesapp.ui

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.infotech.assignment.nyinyi.omdbmoviesapp.R
import com.infotech.assignment.nyinyi.omdbmoviesapp.navigator.ApplicationNavigator
import com.infotech.assignment.nyinyi.omdbmoviesapp.navigator.Screens
import com.infotech.assignment.nyinyi.omdbmoviesapp.utils.makeStatusBarTransparent
import com.infotech.assignment.nyinyi.omdbmoviesapp.utils.setMarginTop
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var navigator: ApplicationNavigator

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        makeStatusBarTransparent()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_container)) { _, insets ->
            findViewById<FrameLayout>(R.id.main_container).setMarginTop(insets.systemWindowInsetTop)
            insets.consumeSystemWindowInsets()
        }

        if (savedInstanceState == null) {
            navigator.navigateTo(Screens.MOVIES_LIST)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        if (supportFragmentManager.backStackEntryCount == 0) {
            finish()
        }
    }
}
