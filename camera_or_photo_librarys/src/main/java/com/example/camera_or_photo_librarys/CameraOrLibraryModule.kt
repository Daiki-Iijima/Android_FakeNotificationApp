package com.example.camera_or_photo_librarys

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import kotlinx.coroutines.selects.select
import java.io.File

fun getLocalDirTempUri(context: Context): Uri {
    //  ディレクトリの指定(この時点では作成していない)
    val directory = File(context.filesDir, "icons")

    if (!directory.exists()) {
        directory.mkdirs()
    }

    //  指定したディレクトリに空のファイルを生成
    //  ディレクトリがない場合は自動生成
    val file = File.createTempFile(
        "icon_" + System.currentTimeMillis().toString(),    //  現在時刻
        ".jpg",
        directory
    )

    return FileProvider.getUriForFile(
        context,
        context.getString(R.string.file_provider),
        file
    )
}

fun deleteTempFile(context: Context, uri: Uri) {
    val rowDeleted = context.contentResolver.delete(uri, null, null)
}

@Composable
fun SelectOrTakePhotoBottomSheet(
    photoDir: MutableState<Uri?>,
    modifier: Modifier = Modifier
) {
    //  撮影した写真のURIを保持
    val takePhotoDir = remember {
        mutableStateOf<Uri?>(null)
    }

    val context = LocalContext.current

    val takePictureLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture(),
            onResult = { result ->
                //  写真が取られなかった場合
                if (!result) {
                    takePhotoDir.value?.let { uri ->
                        //  tempファイルを削除
                        deleteTempFile(context = context, uri = uri)

                        //  takePhotoDirを削除
                        takePhotoDir.value = null
                    }
                } else {
                    photoDir.value = takePhotoDir.value
                }
            }
        )

    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
            if (it) {
                takePhotoDir.value = getLocalDirTempUri(context = context)
                takePhotoDir.value?.let {
                    takePictureLauncher.launch(it)
                }
            }
        }


    val selectPhotoLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) {
            photoDir.value = it
        }

    Column {
        Button(onClick = {
            //  パーミッションチェック
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            } else {
                //  パーミッションがあれば実行
                takePhotoDir.value = getLocalDirTempUri(context = context)
                takePhotoDir.value?.let {
                    takePictureLauncher.launch(it)
                }
            }
        }) {
            Text(text = "写真を撮影")
        }
        Button(onClick = {
            //  画像のみを選択できるように
            val pickVisualMediaRequest = PickVisualMediaRequest
                .Builder()
                .setMediaType(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly)
                .build()

            selectPhotoLauncher.launch(pickVisualMediaRequest)
        }) {
            Text(text = "写真を選択")
        }
    }
}
