package com.sidgowda.pawcalc.doginput.ui

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sidgowda.pawcalc.ui.R
import com.sidgowda.pawcalc.ui.theme.LightDarkPreview
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun OpenMedia(
    modifier: Modifier = Modifier,
    onClose: () -> Unit,
    onSavePhoto: (Uri) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .animateContentSize()
    ) {
        val scope = rememberCoroutineScope()
        val context = LocalContext.current
        var chosenMediaImageUri by remember {
            mutableStateOf<Uri?>(null)
        }
        var images by remember {
            mutableStateOf<List<Image>?>(null)
        }
        val gridState = rememberLazyGridState()

        // get media
        LaunchedEffect(key1 = Unit) {
            scope.launch(Dispatchers.IO) {
                val mediaImages = retrieveImagesFromMedia(context)
                withContext(Dispatchers.Main) {
                    images = mediaImages
                }
            }
        }
        // don't show content till there are images
        if (images != null) {
            // todo create a shared Container Transition when clicking on image
            AnimatedContent(
                targetState = chosenMediaImageUri
            ) { targetState ->
                if (targetState != null) {
                    // show selected image
                    // animate image from small to big
                    ExpandedImageContainer(
                        modifier = modifier,
                        image = targetState,
                        onBack = {
                            chosenMediaImageUri = null
                        },
                        onSavePhoto = {
                            onSavePhoto(chosenMediaImageUri!!)
                        },
                        contentScale = ContentScale.FillHeight,
                        placeholder = R.drawable.ic_paw,
                        fallback = R.drawable.ic_paw
                    )
                } else {
                    MediaGallery(
                        modifier = Modifier.fillMaxSize(),
                        gridState = gridState,
                        images = images!!,
                        onChoosePhoto = { imageUri ->
                            chosenMediaImageUri = imageUri
                        },
                        onClose = onClose
                    )
                }
            }
        }
    }
}

@Composable
internal fun BoxScope.MediaGallery(
    modifier: Modifier = Modifier,
    gridState: LazyGridState,
    images: List<Image>,
    onChoosePhoto: (Uri) -> Unit,
    onClose: () -> Unit
) {
    LazyVerticalGrid(
        modifier = modifier.fillMaxSize(),
        state = gridState,
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(images) { image ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.5625f)
            ) {
                PhotoImage(
                    modifier = Modifier.clickable {
                        onChoosePhoto(image.uri)
                    },
                    image = image.uri,
                    scaleType = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.ic_paw),
                    fallback = painterResource(id = R.drawable.ic_paw)
                )
            }
        }
    }
    MediaButton(
        modifier = Modifier
            .align(Alignment.TopStart)
            .padding(16.dp),
        imageVector = Icons.Default.Close,
        onAction = onClose
    )
}

private fun retrieveImagesFromMedia(context: Context): List<Image> {
    val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.SIZE
    )
    val images = mutableListOf<Image>()
    context.contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection, null, null, MediaStore.Images.Media.DATE_MODIFIED + " desc"
    )?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val name = cursor.getString(nameColumn)
            val contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            images.add(Image(id, contentUri, name))
        }
    }
    return images
}

//---------Preview----------------------------------------------------------------------------------

@LightDarkPreview
@Composable
fun PreviewOpenMedia() {
    PawCalcTheme {
        OpenMedia(
            onClose = {  },
            onSavePhoto = {}
        )
    }
}

@LightDarkPreview
@Composable
fun PreviewMediaImage() {
    PawCalcTheme {
        PhotoImage(
            image = Uri.EMPTY,
            scaleType = ContentScale.Crop
        )
    }
}
