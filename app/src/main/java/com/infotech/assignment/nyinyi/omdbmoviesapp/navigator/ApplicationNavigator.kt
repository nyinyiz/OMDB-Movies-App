package com.infotech.assignment.nyinyi.omdbmoviesapp.navigator

import android.os.Bundle

enum class Screens {
    MOVIES_LIST,
    MOVIES_DETAIL
}

interface ApplicationNavigator {
    fun navigateTo(screen: Screens, args: Bundle? = null)
}
