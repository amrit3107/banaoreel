package com.banaoreel.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.banaoreel.app.ui.screens.create.CreateScreen
import com.banaoreel.app.ui.screens.home.HomeScreen
import com.banaoreel.app.ui.screens.jobstatus.JobStatusScreen
import com.banaoreel.app.ui.screens.wallet.WalletScreen

private object Routes {
    const val HOME = "home"
    const val CREATE = "create"
    const val JOB_STATUS = "job_status/{jobId}"
    const val WALLET = "wallet"

    fun jobStatus(jobId: String) = "job_status/$jobId"
}

@Composable
fun BanaoReelNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.HOME) {

        composable(Routes.HOME) {
            HomeScreen(
                onCreateClick = { navController.navigate(Routes.CREATE) },
                onWalletClick = { navController.navigate(Routes.WALLET) },
                onJobClick = { jobId -> navController.navigate(Routes.jobStatus(jobId)) }
            )
        }

        composable(Routes.CREATE) {
            CreateScreen(
                onJobCreated = { jobId ->
                    navController.navigate(Routes.jobStatus(jobId)) {
                        popUpTo(Routes.HOME)
                    }
                }
            )
        }

        composable(Routes.JOB_STATUS) {
            // jobId is read from SavedStateHandle inside JobStatusViewModel
            JobStatusScreen(
                onDone = { /* TODO: navigate to preview/result screen once scaffolded */ }
            )
        }

        composable(Routes.WALLET) {
            WalletScreen()
        }
    }
}
