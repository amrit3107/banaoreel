package com.banaoreel.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.banaoreel.app.ui.screens.create.CreateScreen
import com.banaoreel.app.ui.screens.history.HistoryScreen
import com.banaoreel.app.ui.screens.home.HomeScreen
import com.banaoreel.app.ui.screens.jobstatus.JobStatusScreen
import com.banaoreel.app.ui.screens.login.LoginScreen
import com.banaoreel.app.ui.screens.onboarding.OnboardingScreen
import com.banaoreel.app.ui.screens.preview.PreviewScreen
import com.banaoreel.app.ui.screens.profile.ProfileScreen
import com.banaoreel.app.ui.screens.wallet.WalletScreen

private object Routes {
    const val LOGIN = "login"
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val CREATE = "create"
    const val JOB_STATUS = "job_status/{jobId}"
    const val PREVIEW = "preview/{jobId}"
    const val WALLET = "wallet"
    const val HISTORY = "history"
    const val PROFILE = "profile"

    fun jobStatus(jobId: String) = "job_status/$jobId"
    fun preview(jobId: String) = "preview/$jobId"
}

@Composable
fun BanaoReelNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.LOGIN) {

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoggedIn = { needsOnboarding ->
                    val destination = if (needsOnboarding) Routes.ONBOARDING else Routes.HOME
                    navController.navigate(destination) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onComplete = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onCreateClick = { navController.navigate(Routes.CREATE) },
                onWalletClick = { navController.navigate(Routes.WALLET) },
                onJobClick = { jobId -> navController.navigate(Routes.jobStatus(jobId)) },
                onHistoryClick = { navController.navigate(Routes.HISTORY) },
                onProfileClick = { navController.navigate(Routes.PROFILE) }
            )
        }

        composable(Routes.CREATE) {
            CreateScreen(
                onJobCreated = { jobId ->
                    navController.navigate(Routes.jobStatus(jobId)) {
                        popUpTo(Routes.HOME)
                    }
                },
                onGoToWallet = { navController.navigate(Routes.WALLET) }
            )
        }

        composable(Routes.JOB_STATUS) {
            JobStatusScreen(
                onDone = { job ->
                    navController.navigate(Routes.preview(job.id)) {
                        popUpTo(Routes.HOME)
                    }
                }
            )
        }

        composable(Routes.PREVIEW) {
            PreviewScreen(
                onGenerateAnother = {
                    navController.navigate(Routes.CREATE) {
                        popUpTo(Routes.HOME)
                    }
                }
            )
        }

        composable(Routes.WALLET) {
            WalletScreen()
        }

        composable(Routes.HISTORY) {
            HistoryScreen(
                onJobClick = { jobId -> navController.navigate(Routes.preview(jobId)) }
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                onLoggedOut = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) // clear the entire back stack, not just up to Home
                    }
                }
            )
        }
    }
}
