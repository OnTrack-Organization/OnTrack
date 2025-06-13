package de.ashman.ontrack.ads

import android.content.Context
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.valentinilk.shimmer.shimmer
import de.ashman.ontrack.features.detail.components.MediaTitle
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.ad_attribution
import org.jetbrains.compose.resources.stringResource

@Composable
actual fun NativeAdPoster(
    modifier: Modifier,
) {
    val context = LocalContext.current
    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }
    val adUnitId = "ca-app-pub-7769337200974564/8538755946"

    LaunchedEffect(Unit) {
        loadNativeAd(context, adUnitId) { ad ->
            nativeAd = ad
        }
    }

    // Show ad or loading shimmer box
    nativeAd?.let { ad ->
        PosterNativeAdView(ad, modifier)
    } ?: Box(
        modifier = modifier
            .aspectRatio(2f / 3f)
            .clip(MaterialTheme.shapes.medium)
            .shimmer()
            .background(MaterialTheme.colorScheme.surfaceVariant)
    )
}

@Composable
fun PosterNativeAdView(
    ad: NativeAd,
    modifier: Modifier = Modifier,
) {
    val attributionText = stringResource(Res.string.ad_attribution)

    Column(
        modifier = Modifier.width(IntrinsicSize.Min),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        AndroidView(
            modifier = modifier
                .aspectRatio(2f / 3f)
                .clip(MaterialTheme.shapes.medium),
            factory = { context ->
                NativeAdView(context).apply {
                    val mediaView = MediaView(context).apply {
                        layoutParams = FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT
                        )
                        setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                    }

                    val attributionView = TextView(context).apply {
                        text = attributionText
                        setTextColor(android.graphics.Color.WHITE)
                        setBackgroundColor(0x66000000)
                        textSize = 12f
                        setPadding(12, 6, 12, 6)
                        layoutParams = FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.WRAP_CONTENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            gravity = Gravity.BOTTOM or Gravity.END
                            setMargins(0, 0, 16, 16)
                        }
                    }

                    addView(mediaView)
                    setMediaView(mediaView)
                    addView(attributionView)
                    setNativeAd(ad)
                }
            }
        )

        MediaTitle(
            textStyle = MaterialTheme.typography.titleLarge,
            title = ad.headline,
        )
    }
}

fun loadNativeAd(context: Context, adUnitId: String, callback: (NativeAd?) -> Unit) {
    val builder = AdLoader.Builder(context, adUnitId).forNativeAd { nativeAd -> callback(nativeAd) }

    val adLoader = builder
        .withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                callback(null)
            }
        })
        .withNativeAdOptions(NativeAdOptions.Builder().build())
        .build()

    adLoader.loadAd(AdRequest.Builder().build())
}