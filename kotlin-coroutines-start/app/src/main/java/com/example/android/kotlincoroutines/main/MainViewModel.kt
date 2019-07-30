/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.kotlincoroutines.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*

/**
 * MainViewModel designed to store and manage UI-related data in a lifecycle conscious way. This
 * allows data to survive configuration changes such as screen rotations. In addition, background
 * work such as fetching network results can continue through configuration changes and deliver
 * results after the new Fragment or Activity is available.
 */
class MainViewModel : ViewModel() {

    /**
     * In Kotlin, all coroutines run inside a CoroutineScope.
     * A scope controls the lifetime of coroutines through its job
     */
    private val viewModelJob = Job()

    /**
     * uiScope will start coroutines in Dispatchers.Main which is the main thread on Android. A coroutine
     * started on the main won't block the main thread while suspended. Since a ViewModel, coroutine
     * almost always updates the UI on the main thread, starting coroutines on the main thread is resonable
     * default. A coroutine can switch dispatchers any time after it's started
     */
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    /**
     * Request a snackbar to display a string.
     *
     * This variable is private because we don't want to expose MutableLiveData
     *
     * MutableLiveData allows anyone to set a value, and MainViewModel is the only
     * class that should be setting values.
     */
    private val _snackBar = MutableLiveData<String>()

    /**
     * Request a snackbar to display a string.
     */
    val snackbar: LiveData<String>
        get() = _snackBar

    private fun makeNetworkRequest() {
        // launch a coroutine in viewModelScope
        viewModelScope.launch(Dispatchers.IO) {
            //slowFetch() here
        }
    }

    /**
     * Wait one second then display a snackbar.
     */
    fun onMainViewClicked() {
        // Use viewModelScope to avoid boilerplate code
        // launch a coroutine in viewModelScope
        viewModelScope.launch {
            // suspend this coroutine for one
            delay(1_000)
            // resume in the main dispatcher
            // _snackbar.value can be called directly from main thread
            _snackBar.value = "Hello, from coroutines!"
        }

    }

    /**
     * Called immediately after the UI shows the snackbar.
     */
    fun onSnackbarShown() {
        _snackBar.value = null
    }

    override fun onCleared() {
        super.onCleared()
        // Cancel the scope when ViewModel is cleared
        //viewModelJob.cancel()
    }
}
