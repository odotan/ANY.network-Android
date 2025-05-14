package com.anynetwork.app

import android.app.ComponentCaller
import android.content.Intent
import android.graphics.Color
import android.net.Uri
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.anynetwork.app.data.networkauth.FacebookNetworkAuthentication
import com.anynetwork.app.ui.navigation.MyProfileRoute
import com.anynetwork.app.ui.navigation.Route
import com.anynetwork.app.ui.screens.connect.ConnectRoot
import com.anynetwork.app.ui.screens.connectsuccess.ConnectSuccessScreenRoot
import com.anynetwork.app.ui.screens.externalprofile.ExternalProfileRoot
import com.anynetwork.app.ui.screens.hashsearch.HashSearchScreen
import com.anynetwork.app.ui.screens.home.HomeRoot
import com.anynetwork.app.ui.screens.home.HomeViewModel
import com.anynetwork.app.ui.screens.myprofile.MyProfileRoot
import com.anynetwork.app.ui.screens.onboarding.OnboardingRoot
import com.anynetwork.app.ui.screens.splash.SplashRoot
import com.anynetwork.app.ui.screens.testing.GridPlaygroundScreen
import com.anynetwork.app.ui.screens.words.ShowMyPhraseScreenRoot
import com.anynetwork.app.ui.theme.ANYnetworkTheme
import com.anynetwork.app.ui.utils.log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var facebookAuth: FacebookNetworkAuthentication

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
                val homeNavController = rememberNavController()
                val myProfileNavController = rememberNavController()

                val homeViewModel = hiltViewModel<HomeViewModel>()

                fun handleDeepLink(deepLink: Uri) {
                    Timber.i("deepLink: $deepLink")
                    if (deepLink.toString().contains("oobCode") && deepLink.toString().contains("mode=signIn")) {
                        myProfileNavController.navigate(
                            MyProfileRoute.Connect(
                                mode = "email",
                                emailSignInLink = deepLink.toString()
                            )
                        )
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = /*Route.Splash*/Route.HashSearch,
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
                    composable<Route.HashSearch> { HashSearchScreen() }

                    composable<Route.Splash> { SplashRoot(navController) }

                    composable<Route.GridPlayground> {
                        it.toRoute<Route.GridPlayground>().mode.let { it -> GridPlaygroundScreen(mode = it) }
                    }

                    composable<Route.Onboarding> { OnboardingRoot(navController) }

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
                        HomeRoot(
                            viewModel = homeViewModel,
                            homeNavController = homeNavController
                        )
                    }
                }

                NavHost(
                    navController = homeNavController,
                    startDestination = Route.Home,
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
                    composable<Route.Home> {  }

                    composable<Route.ExternalProfileNotExploding> {
                        ExternalProfileRoot(
                            navController = homeNavController,
                            id = it.toRoute<Route.ExternalProfileNotExploding>().id,
                            homeViewModel = homeViewModel,
                            onContactUpdated = {
                                Timber.i("onContactUpdated")
                                homeViewModel.reloadData()
                            },
                            onBackPress = {
                                homeNavController.popBackStack(Route.Home, inclusive = false)
                            }
                        )
                    }

                    composable<Route.MyProfile> {
                        val transitionDuration = 300 // Duration of the explosion animation
                        val x = it.toRoute<Route.MyProfile>().offsetX
                        val y = it.toRoute<Route.MyProfile>().offsetY

                        val currentConfig = LocalConfiguration.current
                        val width = with(LocalDensity.current) { currentConfig.screenWidthDp.dp.toPx() }
                        val height = with(LocalDensity.current) { currentConfig.screenHeightDp.dp.toPx() }

                        val initialX = x - (width / 2)
                        val initialY = y - (height / 2)

                        // Define animation states
                        var scaleX by remember { mutableStateOf(0.1f) } // Start scaled based on initial width
                        var scaleY by remember { mutableStateOf(0.1f) } // Start scaled based on initial height
                        var translateX by remember { mutableStateOf((initialX).log { "cellPosition.boundsInRoot().center.x" }) }
                        var translateY by remember { mutableStateOf((initialY).log { "cellPosition.boundsInRoot().center.y" }) }
                        var opacity by remember { mutableStateOf(0f) }

                        var isVisible by remember { mutableStateOf(true) }

                        // Trigger the animation when the screen is displayed
                        LaunchedEffect(Unit) {
                            scaleX = 1f // Explode slightly larger than the screen
                            scaleY = 1f
                            translateX = 0f // Move to center
                            translateY = 0f
                            opacity = 1f
                        }

                        // Animate values
                        var isEnterAnimationFinished by remember { mutableStateOf(false) }
                        val animatedScaleX = animateFloatAsState(
                            targetValue = scaleX,
                            animationSpec = tween(durationMillis = transitionDuration)
                        )
                        val animatedScaleY = animateFloatAsState(
                            targetValue = scaleY,
                            animationSpec = tween(durationMillis = transitionDuration)
                        )
                        val animatedTranslateX = animateFloatAsState(
                            targetValue = translateX,
                            animationSpec = tween(durationMillis = transitionDuration)
                        )
                        val animatedTranslateY = animateFloatAsState(
                            targetValue = translateY,
                            animationSpec = tween(durationMillis = transitionDuration)
                        )
                        val animatedOpacity = animateFloatAsState(
                            targetValue = opacity,
                            animationSpec = tween(durationMillis = transitionDuration),
                            finishedListener = {
                                isEnterAnimationFinished = true
                            }
                        )

                        // Apply animations to the DetailScreen
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer(
                                    scaleX = animatedScaleX.value,
                                    scaleY = animatedScaleY.value,
                                    translationX = animatedTranslateX.value,  // Center horizontally
                                    translationY = animatedTranslateY.value, // Center vertically
                                    alpha = animatedOpacity.value
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            MyProfileRoot(
                                navController = myProfileNavController,
                                onContactUpdated = {
                                    Timber.i("onContactUpdated")
                                    homeViewModel.loadProfile()
                                },
                                isEnterAnimationFinished = isEnterAnimationFinished,
                                onBackPress = {
                                    // Trigger exit animation
                                    scaleX = 0.01f
                                    scaleY = 0.01f
                                    translateX = initialX
                                    translateY = initialY
                                    opacity = 0f

                                    // Wait for animation to finish before changing the route
                                    LaunchedEffect(Unit) {
                                        delay(transitionDuration.toLong()) // Wait for animation to complete
                                        homeNavController.popBackStack(Route.Home, inclusive = false)
                                    }
                                },

                            )
                        }
                    }

                    composable<Route.ExternalProfile> {
                        val transitionDuration = 300 // Duration of the explosion animation
                        val x = it.toRoute<Route.ExternalProfile>().offsetX
                        val y = it.toRoute<Route.ExternalProfile>().offsetY

                        val currentConfig = LocalConfiguration.current
                        val width = with(LocalDensity.current) { currentConfig.screenWidthDp.dp.toPx() }
                        val height = with(LocalDensity.current) { currentConfig.screenHeightDp.dp.toPx() }

                        val initialX = x - (width / 2)
                        val initialY = y - (height / 2)

                        // Define animation states
                        var scaleX by remember { mutableStateOf(0.1f) } // Start scaled based on initial width
                        var scaleY by remember { mutableStateOf(0.1f) } // Start scaled based on initial height
                        var translateX by remember { mutableStateOf((initialX).log { "cellPosition.boundsInRoot().center.x" }) }
                        var translateY by remember { mutableStateOf((initialY).log { "cellPosition.boundsInRoot().center.y" }) }
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
                        var isEnterAnimationFinished by remember { mutableStateOf(false) }
                        val animatedScaleX = animateFloatAsState(
                            targetValue = scaleX,
                            animationSpec = tween(durationMillis = transitionDuration)
                        )
                        val animatedScaleY = animateFloatAsState(
                            targetValue = scaleY,
                            animationSpec = tween(durationMillis = transitionDuration)
                        )
                        val animatedTranslateX = animateFloatAsState(
                            targetValue = translateX,
                            animationSpec = tween(durationMillis = transitionDuration)
                        )
                        val animatedTranslateY = animateFloatAsState(
                            targetValue = translateY,
                            animationSpec = tween(durationMillis = transitionDuration)
                        )
                        val animatedOpacity = animateFloatAsState(
                            targetValue = opacity,
                            animationSpec = tween(durationMillis = transitionDuration),
                            finishedListener = {
                                isEnterAnimationFinished = true
                            }
                        )

                        // Apply animations to the DetailScreen
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer(
                                    scaleX = animatedScaleX.value,
                                    scaleY = animatedScaleY.value,
                                    translationX = animatedTranslateX.value,  // Center horizontally
                                    translationY = animatedTranslateY.value, // Center vertically
                                    alpha = animatedOpacity.value
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            ExternalProfileRoot(
                                navController = homeNavController,
                                id = it.toRoute<Route.ExternalProfile>().id,
                                isEnterAnimationFinished = isEnterAnimationFinished,
                                onContactUpdated = {
                                    Timber.i("onContactUpdated")
                                    homeViewModel.reloadData()
                                },
                                homeViewModel = homeViewModel,
                                onBackPress = {
                                    // Trigger exit animation
                                    scaleX = 0.01f
                                    scaleY = 0.01f
                                    translateX = initialX
                                    translateY = initialY
                                    opacity = 0f

                                    // Wait for animation to finish before changing the route
                                    LaunchedEffect(Unit) {
                                        delay(transitionDuration.toLong()) // Wait for animation to complete
                                        homeNavController.popBackStack(Route.Home, inclusive = false)
                                    }
                                }
                            )
                        }
                    }

                    composable<Route.NewContact> {
                        ExternalProfileRoot(
                            navController = navController,
                            id = null,
                            input = it.toRoute<Route.NewContact>().input,
                            onContactUpdated = {
                                homeViewModel.reloadData()
                            },
                            homeViewModel = homeViewModel,
                            onBackPress = {
                                homeNavController.popBackStack(Route.Home, inclusive = false)
                            },
                        )
                    }
                }

                NavHost(
                    navController = myProfileNavController,
                    startDestination = MyProfileRoute.MyProfile
                ) {
                    composable<MyProfileRoute.MyProfile> {  }

                    composable<MyProfileRoute.ShowMyPhrase> {
                        ShowMyPhraseScreenRoot(myProfileNavController)
                    }

                    composable<MyProfileRoute.Connect> {
                        val emailSignInLink = it.toRoute<MyProfileRoute.Connect>().emailSignInLink
                        ConnectRoot(
                            navController = myProfileNavController,
                            emailSignInLink = emailSignInLink,
                            mode = it.toRoute<MyProfileRoute.Connect>().mode
                        )
                    }

                    composable<MyProfileRoute.ConnectSuccess> {
                        ConnectSuccessScreenRoot(
                            navController = myProfileNavController,
                            mode = it.toRoute<MyProfileRoute.ConnectSuccess>().mode
                        )
                    }
                }

                val deepLink = intent?.data
                deepLink?.let {
                    handleDeepLink(it)
                }
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        caller: ComponentCaller
    ) {
        Timber.i("onActivityResult")
        super.onActivityResult(requestCode, resultCode, data, caller)
        facebookAuth.callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}

