package com.mybike.data.repository

import android.util.Log
import com.mybike.data.access.ExampleDao
import com.mybike.data.datastore.DatastoreManager

class ExampleRepository(
    private val exampleDao: ExampleDao,
    private val dataStoreManager: DatastoreManager
) {
    suspend fun testDataStore() {
        dataStoreManager.sampleUpdateMethod("testSet")
        dataStoreManager.sampleGetMethod().collect { value ->
            Log.d("TEST", "Sample get method result $value")
        }
    }
}