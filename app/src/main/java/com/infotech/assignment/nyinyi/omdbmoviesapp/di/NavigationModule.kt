package com.infotech.assignment.nyinyi.omdbmoviesapp.di

import com.infotech.assignment.nyinyi.omdbmoviesapp.navigator.ApplicationNavigator
import com.infotech.assignment.nyinyi.omdbmoviesapp.navigator.ApplicationNavigatorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@InstallIn(ActivityComponent::class)
@Module
abstract class NavigationModule {
    @Binds
    abstract fun bindNavigator(impl: ApplicationNavigatorImpl): ApplicationNavigator
}
