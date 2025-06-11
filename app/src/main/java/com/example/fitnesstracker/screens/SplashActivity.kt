package com.example.fitnesstracker.screens
import androidx.compose.animation.core.tween
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitnesstracker.R

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SplashScreen()
        }

        // After 2.5 seconds → move to Welcome screen
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        }, 2500)
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SplashScreen() {
    val context = LocalContext.current
    var visible by remember { mutableStateOf(false) }

    // Animate visibility after a slight delay
    LaunchedEffect(Unit) {
        visible = true
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background image (blurred gym)
        Image(
            painter = painterResource(id = R.drawable.blurred_gym_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Fade animation around the text
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(1500)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Daily Gains",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 40.sp,
                        color = androidx.compose.ui.graphics.Color.White
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Your fitness journey starts here!",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f)
                    )
                )
            }
        }
    }
}