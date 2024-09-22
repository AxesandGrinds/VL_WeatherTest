package com.vl.victorlekweuwaweatherapplication.di

import android.app.Application
import androidx.room.Room
import com.vl.victorlekweuwaweatherapplication.api.GeoCodingApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import com.vl.victorlekweuwaweatherapplication.api.WeatherApi
import com.vl.victorlekweuwaweatherapplication.data.WeatherDatabase
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    @Named("weather")
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(WeatherApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideNewsApi(@Named("weather") retrofit: Retrofit): WeatherApi =
        retrofit.create(WeatherApi::class.java)

    @Provides
    @Singleton
    @Named("location")
    fun provideRetrofit2(): Retrofit =
        Retrofit.Builder()
            .baseUrl(GeoCodingApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideNewsApi2(@Named("location") retrofit: Retrofit): GeoCodingApi =
        retrofit.create(GeoCodingApi::class.java)

    @Provides
    @Singleton
    fun provideDatabase(app: Application): WeatherDatabase =
        Room.databaseBuilder(app, WeatherDatabase::class.java, "weather_database")
            .fallbackToDestructiveMigration()
            .build()
}