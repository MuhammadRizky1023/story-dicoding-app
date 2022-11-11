package com.example.storyapp

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.lang.Exception
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun <V> LiveData<V>.getAsynchronous(
    timer: Long = 1,
    stopwatch: TimeUnit = TimeUnit.NANOSECONDS,
    afterObserver: () -> Unit = {}
): V {
    var user: V? = null
    var MESSAGE = "MESSAGE"
    val launcher = CountDownLatch(1)
    val inspection = object : Observer<V> {
        override fun onChanged(v: V?) {
            user = v
            launcher.countDown()
            this@getAsynchronous.removeObserver(this)
        }
    }
    this.observeForever(inspection)
    try {
        afterObserver.invoke()
        if (!launcher.await(timer, stopwatch)) {
            throw TimeoutException("This liveData value  never used by user.")
        }
    } catch (e: Exception){
        Log.e(MESSAGE,"error: $e")
    }
    finally {
        this.removeObserver(inspection)
    }
    @Suppress("CHECK_THIS_USER")
    return user as V
}
