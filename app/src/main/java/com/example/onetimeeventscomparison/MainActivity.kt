package com.example.onetimeeventscomparison

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.onetimeeventscomparison.ui.theme.OneTimeEventsComparisonTheme
import kotlinx.coroutines.flow.Flow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OneTimeEventsComparisonTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        val viewModel = viewModel<LoginViewModel>()
                        val state = viewModel.state

//                        LaunchedEffect(state.isLoggedIn) {
//                            if (state.isLoggedIn) {
//                                navController.navigate("profile")
//                                viewModel.onNavigatedToLogin()//Костыль
//                            }
//                        }

                        ObserverAsEvent(flow = viewModel.navigationEventChannelFlow) { event ->
                            when (event) {
                                is NavigationEvent.NavigateToProfile -> {
                                    navController.navigate("profile")
                                }

                                is NavigationEvent.CountEvent -> {
                                    println("COUNT: ${event.count}")

                                    /**
                                     * 2024-05-27 17:37:42.385 26171-26171 System.out              com.example.onetimeeventscomparison  I  COUNT: 453
                                     * 2024-05-27 17:37:42.422 26171-26171 System.out              com.example.onetimeeventscomparison  I  COUNT INTERRUPT
                                     * 2024-05-27 17:37:42.565 26171-26171 System.out              com.example.onetimeeventscomparison  I  COUNT: 455
                                     */

                                }
                            }
                        }

                        LoginScreen(
                            state = state,
                            onLoginClick = viewModel::login
                        )
                    }
                    composable("profile") {
                        ProfileScreen()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        println("COUNT INTERRUPT")
    }
}

@Composable
private fun <T> ObserverAsEvent(flow: Flow<T>, onEvent: (T) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect(onEvent)
        }
    }
}

@Composable
private fun LoginScreen(state: LoginState, onLoginClick: () -> Unit) {
    Surface {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {

            Button(onClick = onLoginClick) {
                Text(text = "Login")
            }

            if (state.isLoading) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun ProfileScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Profile")
    }
}
