package com.infotech.assignment.nyinyi.omdbmoviesapp.navigator

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.infotech.assignment.nyinyi.omdbmoviesapp.R
import com.infotech.assignment.nyinyi.omdbmoviesapp.ui.movies.detail.MovieDetailFragment
import com.infotech.assignment.nyinyi.omdbmoviesapp.ui.movies.list.MovieListFragment
import javax.inject.Inject

class ApplicationNavigatorImpl @Inject constructor(private val activity: FragmentActivity) :
    ApplicationNavigator {
    override fun navigateTo(screen: Screens, args: Bundle?) {

        val fragment = when (screen) {
            Screens.MOVIES_LIST -> MovieListFragment()
            Screens.MOVIES_DETAIL -> MovieDetailFragment()
        }.apply {
            arguments = args
        }

        activity.supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, fragment)
            .addToBackStack(fragment::class.java.canonicalName)
            .commit()
    }
}
