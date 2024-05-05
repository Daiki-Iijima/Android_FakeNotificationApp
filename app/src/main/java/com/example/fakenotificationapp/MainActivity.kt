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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.fakenotificationapp.ui.theme.FakeNotificationAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            FakeNotificationAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting(
                        createNotificationChannel = ::createNotificationChannel,
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
    createNotificationChannel: (String, String, String) -> Unit,
    notify: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {

    var notificationChannelNameStr by remember { mutableStateOf("") }
    var notificationChannelDescriptionStr by remember { mutableStateOf("") }
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
                Text(text = "通知チャンネル名")
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done  //  EnterキーをDone(完了)に設定
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                }
            ),
            value = notificationChannelNameStr,
            onValueChange = {
                notificationChannelNameStr = it
            },
        )

        OutlinedTextField(
            label = {
                Text(text = "通知チャンネル説明")
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done  //  EnterキーをDone(完了)に設定
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                }
            ),
            value = notificationChannelDescriptionStr,
            onValueChange = {
                notificationChannelDescriptionStr = it
            },
        )

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
            onClick = {
                createNotificationChannel(
                    notificationChannelNameStr,
                    notificationChannelNameStr,
                    notificationChannelDescriptionStr
                )
                notify(
                    notificationChannelNameStr,
                    notificationTitleStr,
                    notificationMessageStr
                )
            },
            modifier = Modifier.padding(top = 10.dp)
        ) {
            Text(text = "通知")
        }
    }


}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    FakeNotificationAppTheme {
        Greeting(
            createNotificationChannel = { _, _, _ ->
            },
            notify = { _, _, _ ->
            },
        )
    }
}