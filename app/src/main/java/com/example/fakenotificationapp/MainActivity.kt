package com.example.fakenotificationapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                        notify = ::notify
                    )
                }
            }
        }
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

    private fun notify(channelID: String, title: String, message: String) {
        //  通知タップ時のイベント作成
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        //  通知の作成
        val builder = NotificationCompat.Builder(this, channelID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)    //  通知がタップされると自動で通知を削除する


        //  通知
        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@with
            }
            notify(1, builder.build())
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Greeting(
    notify: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current

    val channelID = stringResource(id = R.string.channel_id)

    var notificationTitleStr by remember { mutableStateOf("") }
    var notificationMessageStr by remember { mutableStateOf("") }

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
                notify(
                    channelID,
                    notificationTitleStr,
                    notificationMessageStr
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
            notify = { _, _, _ ->
            },
        )
    }
}