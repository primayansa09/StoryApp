package com.example.mystories

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun <T> LiveData<T>.getOrAwaitvalue(
        time: Long = 2,
        timeUnit: TimeUnit = TimeUnit.SECONDS,
        afterObserve: () -> Unit = {}
    ): T {
        var data: T? = null
        val latch = CountDownLatch(1)
        val observe = object : Observer<T> {
            override fun onChanged(o: T) {
                data = o
                latch.countDown()
                this@getOrAwaitvalue.removeObserver(this)
            }
        }
        this.observeForever(observe)

        try {
            afterObserve.invoke()

            if (!latch.await(time, timeUnit)){
                throw TimeoutException("LiveData value was never set.")
            }
        } finally {
            this.removeObserver(observe)
        }

        @Suppress("UNCHECKED_CAST")
        return data as T
    }