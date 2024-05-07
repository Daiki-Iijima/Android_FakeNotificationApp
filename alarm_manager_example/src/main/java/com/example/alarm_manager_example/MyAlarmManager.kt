package com.example.alarm_manager_example

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.app.AlarmManagerCompat


class MyAlarmManager(
    private val context: Context,
) {

    private val alarmManager: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val hasPermission: Boolean
        get() = AlarmManagerCompat.canScheduleExactAlarms(alarmManager)

    fun setAlarm(triggerAtMillis: Long) {
        val pendingIntent = createAlarmPendingIntent(0)

        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager, AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent
        )

        Toast.makeText(
            context, "アラーム設定", Toast.LENGTH_SHORT
        ).show()
    }

    fun cancelAlarm() {
        val pendingIntent = createAlarmPendingIntent(0)

        alarmManager.cancel(pendingIntent)

        Toast.makeText(
            context, "アラーム解除", Toast.LENGTH_SHORT
        ).show()
    }

    private fun createAlarmPendingIntent(requestCode: Int): PendingIntent {
        //  アラーム発火時の処理をIntent化
        val intent = Intent(context, AlarmReceiver::class.java)

        //  アプリ起動時以外からも実行される可能性があるので、IntentをPendingIntentに変換
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return pendingIntent
    }

}

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("AlarmReceiver", "アラームが発火しました。")
        Toast.makeText(context, "アラームが発火しました", Toast.LENGTH_SHORT).show()
    }
}