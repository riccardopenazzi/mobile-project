package it.unibo.noteforall

import it.unibo.noteforall.utils.LocationService
import org.koin.dsl.module

val appModule = module {
    single { LocationService(get()) }
}