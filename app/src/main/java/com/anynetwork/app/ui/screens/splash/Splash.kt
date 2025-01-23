package com.anynetwork.app.ui.screens.splash

import android.content.Context
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.anynetwork.app.R
import com.anynetwork.app.ui.navigation.Route
import com.anynetwork.app.ui.screens.home.HomeViewModel
import com.anynetwork.app.ui.utils.fdph
import com.anynetwork.app.ui.utils.fdpv
import com.anynetwork.app.ui.utils.xdph
import com.anynetwork.app.ui.utils.xdpv
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun SplashRoot(navController: NavHostController) {
    Box(modifier = Modifier.background(Color(0xFF120E1E))) {
        val context = LocalContext.current
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val skipOnboarding = sharedPreferences.getBoolean("onboardingCompleted", false)

        val coroutineScope = rememberCoroutineScope()
        var isSplashLogo1Visible by remember { mutableStateOf(false) }
        var isSplashLogo2Visible by remember { mutableStateOf(false) }
        var isSplashLogoPurpleHexVisible by remember { mutableStateOf(false) }
        var isSplashLogoPurpleHexScaled by remember { mutableStateOf(false) }

        var normalAnimations = true

        LaunchedEffect(Unit) {
            isSplashLogo1Visible = true
            delay(if (!normalAnimations) 100 else 1000) // Delay before starting the second animation

            isSplashLogoPurpleHexVisible = true
            isSplashLogo2Visible = true
            delay(if (!normalAnimations) 50 else 1000)
        }

        val animationDuration = 1000

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .width(143.xdph)
                            .height(180.54.xdpv)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        val purpleHexScaleAnimation = animateFloatAsState(
                            targetValue = if (isSplashLogoPurpleHexScaled) 32f else 1f,
                            animationSpec = tween(durationMillis = 500),
                            finishedListener = {
                                if (!skipOnboarding) {
                                    navController.navigate(Route.Onboarding)
                                } else {
                                    navController.navigate(Route.Home) {
                                        launchSingleTop = true
                                        Timber.i("home start animation navigate to Home")
                                        popUpTo(Route.Splash) { inclusive = true } // Remove Splash from back stack
                                    }
                                }
                            }
                        )

                        if (isSplashLogoPurpleHexVisible) Image(
                            modifier = Modifier
                                .padding(top = 28.73.fdpv, start = 42.55.fdph)
                                .width(44.62.fdph)
                                .height(38.89.fdpv)
                                .scale(purpleHexScaleAnimation.value),
                            painter = painterResource(R.drawable.splash_logo_1_purple_hex),
                            contentDescription = "back button",
                        )

                        val alpha by animateFloatAsState(
                            targetValue = if (isSplashLogo1Visible) 1f else 0f,
                            animationSpec = tween(
                                durationMillis = animationDuration,
                                easing = FastOutSlowInEasing
                            )
                        )

                        Image(
                            modifier = Modifier
                                .alpha(alpha)
                                .fillMaxSize(),
                            painter = painterResource(R.drawable.splash_logo_1),
                            contentDescription = "back button",
                        )
                    }

                    Spacer(modifier = Modifier.height(23.9.xdpv))

                    val alpha2 by animateFloatAsState(
                        targetValue = if (isSplashLogo2Visible) 1f else 0f,
                        animationSpec = tween(
                            durationMillis = animationDuration,
                            easing = FastOutSlowInEasing
                        ),
                        finishedListener = {
                            coroutineScope.launch {
                                isSplashLogo1Visible = false
                                isSplashLogo2Visible = false

                                delay(if (!normalAnimations) 50 else 500) // Delay before starting the third animation
                                isSplashLogoPurpleHexScaled = true
                            }
                        }
                    )
                    Image(
                        modifier = Modifier
                            .width(136.xdph)
                            .height(39.5.xdpv)
                            .align(Alignment.CenterHorizontally)
                            .alpha(alpha2),
                        painter = painterResource(R.drawable.splash_logo_2),
                        contentDescription = "back button",
                    )

                    Spacer(modifier = Modifier.height(15.5.xdpv))

                    Image(
                        modifier = Modifier
                            .width(102.xdph)
                            .height(13.02.xdpv)
                            .align(Alignment.CenterHorizontally)
                            .alpha(alpha2),
                        painter = painterResource(R.drawable.splash_logo_3),
                        contentDescription = "back button",
                    )
                }
        }
    }
}