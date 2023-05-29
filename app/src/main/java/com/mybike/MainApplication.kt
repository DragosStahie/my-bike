package com.mybike

import android.app.Application
import androidx.room.Room
import com.mybike.bikes.BikesViewModel
import com.mybike.data.database.AppDatabase
import com.mybike.data.datastore.DatastoreManager
import com.mybike.data.repository.ExampleRepository
import com.mybike.navigation.Navigator
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module


class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(appModule)
        }
    }
}

val appModule = module {
    single { Navigator() }
    single {
        Room.databaseBuilder(
            androidApplication(),
            AppDatabase::class.java,
            "AppDatabase"
        ).build()
    }
    single {
        val database = get<AppDatabase>()
        database.getExampleDao()
    }
    single { ExampleRepository(get(), get()) }
    single { DatastoreManager(androidContext()) }
    viewModel { BikesViewModel(get()) }
}