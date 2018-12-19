package com.example.mac.ezbooks.di

/*
 * Copyright (C) 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



import android.content.Context
import android.support.v4.content.AsyncTaskLoader

/**
 * AsyncTaskLoader implementation that opens a network connection and
 * query's the Book Service API.
 */
class BookLoader// Constructor providing a reference to the search term.
(context: Context, // Variable that stores the search string.
 private val mQueryString: String) : AsyncTaskLoader<String>(context) {

    /**
     * This method is invoked by the LoaderManager whenever the loader is started
     */
    override fun onStartLoading() {
        forceLoad() // Starts the loadInBackground method
    }

    /**
     * Connects to the network and makes the Books API request on a background thread.
     *
     * @return Returns the raw JSON response from the API call.
     */
    override fun loadInBackground(): String? {
        return NetworkUtils.getBookInfo(mQueryString)
    }
}