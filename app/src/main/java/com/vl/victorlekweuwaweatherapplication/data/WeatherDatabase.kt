package com.vl.victorlekweuwaweatherapplication.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vl.victorlekweuwaweatherapplication.api.models.weatherModels.WeatherResponse
import com.vl.victorlekweuwaweatherapplication.util.typeConverters.CloudsTypeConverter
import com.vl.victorlekweuwaweatherapplication.util.typeConverters.CoordTypeConverter
import com.vl.victorlekweuwaweatherapplication.util.typeConverters.MainTypeConverter
import com.vl.victorlekweuwaweatherapplication.util.typeConverters.SysTypeConverter
import com.vl.victorlekweuwaweatherapplication.util.typeConverters.WeatherTypeConverter
import com.vl.victorlekweuwaweatherapplication.util.typeConverters.WindTypeConverter

@Database(entities = [WeatherResponse::class], version = 1)
@TypeConverters(
    CoordTypeConverter::class,
    WeatherTypeConverter::class,
    MainTypeConverter::class,
    WindTypeConverter::class,
    CloudsTypeConverter::class,
    SysTypeConverter::class
)
abstract class WeatherDatabase : RoomDatabase()  {

    abstract fun weatherDao() : WeatherDao

}