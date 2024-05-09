package com.example.camera_or_photo_library

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.example.camera_or_photo_library.ui.theme.FakeNotificationAppTheme

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
                    Greeting()
                }
            }
        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier) {

    val resultDir = remember {
        mutableStateOf<Uri?>(null)
    }

    val showBottomSheet = remember {
        mutableStateOf(false)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Button(onClick = { showBottomSheet.value = true }) {
            Text(text = "処理開始")
        }

        if (showBottomSheet.value) {
            SelectOrTakePhotoBottomSheet(resultDir)
        }

        if(resultDir.value != null) {
            AsyncImage(
                model = resultDir.value,
                contentDescription = null,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FakeNotificationAppTheme {
        Greeting()
    }
}