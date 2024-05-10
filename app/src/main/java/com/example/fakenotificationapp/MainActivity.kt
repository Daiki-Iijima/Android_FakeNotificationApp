package com.example.fakenotificationapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import coil.compose.AsyncImage
import com.example.fakenotificationapp.ui.theme.FakeNotificationAppTheme
import java.util.Calendar

class MainActivity : ComponentActivity() {
    private lateinit var notifyAlarmManager: NotifyAlarmManager
    private lateinit var exactAlarmPermissionLauncher: ActivityResultLauncher<Intent>

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exactAlarmPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            //  ActivityResultは使えないので、自前のチェックを行う
            if (notifyAlarmManager.hasPermission) {
                Toast.makeText(
                    this, "権限が与えられました！ありがとう！", Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this, "権限が与えられませんでした", Toast.LENGTH_SHORT
                ).show()

                showPermissionSetting()
            }
        }

        notifyAlarmManager = NotifyAlarmManager(
            context = this,
        )

        if (!notifyAlarmManager.hasPermission) {
            showPermissionSetting()
        }

        //  チャンネルは事前に作っておく
        //  TODO : 自分で作れる設定を追加したい
        createNotificationChannel(
            channelID = getString(R.string.channel_id),
            name = getString(R.string.channel_name),
            descriptionStr = getString(R.string.channel_description)
        )

        setContent {
            FakeNotificationAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting(
                        notifyAlarmManager
                    )
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun showPermissionSetting() {
        //  アラームスケジュール設定のパーミッション設定用Intentを生成
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
        //  結果を受け取る前提で起動
        exactAlarmPermissionLauncher.launch(intent)
    }

    private fun createNotificationChannel(
        channelID: String,
        name: String,
        descriptionStr: String
    ) {
        val importance = NotificationManager.IMPORTANCE_HIGH    //  チャンネルに来る通知の重要度
        val channel = NotificationChannel(channelID, name, importance).apply {
            description = descriptionStr
        }
        // Register the channel with the system.
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Greeting(
    notifyAlarmManager: NotifyAlarmManager?,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current

    val channelID = stringResource(id = R.string.channel_id)

    var notificationTitleStr by remember { mutableStateOf("") }
    var notificationMessageStr by remember { mutableStateOf("") }
    var notifyImageUri by remember { mutableStateOf<Uri?>(null) }

    //  TODO : とりあえずURIを指定しておくが、画像選択機能を実装したら消す
    notifyImageUri = Uri.parse("content://media/external/images/media/12345")

    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 10.dp)
    ) {
        OutlinedTextField(
            label = {
                Text(text = "通知タイトル")
            },

            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done  //  EnterキーをDone(完了)に設定
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                }
            ),
            value = notificationTitleStr,
            onValueChange = {
                notificationTitleStr = it
            },
        )

        OutlinedTextField(
            label = {
                Text(text = "通知メッセージ")
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done  //  EnterキーをDone(完了)に設定
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                }
            ),
            value = notificationMessageStr,
            onValueChange = {
                notificationMessageStr = it
            }
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            onClick = { /*TODO*/ }
        ) {
            Text(text = "日付設定")
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            onClick = { /*TODO*/ }
        ) {
            Text(text = "時間設定")
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            AsyncImage(
                model = "",
                contentDescription = null,
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp)
            ) {
                Text(text = "アイコン設定")
            }
        }

        Button(
            onClick = {
                //  アラーム始動
                val calendar: Calendar = Calendar.getInstance().apply {
                    add(Calendar.SECOND, 1)
                }
                notifyAlarmManager?.setAlarm(
                    calendar.timeInMillis,
                    notificationTitleStr,
                    notificationMessageStr,
                    notifyImageUri!!
                )
            },
            modifier = Modifier.padding(top = 10.dp)
        ) {
            Text(text = "開始")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    FakeNotificationAppTheme {
        Greeting(
            null
        )
    }
}