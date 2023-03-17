/*
Copyright 2022 Google LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.example.commutual

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.util.Log
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Stable
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavHostController
import com.example.commutual.common.snackbar.SnackbarManager
import com.example.commutual.common.snackbar.SnackbarMessage.Companion.toMessage
import com.example.commutual.model.AlarmReceiver
import com.example.commutual.model.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import java.io.Serializable
import java.util.*

@Stable
class CommutualAppState(
    val scaffoldState: ScaffoldState,
    val navController: NavHostController,
    private val snackbarManager: SnackbarManager,
    private val resources: Resources,
    coroutineScope: CoroutineScope,
) {


    init {
        coroutineScope.launch {
            snackbarManager.snackbarMessages.filterNotNull().collect { snackbarMessage ->
                val text = snackbarMessage.toMessage(resources)
                scaffoldState.snackbarHostState.showSnackbar(text)
            }
        }
    }

    fun popUp() {
        navController.popBackStack()
    }

    fun navigate(route: String) {
        navController.navigate(route) { launchSingleTop = true }
    }

    fun navigateAndPopUp(route: String, popUp: String) {
        navController.navigate(route) {
            launchSingleTop = true
            popUpTo(popUp) { inclusive = true }
        }
    }


    fun clearAndNavigate(route: String) {
        navController.navigate(route) {
            launchSingleTop = true
            popUpTo(0) { inclusive = true }
        }
    }

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val name = CHANNEL_NAME
            val descriptionText = CHANNEL_DESCRIPTION
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showReminderNotification(
        context: Context,
        notificationId: Int,
        titleText: String,
        contentText: String,
        priority: Int = NotificationCompat.PRIORITY_DEFAULT
    ) {

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_home)
//            .setSmallIcon(R.drawable.ic_commutual)
            .setContentTitle(titleText)
            .setContentText(contentText)
            .setPriority(priority)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(CommutualActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
                return
            }
            notify(notificationId, builder.build())
        }
    }

    fun setAlarmManager(
        context: Context,
        titleText: String,
        contentText: String,
        calendar: Calendar,
        task: Task,
        chatId: String,
        alarmType: Int
    ) {
        Log.d("before am task", "task:$task")

        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java)

        intent.putExtra("title", titleText)
        intent.putExtra("content", contentText)
        intent.putExtra("content", contentText)
        intent.putExtra("alarmType", alarmType)
        intent.putExtra("chatId", chatId)
        intent.putExtra("task", task as Serializable)
        intent.setAction((System.currentTimeMillis().toString()));


        Log.d("com app state before am task", "task:$task")
        Log.d("com app state", "alarm type:$alarmType")

        val amRequestId = Random().nextInt(543254)
        Log.d("com app state", "am requestid:$amRequestId")

        val pendingIntent =
            PendingIntent.getBroadcast(context, amRequestId, intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT)

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )

        Log.d("alarm calendar", "calendar:${FormatterClass.formatDate(calendar.timeInMillis)}")


        Log.d("", "alarm set")

    }


    companion object {
        const val CHANNEL_ID = "notificationChannel"
        private const val CHANNEL_NAME = "Commutual Notifications"
        private const val CHANNEL_DESCRIPTION = "Receive notifications from Commutual"



    }
}
