package com.example.android.politicalpreparedness

import android.app.Application
import com.example.android.politicalpreparedness.database.ElectionDataSource
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.database.ElectionMainRepository
import com.example.android.politicalpreparedness.election.ElectionsViewModel
import com.example.android.politicalpreparedness.election.VoterInfoViewModel
import com.example.android.politicalpreparedness.network.BASE_URL
import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.network.CivicsHttpClient
import com.example.android.politicalpreparedness.network.jsonadapter.ElectionAdapter
import com.example.android.politicalpreparedness.representative.RepresentativeViewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import java.util.*

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        val myModule = module {
            //Declare a ViewModel - be later inject into Fragment with dedicated injector using by viewModel()
            viewModel {
                ElectionsViewModel(
                        get(),
                        get() as ElectionDataSource
                )
            }
            viewModel {
                RepresentativeViewModel(
                        get(),
                        get() as ElectionDataSource
                )
            }
            //Declare singleton definitions to be later injected using by inject()
            single {
                //This view model is declared singleton to be used across multiple fragments
                VoterInfoViewModel(
                        get(),
                        get() as ElectionDataSource
                )
            }
            single {
                CivicsHttpClient.getClient()
            }



            single<Converter.Factory>(named("moshi")) {
                val moshi = Moshi.Builder()
                        .add(ElectionAdapter())
                        .add(KotlinJsonAdapterFactory())
                        .add(Date::class.java, Rfc3339DateJsonAdapter())
                        .build()
                MoshiConverterFactory.create(moshi)
            }


            single {
                Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(get())
                        .addConverterFactory(get(named("moshi")))
                        .build()
            }

            single { get<Retrofit>().create(CivicsApiService::class.java) }
            single { ElectionMainRepository(get(), get()) as ElectionDataSource }
            single { ElectionDatabase.getInstance(this@MyApp).electionDao }
        }

        val networkModule = module {


        }
        startKoin {
            androidContext(this@MyApp)
            modules(listOf(myModule))
        }
    }
}