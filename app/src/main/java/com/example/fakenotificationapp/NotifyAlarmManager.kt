package com.example.fakenotificationapp

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.AlarmManagerCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.content.ContextCompat.getString
import java.io.FileNotFoundException
import java.io.IOException

class NotifyAlarmManager(
    private val context: Context,
) {

    private val alarmManager: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val hasPermission: Boolean
        get() = AlarmManagerCompat.canScheduleExactAlarms(alarmManager)

    fun setAlarm(triggerAtMillis: Long, title: String, message: String, imageUri: Uri) {
        val pendingIntent = createAlarmPendingIntent(0, title, message, imageUri)

        if (System.currentTimeMillis() > triggerAtMillis) {
            Toast.makeText(context, "過去の時間に設定することはできません", Toast.LENGTH_SHORT)
                .show()
            return
        }

        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager, AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent
        )

        Toast.makeText(
            context, "アラーム設定", Toast.LENGTH_SHORT
        ).show()
    }

    fun cancelAlarm(title: String, message: String, imageUri: Uri) {
        val pendingIntent = createAlarmPendingIntent(0, title, message, imageUri)

        alarmManager.cancel(pendingIntent)

        Toast.makeText(
            context, "アラーム解除", Toast.LENGTH_SHORT
        ).show()
    }

    private fun createAlarmPendingIntent(
        requestCode: Int,
        title: String,
        message: String,
        uri: Uri,
    ): PendingIntent {
        //  アラーム発火時の処理をIntent化
        val intent = Intent(context, NotifyAlarmReceiver::class.java)
        intent.putExtra("title", title)
        intent.putExtra("message", message)
        intent.putExtra("imageUri", uri)

        //  アプリ起動時以外からも実行される可能性があるので、IntentをPendingIntentに変換
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

}

class NotifyAlarmReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onReceive(context: Context?, intent: Intent?) {

        if (context == null || intent == null) {
            Log.d("NotifyAlarmReceiver", "戻っている")
            return
        }

        val channelID = getString(context, R.string.channel_id)
        val title = intent.getStringExtra("title") ?: "Default Title"
        val message = intent.getStringExtra("message") ?: "Default Message"
        val imageUri = intent.getParcelableExtra("imageUri", Uri::class.java)

        //  TODO : デフォルト画像を用意したい
//        val bitmap = getBitmapFromUri(context, imageUri) ?: drawableToBitmap(
//            ContextCompat.getDrawable(
//                context,
//                R.drawable.ic_launcher_background
//            )!!
//        )

        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        //  通知の作成
        val builder = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)    //  通知がタップされると自動で通知を削除する

        //  通知
        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@with
            }
            notify(0, builder.build())
        }
    }

    fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        val width =
            if (!drawable.bounds.isEmpty) drawable.bounds.width() else drawable.intrinsicWidth
        val height =
            if (!drawable.bounds.isEmpty) drawable.bounds.height() else drawable.intrinsicHeight

        val bitmap = Bitmap.createBitmap(
            if (width > 0) width else 1,
            if (height > 0) height else 1,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

    private fun getBitmapFromUri(context: Context, uri: Uri?): Bitmap? {
        if (uri == null) {
            return null
        }

        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            null
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}
