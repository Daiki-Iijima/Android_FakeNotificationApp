package com.example.alarm_manager_example

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.AlarmManagerCompat


class MyAlarmManager(private var context: Context) {

    private var alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun setAlarm(triggerAtMillis: Long) {
        //  アラーム発火時の処理をIntent化
        val intent = Intent(context, AlarmReceiver::class.java)

        //  アプリ起動時以外からも実行される可能性があるので、IntentをPendingIntentに変換
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        Toast.makeText(
            context,
            "アラーム設定",
            Toast.LENGTH_SHORT
        ).show()

        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent
        )
    }

}

class AlarmReceiver() : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("AlarmReceiver","アラームが発火しました。")
    }
}