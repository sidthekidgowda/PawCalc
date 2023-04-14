package com.sidgowda.pawcalc.doginput.ui

import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.sidgowda.pawcalc.camera.R
import com.sidgowda.pawcalc.ui.component.PawCalcButton
import com.sidgowda.pawcalc.ui.theme.LightDarkPreview
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme

@Composable
internal fun BoxScope.ExpandedImageContainer(
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.FillHeight,
    image: Uri,
    @DrawableRes
    placeholder: Int? = null,
    @DrawableRes
    fallback: Int? = null,
    onBack: () -> Unit,
    onSavePhoto: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PhotoImage(
            modifier = Modifier.weight(1.0f),
            image = image,
            scaleType = contentScale,
            placeholder = if (placeholder != null) painterResource(id = placeholder) else null,
            fallback = if (fallback != null) painterResource(id = fallback) else null
        )
        Spacer(modifier = Modifier
            .height(6.dp)
            .fillMaxWidth())
        PawCalcButton(
            modifier = Modifier
                .fillMaxWidth(.5f),
            text = stringResource(id = R.string.media_image_save_button),
            onClick = onSavePhoto
        )
        Spacer(modifier = Modifier
            .height(10.dp)
            .fillMaxWidth())
    }
    MediaButton(
        modifier = Modifier
            .align(Alignment.TopStart)
            .padding(16.dp),
        imageVector = Icons.Default.ArrowBack,
        onAction = onBack
    )
}

@Composable
internal fun PhotoImage(
    modifier: Modifier = Modifier,
    image: Uri,
    contentDescription: String? = null,
    placeholder: Painter? = null,
    fallback: Painter? = null,
    scaleType: ContentScale
) {
    AsyncImage(
        modifier = modifier,
        model = ImageRequest.Builder(LocalContext.current).data(image).crossfade(true).build(),
        placeholder = placeholder,
        contentDescription = contentDescription,
        error = fallback,
        contentScale = scaleType
    )
}

//---------Preview----------------------------------------------------------------------------------

@LightDarkPreview
@Composable
fun PreviewPhotoImage() {
    PawCalcTheme {

    }
}
