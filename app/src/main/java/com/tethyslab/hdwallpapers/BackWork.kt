package com.tethyslab.hdwallpapers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.WallpaperManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.*
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import com.tethyslab.hdwallpapers.retrofit.DataClass
import com.tethyslab.hdwallpapers.retrofit.DataService
import com.tethyslab.hdwallpapers.retrofit.RetrofitClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class BackWork(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams){
    private val MAX_WAIT_TIME_SECONDS = 15L
    lateinit var latch : CountDownLatch

    override fun doWork(): Result {
        latch = CountDownLatch(1)
        try {
            val sharedPreferences = this.applicationContext.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
            val localBroadcastManager = LocalBroadcastManager.getInstance(applicationContext)
            val successIntent = Intent("com.tethyslab.SUCCESS")
            val waitIntent = Intent("com.tethyslab.WAIT")
            val errorIntent = Intent("com.tethyslab.ERROR")
            val progressBarIntent = Intent("com.tethyslab.PROGRESSBAR")
            val notificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    "NOTIFICATION_CHANNEL",
                    "Daily Wallpaper",
                    NotificationManager.IMPORTANCE_LOW
                )
                notificationManager.createNotificationChannel(channel)
            }
            val notification =
                NotificationCompat.Builder(applicationContext, "NOTIFICATION_CHANNEL")
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setContentTitle("Wallpaper changed!").setContentText("Hope you like it...")

            val getSelectedCats = sharedPreferences.getStringSet("selectedCats", null)
            val randomInt = (1..(getSelectedCats?.size as Int)).random()
            val wallpaper = getSelectedCats?.toList()?.get(randomInt - 1)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            val wallpaperManager: WallpaperManager = WallpaperManager.getInstance(applicationContext)
            editor.putString("currentCat", wallpaper).apply()
            val target = object : Target {
                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                    localBroadcastManager.sendBroadcast(waitIntent)
                }

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                    localBroadcastManager.sendBroadcast(progressBarIntent)
                    localBroadcastManager.sendBroadcast(errorIntent)
                    latch.countDown()
                }

                override fun onBitmapLoaded(
                    bitmap: Bitmap?,
                    from: Picasso.LoadedFrom?
                ) {
                    try {
                        wallpaperManager.setBitmap(bitmap)
                        localBroadcastManager.sendBroadcast(progressBarIntent)
                        localBroadcastManager.sendBroadcast(successIntent)
                        notificationManager.notify(1234, notification.build())
                        latch.countDown()
                    } catch (e: Exception) {
                        localBroadcastManager.sendBroadcast(progressBarIntent)
                        localBroadcastManager.sendBroadcast(errorIntent)
                        latch.countDown()
                    }
                }
            }
            RetrofitClient.getClient().create(DataService::class.java)
                .getData().enqueue(object : retrofit2.Callback<DataClass> {
                    override fun onResponse(call: Call<DataClass>, response: Response<DataClass>) {
                        val json = Gson().toJson(response.body())
                        val jsonObject = JSONObject(json).get(wallpaper).toString()
                        editor.putString("currentCatLink", jsonObject).apply()
                        Picasso.get().load(jsonObject).into(target)
                    }

                    override fun onFailure(call: Call<DataClass>, t: Throwable) {
                        localBroadcastManager.sendBroadcast(progressBarIntent)
                        localBroadcastManager.sendBroadcast(errorIntent)
                        latch.countDown()
                    }
                })
            editor.putStringSet("changeCatSet", getSelectedCats).apply()
            editor.remove("empty").apply()
            val currentDate = Calendar.getInstance()
            val dueDate = Calendar.getInstance()
            dueDate.set(Calendar.HOUR_OF_DAY, 3)
            dueDate.set(Calendar.MINUTE, 0)
            dueDate.set(Calendar.SECOND, 0)
            if (dueDate.before(currentDate)) {
                dueDate.add(Calendar.HOUR_OF_DAY, 24)
            }
            val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis
            val request = OneTimeWorkRequestBuilder<BackWork>()
                .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                .build()
            WorkManager.getInstance(applicationContext).enqueueUniqueWork("uniqueWork", ExistingWorkPolicy.REPLACE,request)
            latch.await(MAX_WAIT_TIME_SECONDS, TimeUnit.SECONDS)
            return Result.success()
        }catch (e: Exception){
            return Result.failure()
        }
    }
}

