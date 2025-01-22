@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3Api::class
)

package com.anynetwork.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.anynetwork.app.ui.components.text.toFloatPx
import com.anynetwork.app.ui.navigation.Route
import com.anynetwork.app.ui.screens.connect.ConnectRoot
import com.anynetwork.app.ui.screens.contactspermissions.ContactsPermissionsRoot
import com.anynetwork.app.ui.screens.externalprofile.ExternalProfileRoot
import com.anynetwork.app.ui.screens.home.HomeRoot
import com.anynetwork.app.ui.screens.myprofile.MyProfileRoot
import com.anynetwork.app.ui.screens.onboarding.OnboardingRoot
import com.anynetwork.app.ui.screens.splash.SplashRoot
import com.anynetwork.app.ui.screens.testing.GridPlaygroundScreen
import com.anynetwork.app.ui.theme.ANYnetworkTheme
import com.anynetwork.app.ui.utils.log
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()
        actionBar?.hide()
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT, Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT, Color.TRANSPARENT
            )
        )

        WindowCompat.setDecorFitsSystemWindows(window, false) // Ensures the window doesn't resize on keyboard appearance


        // Set window flags for fullscreen mode
        setContent {
            ANYnetworkTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Route.Splash,
                    enterTransition = {
                        fadeIn()
                    },
                    exitTransition = { fadeOut() },
                    popEnterTransition = {
                        fadeIn()
                    },
                    popExitTransition = {
                        fadeOut()
                    }
                ) {
                    composable<Route.Splash> { SplashRoot(navController) }

                    composable<Route.GridPlayground> {
                        it.toRoute<Route.GridPlayground>().mode.let { it -> GridPlaygroundScreen(mode = it) }
                    }

                    composable<Route.Onboarding> { OnboardingRoot(navController) }

                    composable<Route.Connect> { ConnectRoot() }

                    composable<Route.ContactsPermissions> { ContactsPermissionsRoot(navController, contentResolver) }

//                        composable<Route.Search> { SearchRoot(navController) }

                    composable<Route.NewContact> {
                        ExternalProfileRoot(
                            navController = navController,
                            id = null,
                            input = it.toRoute<Route.NewContact>().input,
                        )
                    }

                    composable<Route.MyProfile> {
                        MyProfileRoot(navController)
                    }

                    composable<Route.ExternalProfileWithOffset> {
                        val transitionDuration = 600 // Duration of the explosion animation
                        val x = it.toRoute<Route.ExternalProfileWithOffset>().clickOffsetX
                        val y = it.toRoute<Route.ExternalProfileWithOffset>().clickOffsetY
                        val currentConfig = LocalConfiguration.current
                        val width = currentConfig.screenWidthDp.toFloat()
                        val height = currentConfig.screenHeightDp.toFloat()

                        // Define animation states
                        var scaleX by remember { mutableStateOf(width / 1000f) } // Start scaled based on initial width
                        var scaleY by remember { mutableStateOf(height / 1000f) } // Start scaled based on initial height
                        var translateX by remember { mutableStateOf(x) }
                        var translateY by remember { mutableStateOf(y) }
                        var opacity by remember { mutableStateOf(0f) }

                        // Trigger the animation when the screen is displayed
                        LaunchedEffect(Unit) {
                            scaleX = 1f // Explode slightly larger than the screen
                            scaleY = 1f
                            translateX = 0f // Move to center
                            translateY = 0f
                            opacity = 1f
                        }

                        // Animate values
                        val animatedScaleX = animateFloatAsState(targetValue = scaleX, animationSpec = tween(durationMillis = transitionDuration))
                        val animatedScaleY = animateFloatAsState(targetValue = scaleY, animationSpec = tween(durationMillis = transitionDuration))
                        val animatedTranslateX = animateFloatAsState(targetValue = translateX, animationSpec = tween(durationMillis = transitionDuration))
                        val animatedTranslateY = animateFloatAsState(targetValue = translateY, animationSpec = tween(durationMillis = transitionDuration))
                        val animatedOpacity = animateFloatAsState(targetValue = opacity, animationSpec = tween(durationMillis = transitionDuration))

                        // Apply animations to the DetailScreen
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer(
                                    scaleX = animatedScaleX.value,
                                    scaleY = animatedScaleY.value,
                                    translationX = animatedTranslateX.value,
                                    translationY = animatedTranslateY.value,
                                    alpha = animatedOpacity.value
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            ExternalProfileRoot(
                                navController = navController,
                                id = it.toRoute<Route.ExternalProfileWithOffset>().id,
                                clickOffsetX = it.toRoute<Route.ExternalProfileWithOffset>().clickOffsetX,
                                clickOffsetY = it.toRoute<Route.ExternalProfileWithOffset>().clickOffsetY,
                            )
                        }
                    }

                    composable<Route.Home>(
                        enterTransition = {
                            fadeIn()
                        },
                        exitTransition = { fadeOut() },
                        popEnterTransition = {
                            fadeIn()
                        },
                        popExitTransition = {
                            fadeOut()
                        }
                    ) {
                        HomeRoot(navController)
                    }

                    composable<Route.ExternalProfile> {
                        val transitionDuration = 600 // Duration of the explosion animation
                        val x = it.toRoute<Route.ExternalProfile>().offsetX
                        val y = it.toRoute<Route.ExternalProfile>().offsetY
                        val currentConfig = LocalConfiguration.current
                        val width = currentConfig.screenWidthDp.toFloat()
                        val height = currentConfig.screenHeightDp.toFloat()

                        val toolbarHeight = TopAppBarDefaults.LargeAppBarCollapsedHeight.toFloatPx()

                        // Define animation states
                        var scaleX by remember { mutableStateOf(width / 1000f) } // Start scaled based on initial width
                        var scaleY by remember { mutableStateOf(height / 1000f) } // Start scaled based on initial height
                        var translateX by remember { mutableStateOf((x - width).log { "cellPosition.boundsInRoot().center.x" }) }
                        var translateY by remember { mutableStateOf((y - height - toolbarHeight).log { "cellPosition.boundsInRoot().center.y" }) }
                        var opacity by remember { mutableStateOf(0f) }

                        // Trigger the animation when the screen is displayed
                        LaunchedEffect(Unit) {
                            scaleX = 0.1f // Explode slightly larger than the screen
                            scaleY = 0.1f
//                            translateX = 0f // Move to center
//                            translateY = 0f
                            opacity = 1f
                        }

                        // Animate values
                        val animatedScaleX = animateFloatAsState(targetValue = scaleX, animationSpec = tween(durationMillis = transitionDuration))
                        val animatedScaleY = animateFloatAsState(targetValue = scaleY, animationSpec = tween(durationMillis = transitionDuration))
                        val animatedTranslateX = animateFloatAsState(targetValue = translateX, animationSpec = tween(durationMillis = transitionDuration))
                        val animatedTranslateY = animateFloatAsState(targetValue = translateY, animationSpec = tween(durationMillis = transitionDuration))
                        val animatedOpacity = animateFloatAsState(targetValue = opacity, animationSpec = tween(durationMillis = transitionDuration))

                        // Apply animations to the DetailScreen
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer(
                                    scaleX = animatedScaleX.value,
                                    scaleY = animatedScaleY.value,
                                    translationX = animatedTranslateX.value,
                                    translationY = animatedTranslateY.value,
                                    alpha = animatedOpacity.value
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            ExternalProfileRoot(
                                id = it.toRoute<Route.ExternalProfile>().id,
                                navController = navController,
                            )
                        }
                    }
                }
            }
        }
    }
}

