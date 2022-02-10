package com.example.jetpackwebview

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.jetpackwebview.MainDestinations.WEBVIEWSCREEN
import com.example.jetpackwebview.MainDestinations.WEBVIEW_USERAGENT_SET
import com.example.jetpackwebview.MainDestinations.MAINSCREEN
import com.example.jetpackwebview.ui.theme.JetpackWebViewTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackWebViewTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    NavGraph()
                }
            }
        }
    }
}

object MainDestinations {
    const val MAINSCREEN = "mainscreen"
    const val WEBVIEWSCREEN = "webviewscreen"
    const val WEBVIEW_USERAGENT_SET = "webview_useragent_set"
}

@Composable
fun NavGraph(startDestination: String = MAINSCREEN) {
    val navController = rememberNavController()
    val actions = remember(navController) { MainActions(navController) }
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(MAINSCREEN) {
            MainScreen(actions)
        }
        composable(
            "$WEBVIEWSCREEN/{$WEBVIEW_USERAGENT_SET}",
            arguments = listOf(navArgument(WEBVIEW_USERAGENT_SET) {
                type = NavType.BoolType
            })
        ) { backStackEntry ->
            val arguments = requireNotNull(backStackEntry.arguments)
            ChildScreen(arguments.getBoolean(WEBVIEW_USERAGENT_SET), actions)
        }
    }
}

class MainActions(navController: NavHostController) {
    val mainScreen: () -> Unit = {
        navController.navigate(MAINSCREEN)
    }
    val webviewScreen: (Boolean) -> Unit = { setting ->
        navController.navigate("$WEBVIEWSCREEN/$setting")
    }
    val upPress: () -> Unit = {
        navController.navigateUp()
    }
}

@Composable
fun MainScreen(actions: MainActions) {
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            MyButton("Webview Default UserAgent") {
                actions.webviewScreen(false)
            }
            MyButton("Webview Custom UserAgent") {
                actions.webviewScreen(true)
            }
        }
    }
}

@Composable
fun ColumnScope.MyButton(
    title: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .weight(1f)
            .padding(8.dp)
            .fillMaxSize()
    ) {
        Text(title)
    }
}

@Composable
fun ChildScreen(setUserAgent: Boolean?, actions: MainActions) {
    AndroidView(factory = {
        WebView.setWebContentsDebuggingEnabled(true)
        WebView(it).apply {
            webViewClient = WebViewClient()
            if (setUserAgent == true) {
                settings.userAgentString = "My-User-Agent"
            }
            loadUrl("https://elye-project.medium.com/")
        }
    })
}
