package com.mybike

import android.app.Application
import androidx.room.Room
import com.mybike.bikes.viewmodel.AddBikesViewModel
import com.mybike.bikes.viewmodel.BikeDetailsViewModel
import com.mybike.bikes.viewmodel.BikesViewModel
import com.mybike.bikes.viewmodel.EditBikesViewModel
import com.mybike.data.database.AppDatabase
import com.mybike.data.datastore.DatastoreManager
import com.mybike.data.repository.BikesRepository
import com.mybike.data.repository.RidesRepository
import com.mybike.data.repository.SettingsRepository
import com.mybike.navigation.Navigator
import com.mybike.rides.viewmodel.AddRidesViewModel
import com.mybike.rides.viewmodel.EditRidesViewModel
import com.mybike.rides.viewmodel.RidesViewModel
import com.mybike.settings.viewmodel.SettingsViewModel
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
        database.getBikesDao()
    }
    single {
        val database = get<AppDatabase>()
        database.getRidesDao()
    }
    single { BikesRepository(get()) }
    single { RidesRepository(get()) }
    single { SettingsRepository(get()) }
    single { DatastoreManager(androidContext()) }
    viewModel { MainViewModel(get(),get(), get()) }
    viewModel { BikesViewModel(get(),get(), get()) }
    viewModel { AddBikesViewModel(get(), get()) }
    viewModel { EditBikesViewModel(get(), get()) }
    viewModel { BikeDetailsViewModel(get(), get(), get()) }
    viewModel { SettingsViewModel(get(), get()) }
    viewModel { AddRidesViewModel(get(), get(), get()) }
    viewModel { EditRidesViewModel(get(), get(), get()) }
    viewModel { RidesViewModel(get(), get(), get()) }
}