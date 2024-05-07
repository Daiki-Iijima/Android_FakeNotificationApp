package com.example.alarm_manager_example

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.alarm_manager_example.ui.theme.FakeNotificationAppTheme
import java.util.Calendar

class MainActivity : ComponentActivity() {
    private lateinit var alarmManager: MyAlarmManager
    private lateinit var exactAlarmPermissionLauncher: ActivityResultLauncher<Intent>

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exactAlarmPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (alarmManager.hasPermission) {
                Toast.makeText(
                    this,
                    "権限が与えられました！ありがとう！",
                    Toast.LENGTH_SHORT
                ).show()

                //  アラーム始動
                startAlarm()
            } else {
                Toast.makeText(
                    this,
                    "権限が与えられませんでした",
                    Toast.LENGTH_SHORT
                ).show()

                showPermissionSetting()
            }
        }

        alarmManager = MyAlarmManager(
            context = this,
        )


        if (!alarmManager.hasPermission) {
            showPermissionSetting()
        } else {
            startAlarm()
        }

        setContent {
            FakeNotificationAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }

    private fun startAlarm() {
        //  アラーム始動
        val calendar: Calendar = Calendar.getInstance().apply {
            add(Calendar.SECOND, 4)
        }
        alarmManager.setAlarm(calendar.timeInMillis)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun showPermissionSetting() {
        //  アラームスケジュール設定のパーミッション設定用Intentを生成
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
        //  結果を受け取る前提で起動
        exactAlarmPermissionLauncher.launch(intent)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!", modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FakeNotificationAppTheme {
        Greeting("Android")
    }
}