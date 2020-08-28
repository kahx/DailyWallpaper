package com.tethyslab.hdwallpapers

import android.app.WallpaperManager
import android.content.*
import android.graphics.Bitmap
import android.graphics.Color.argb
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.takusemba.spotlight.Spotlight
import com.takusemba.spotlight.Target
import com.takusemba.spotlight.effet.RippleEffect
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.TimeUnit
import androidx.core.view.*
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.takusemba.spotlight.shape.RoundedRectangle
import com.tethyslab.hdwallpapers.retrofit.DataClass
import com.tethyslab.hdwallpapers.retrofit.DataService
import com.tethyslab.hdwallpapers.retrofit.RetrofitClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.lang.Exception
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = sharedPreferences.edit()
        var hashSet = HashSet<String>(3)
        var chkBoxIds = mutableMapOf("c1" to R.id.c1, "c2" to R.id.c2, "c3" to R.id.c3, "c4" to R.id.c4, "c5" to R.id.c5, "c6" to R.id.c6, "c7" to R.id.c7, "c8" to R.id.c8)
        var counter = 0

        val currentDate = Calendar.getInstance()
        val dueDate = Calendar.getInstance()
        dueDate.set(Calendar.HOUR_OF_DAY, 17)
        dueDate.set(Calendar.MINUTE, 28)
        dueDate.set(Calendar.SECOND, 5)
        val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis
        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24)
        }

        val request = PeriodicWorkRequestBuilder<BackWork>(1, TimeUnit.MILLISECONDS)
            .build()

        if(sharedPreferences.contains("currentCat")){
            val currentCat = sharedPreferences.getString("currentCat", null)
            val currentCatLink = sharedPreferences.getString("currentCatLink", null)
            RetrofitClient.getClient().create(DataService::class.java)
                .getData().enqueue(object : retrofit2.Callback<DataClass> {
                    override fun onResponse(call: Call<DataClass>, response: Response<DataClass>) {
                        val json = Gson().toJson(response.body())
                        val jsonObject = JSONObject(json).get(currentCat).toString()
                        if (jsonObject != currentCatLink){
                            Snackbar.make(findViewById(R.id.mainLayout),"Seems like our background worker occured...", Snackbar.LENGTH_INDEFINITE)
                                .setTextColor(resources.getColor(R.color.colorAccent))
                                .setBackgroundTint(resources.getColor(R.color.colorPrimaryDark))
                                .setAction("RETRY"){
                                    progressBar.visibility = View.VISIBLE
                                    editor.putStringSet("selectedCats", hashSet).apply()
                                    WorkManager.getInstance(this@MainActivity).cancelAllWork()
                                    WorkManager.getInstance(this@MainActivity).enqueueUniquePeriodicWork("uniqueWork",ExistingPeriodicWorkPolicy.KEEP,request)
                                }
                                .setActionTextColor(resources.getColor(R.color.colorAccent))
                                .show()
                        }
                    }

                    override fun onFailure(call: Call<DataClass>, t: Throwable) {

                    }
                })
        }

        c1.findViewById<CheckBox>(R.id.c1).setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                chkBoxIds.remove("c1")
                hashSet.add("c1")
                counter += 1
                if (counter == 3) {
                    for (i in chkBoxIds) {
                        findViewById<CheckBox>(i.value).isEnabled = false
                    }
                }
            } else {
                counter -= 1
                chkBoxIds.put("c1", R.id.c1)
                hashSet.remove("c1")
                if (counter >= 2) {
                    for (i in chkBoxIds) {
                        findViewById<CheckBox>(i.value).isEnabled = true
                    }
                }
            }
        }
        c2.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                chkBoxIds.remove("c2")
                hashSet.add("c2")
                counter += 1
                if(counter == 3){
                    for(i in chkBoxIds){
                        findViewById<CheckBox>(i.value).isEnabled = false
                    }
                }
            }else{
                counter -= 1
                chkBoxIds.put("c2", R.id.c2)
                hashSet.remove("c2")
                if(counter >= 2) {
                    for (i in chkBoxIds) {
                        findViewById<CheckBox>(i.value).isEnabled = true
                    }
                }
            }
        }
        c3.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                chkBoxIds.remove("c3")
                hashSet.add("c3")
                counter += 1
                if(counter == 3){
                    for(i in chkBoxIds){
                        findViewById<CheckBox>(i.value).isEnabled = false
                    }
                }
            }else{
                counter -= 1
                chkBoxIds.put("c3", R.id.c3)
                hashSet.remove("c3")
                if(counter >= 2) {
                    for (i in chkBoxIds) {
                        findViewById<CheckBox>(i.value).isEnabled = true
                    }
                }
            }
        }
        c4.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                chkBoxIds.remove("c4")
                hashSet.add("c4")
                counter += 1
                if(counter == 3){
                    for(i in chkBoxIds){
                        findViewById<CheckBox>(i.value).isEnabled = false
                    }
                }
            }else{
                counter -= 1
                chkBoxIds.put("c4", R.id.c4)
                hashSet.remove("c4")
                if(counter >= 2) {
                    for (i in chkBoxIds) {
                        findViewById<CheckBox>(i.value).isEnabled = true
                    }
                }
            }
        }
        c5.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                chkBoxIds.remove("c5")
                hashSet.add("c5")
                counter += 1
                if(counter == 3){
                    for(i in chkBoxIds){
                        findViewById<CheckBox>(i.value).isEnabled = false
                    }
                }
            }else{
                counter -= 1
                chkBoxIds.put("c5", R.id.c5)
                hashSet.remove("c5")
                if(counter >= 2) {
                    for (i in chkBoxIds) {
                        findViewById<CheckBox>(i.value).isEnabled = true
                    }
                }
            }
        }
        c6.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                chkBoxIds.remove("c6")
                hashSet.add("c6")
                counter += 1
                if(counter == 3){
                    for(i in chkBoxIds){
                        findViewById<CheckBox>(i.value).isEnabled = false
                    }
                }
            }else{
                counter -= 1
                chkBoxIds.put("c6", R.id.c6)
                hashSet.remove("c6")
                if(counter >= 2) {
                    for (i in chkBoxIds) {
                        findViewById<CheckBox>(i.value).isEnabled = true
                    }
                }
            }
        }
        c7.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                chkBoxIds.remove("c7")
                hashSet.add("c7")
                counter += 1
                if(counter == 3){
                    for(i in chkBoxIds){
                        findViewById<CheckBox>(i.value).isEnabled = false
                    }
                }
            }else{
                counter -= 1
                chkBoxIds.put("c7", R.id.c7)
                hashSet.remove("c7")
                if(counter >= 2) {
                    for (i in chkBoxIds) {
                        findViewById<CheckBox>(i.value).isEnabled = true
                    }
                }
            }
        }
        c8.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                chkBoxIds.remove("c8")
                hashSet.add("c8")
                counter += 1
                if(counter == 3){
                    for(i in chkBoxIds){
                        findViewById<CheckBox>(i.value).isEnabled = false
                    }
                }
            }else{
                counter -= 1
                chkBoxIds.put("c8", R.id.c8)
                hashSet.remove("c8")
                if(counter >= 2) {
                    for (i in chkBoxIds) {
                        findViewById<CheckBox>(i.value).isEnabled = true
                    }
                }
            }
        }
        if (!sharedPreferences.contains("selectedCats")){
            window.decorView.rootView.doOnPreDraw {
                val viewAnchor = findViewById<View>(R.id.gridLayout)
                val target = Target.Builder()
                    .setAnchor(viewAnchor)
                    .setShape(RoundedRectangle(viewAnchor.height.toFloat(),viewAnchor.width.toFloat(),25f))
                    .setEffect(RippleEffect(10f, 250f, argb(30, 124, 255, 90)))
                    .setOverlay(layoutInflater.inflate(R.layout.main_target, null))
                    .build()
                val spotlight = Spotlight.Builder(this)
                    .setTargets(target)
                    .setBackgroundColor(R.color.background)
                    .setDuration(1000L)
                    .setAnimation(DecelerateInterpolator(2f))
                    .build()
                spotlight.start()
                mainLayout.setOnClickListener{spotlight.finish()}
                editor.putString("wallpaperTarget","").apply()
            }
        }else{
            sharedPreferences.getStringSet("selectedCats",null)?.forEach{
                val id = resources.getIdentifier(it, "id", applicationContext.packageName)
                findViewById<CheckBox>(id)?.isChecked = true
            }
            linearLayout.visibility = LinearLayout.VISIBLE
        }

        saveBtn.setOnClickListener {
            if(hashSet.isEmpty()){
                Snackbar.make(findViewById(R.id.mainLayout),"You must select at least one category.", Snackbar.LENGTH_SHORT)
                    .setTextColor(resources.getColor(R.color.colorAccent))
                    .setBackgroundTint(resources.getColor(R.color.colorPrimaryDark))
                    .show()
            }else{
                progressBar.visibility = View.VISIBLE
                editor.putStringSet("selectedCats", hashSet).apply()
                WorkManager.getInstance(this).cancelAllWork()
                WorkManager.getInstance(this).enqueueUniquePeriodicWork("uniqueWork",ExistingPeriodicWorkPolicy.KEEP,request)
            }
        }

        changeBtn.setOnClickListener{
            if (!sharedPreferences.contains("empty")){
                progressBar.visibility = View.VISIBLE
                changeWallpaper()
            }else{
                Snackbar.make(findViewById(R.id.mainLayout),"You see all categories!", Snackbar.LENGTH_SHORT)
                    .setTextColor(resources.getColor(R.color.colorAccent))
                    .setBackgroundTint(resources.getColor(R.color.colorPrimaryDark))
                    .show()
            }

        }
        seeBtn.setOnClickListener {
            val intent = Intent(this, WallpaperActivity::class.java)
            startActivity(intent)
        }

        val broadCastReceiver = object : BroadcastReceiver() {
            override fun onReceive(contxt: Context?, intent: Intent?) {
                when (intent?.action) {
                    "com.tethyslab.SUCCESS" -> Snackbar.make(findViewById(R.id.mainLayout),"Successful", Snackbar.LENGTH_SHORT)
                        .setTextColor(resources.getColor(R.color.colorAccent))
                        .setBackgroundTint(resources.getColor(R.color.colorPrimaryDark))
                        .show()
                    "com.tethyslab.WAIT" -> Snackbar.make(findViewById(R.id.mainLayout),"Please wait...", Snackbar.LENGTH_INDEFINITE)
                        .setTextColor(resources.getColor(R.color.colorAccent))
                        .setBackgroundTint(resources.getColor(R.color.colorPrimaryDark))
                        .show()
                    "com.tethyslab.ERROR" -> Snackbar.make(findViewById(R.id.mainLayout),"Error...", Snackbar.LENGTH_SHORT)
                        .setTextColor(resources.getColor(R.color.colorAccent))
                        .setBackgroundTint(resources.getColor(R.color.colorPrimaryDark))
                        .setAction("RETRY"){
                            progressBar.visibility = View.VISIBLE
                            editor.putStringSet("selectedCats", hashSet).apply()
                            WorkManager.getInstance(this@MainActivity).cancelAllWork()
                            WorkManager.getInstance(this@MainActivity).enqueueUniquePeriodicWork("uniqueWork",ExistingPeriodicWorkPolicy.KEEP,request)
                        }
                        .setActionTextColor(resources.getColor(R.color.colorAccent))
                        .show()
                }
            }
        }
        val progressBroadcastManager = object : BroadcastReceiver() {
            override fun onReceive(contxt: Context?, intent: Intent?) {
                when (intent?.action) {
                    "com.tethyslab.PROGRESSBAR" -> {
                        progressBar.visibility = View.GONE
                        if(linearLayout.visibility != LinearLayout.VISIBLE) {
                            linearLayout.visibility = LinearLayout.VISIBLE
                        }
                    }
                }
            }
        }
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(broadCastReceiver, IntentFilter("com.tethyslab.SUCCESS"))
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(broadCastReceiver, IntentFilter("com.tethyslab.WAIT"))
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(broadCastReceiver, IntentFilter("com.tethyslab.ERROR"))
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(progressBroadcastManager, IntentFilter("com.tethyslab.PROGRESSBAR"))

    }
    fun changeWallpaper(){
        val localBroadcastManager = LocalBroadcastManager.getInstance(this)
        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = sharedPreferences.edit()
        val getChangeSet = sharedPreferences.getStringSet("changeCatSet", null)
        val getCurrentCat = sharedPreferences.getString("currentCat", null)
        getChangeSet?.remove(getCurrentCat)
        val randomInt = (1..(getChangeSet?.size as Int)).random()
        val wallpaper = getChangeSet?.toList()?.get(randomInt - 1)
        editor.putString("currentCat",wallpaper).apply()
        val progressBarIntent = Intent("com.tethyslab.PROGRESSBAR")
        RetrofitClient.getClient().create(DataService::class.java)
            .getData().enqueue(object : retrofit2.Callback<DataClass> {
                override fun onResponse(call: Call<DataClass>, response: Response<DataClass>) {
                    val json = Gson().toJson(response.body())
                    val jsonObject = JSONObject(json).get(wallpaper).toString()
                    editor.putString("currentCatLink", jsonObject).apply()
                    val wallpaperManager : WallpaperManager = WallpaperManager.getInstance(applicationContext)
                    Picasso.get().load(jsonObject).into(object: com.squareup.picasso.Target {
                        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                            Snackbar.make(findViewById(R.id.mainLayout),"Please wait...", Snackbar.LENGTH_INDEFINITE)
                                .setTextColor(resources.getColor(R.color.colorAccent))
                                .setBackgroundTint(resources.getColor(R.color.colorPrimaryDark))
                                .show()
                        }

                        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                            localBroadcastManager.sendBroadcast(progressBarIntent)
                            Snackbar.make(findViewById(R.id.mainLayout),"Error..."+e?.message, Snackbar.LENGTH_SHORT)
                                .setTextColor(resources.getColor(R.color.colorAccent))
                                .setBackgroundTint(resources.getColor(R.color.colorPrimaryDark))
                                .show()
                        }

                        override fun onBitmapLoaded(
                            bitmap: Bitmap?,
                            from: Picasso.LoadedFrom?
                        ) {
                            try{
                                wallpaperManager.setBitmap(bitmap)
                                localBroadcastManager.sendBroadcast(progressBarIntent)
                                Snackbar.make(findViewById(R.id.mainLayout),"Successful", Snackbar.LENGTH_SHORT)
                                    .setTextColor(resources.getColor(R.color.colorAccent))
                                    .setBackgroundTint(resources.getColor(R.color.colorPrimaryDark))
                                    .show()
                            }catch (e: Exception){
                                localBroadcastManager.sendBroadcast(progressBarIntent)
                                Snackbar.make(findViewById(R.id.mainLayout),"Error..."+e.message, Snackbar.LENGTH_SHORT)
                                    .setTextColor(resources.getColor(R.color.colorAccent))
                                    .setBackgroundTint(resources.getColor(R.color.colorPrimaryDark))
                                    .show()
                            }

                        }
                    })
                }

                override fun onFailure(call: Call<DataClass>, t: Throwable) {
                    localBroadcastManager.sendBroadcast(progressBarIntent)
                    Snackbar.make(findViewById(R.id.mainLayout),"Error...", Snackbar.LENGTH_SHORT)
                        .setTextColor(resources.getColor(R.color.colorAccent))
                        .setBackgroundTint(resources.getColor(R.color.colorPrimaryDark))
                        .show()
                }
            })
        if (getChangeSet.size == 1){
            editor.putString("empty","").apply()
        }
    }
}
