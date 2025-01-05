@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalSharedTransitionApi::class)

package com.anynetwork.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
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
                        ExternalProfileRoot(
                            navController = navController,
                            id = it.toRoute<Route.ExternalProfileWithOffset>().id,
                            clickOffsetX = it.toRoute<Route.ExternalProfileWithOffset>().clickOffsetX,
                            clickOffsetY = it.toRoute<Route.ExternalProfileWithOffset>().clickOffsetY,
                        )
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

