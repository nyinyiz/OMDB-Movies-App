package com.infotech.assignment.nyinyi.omdbmoviesapp.di

import android.content.Context
import android.preference.PreferenceManager
import com.infotech.assignment.nyinyi.omdbmoviesapp.BuildConfig
import com.infotech.assignment.nyinyi.omdbmoviesapp.data.local.dao.MoviesDao
import com.infotech.assignment.nyinyi.omdbmoviesapp.data.local.dao.RemoteKeysDao
import com.infotech.assignment.nyinyi.omdbmoviesapp.data.local.db.AppDatabase
import com.infotech.assignment.nyinyi.omdbmoviesapp.data.remote.api.MoviesApi
import com.infotech.assignment.nyinyi.omdbmoviesapp.utils.Prefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private val loggingInterceptor =
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)
    private val okHttpClient: OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

    @Provides
    @Singleton
    fun providesDB(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.invoke(context)
    }

    @Singleton
    @Provides
    fun providesKeysDao(appDataBase: AppDatabase): RemoteKeysDao = appDataBase.remoteKeysDao

    @Singleton
    @Provides
    fun providesDao(appDataBase: AppDatabase): MoviesDao = appDataBase.moviesDao

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideMoviesApi(retrofit: Retrofit): MoviesApi =
        retrofit.create(MoviesApi::class.java)

    @Provides
    @Singleton
    fun providePrefs(@ApplicationContext context: Context) =
        Prefs(PreferenceManager.getDefaultSharedPreferences(context))
}
