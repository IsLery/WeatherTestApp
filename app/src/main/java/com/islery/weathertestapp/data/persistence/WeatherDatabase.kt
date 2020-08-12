package com.islery.weathertestapp.data.persistence

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.islery.weathertestapp.WeatherApp
import com.islery.weathertestapp.data.model.CurrentInfo
import com.islery.weathertestapp.data.model.WeatherModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.Callable

const val WEATHER_TABLE = "weather_tb"
const val CURR_INFO_TABLE = "info_tb"
const val DATABASE_NAME = "weather_db"

@Database(
    entities = [WeatherModel::class, CurrentInfo::class],
    version = 1,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao

    abstract fun infoDao(): CurrInfoDao



    companion object {
        private var instance: WeatherDatabase? = null
        fun createDb(): WeatherDatabase = synchronized(this){
            if (instance == null) {
                instance = Room.databaseBuilder(
                    WeatherApp.applicationContext(),
                    WeatherDatabase::class.java,
                    DATABASE_NAME
                )
                //    .addCallback(DB_CALLBACK)
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance as WeatherDatabase
        }
    }

}

private val DB_CALLBACK = object: RoomDatabase.Callback(){
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        if (db is WeatherDatabase){
            Timber.d("creating db")
            db.infoDao().insertDefault(CurrentInfo(timestamp = 0,city = "N/A", countryCode = "N/A", latitude = null,longitude = null)).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
        }
    }
}


@Dao
interface WeatherDao {

    @Query("SELECT * FROM $WEATHER_TABLE")
    fun getAll(): Maybe<List<WeatherModel>>

    @Query("SELECT * FROM $WEATHER_TABLE LIMIT 1")
    fun getFirst(): Single<WeatherModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAll(list: List<WeatherModel>): Completable

    @Query("DELETE FROM $WEATHER_TABLE WHERE timestamp > 0")
    fun clearAll(): Completable

}

@Dao
interface CurrInfoDao {
    @Query("SELECT * FROM $CURR_INFO_TABLE LIMIT 1")
    fun getInfo(): Single<CurrentInfo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateInfo(info: CurrentInfo): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDefault(info: CurrentInfo): Completable
}

