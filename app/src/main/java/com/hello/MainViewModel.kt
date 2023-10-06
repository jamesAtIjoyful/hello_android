package com.hello

import androidx.lifecycle.ViewModel
import javax.inject.Inject

class MainViewModel @Inject constructor() : ViewModel() {
    init {
        println("MainViewMode created")
    }

    fun sayHello(): String = "hello"

}