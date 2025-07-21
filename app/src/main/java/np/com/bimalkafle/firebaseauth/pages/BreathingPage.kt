package np.com.bimalkafle.firebaseauth.pages

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

// --- State Definitions ---
private enum class ExerciseState {
    IDLE, GET_READY, PREPARE_INHALE, BREATHING, FINISHED
}
private enum class BreathingPhase(val instruction: String, val duration: Long) {
    INHALE("Breathe in", 4000L),
    EXHALE("Breathe out", 6000L)
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BreathingPage(navController: NavController) {
    // --- State Management & Colors ---
    var exerciseState by remember { mutableStateOf(ExerciseState.IDLE) }
    var breathingPhase by remember { mutableStateOf(BreathingPhase.INHALE) }
    val basePurple = Color(0xFF512DA8)
    val lightPurple = Color(0xFF9575CD)
    val darkPurpleBg = Color(0xFF311B92)

    // --- State Machine Logic (controls timing) ---
    LaunchedEffect(exerciseState) {
        when (exerciseState) {
            ExerciseState.GET_READY -> { delay(3000); exerciseState = ExerciseState.PREPARE_INHALE }
            ExerciseState.PREPARE_INHALE -> { delay(2000); exerciseState = ExerciseState.BREATHING }
            ExerciseState.BREATHING -> { delay(60000); exerciseState = ExerciseState.FINISHED }
            ExerciseState.FINISHED -> { delay(3000); exerciseState = ExerciseState.IDLE }
            else -> Unit
        }
    }
    LaunchedEffect(exerciseState, breathingPhase) {
        if (exerciseState == ExerciseState.BREATHING) {
            delay(breathingPhase.duration)
            breathingPhase = if (breathingPhase == BreathingPhase.INHALE) BreathingPhase.EXHALE else BreathingPhase.INHALE
        }
    }

    // --- Animation Values ---
    val instructionText = when (exerciseState) {
        ExerciseState.IDLE -> "1-minute breathing exercise"
        ExerciseState.GET_READY -> "Bring awareness to your breath"
        ExerciseState.PREPARE_INHALE -> ""
        ExerciseState.BREATHING -> breathingPhase.instruction
        ExerciseState.FINISHED -> "Well done!"
    }
    val targetScale = when (exerciseState) {
        ExerciseState.PREPARE_INHALE -> 0.2f
        ExerciseState.BREATHING -> if (breathingPhase == BreathingPhase.INHALE) 1.0f else 0.2f
        else -> 1.0f
    }
    val scale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(durationMillis = if (exerciseState == ExerciseState.BREATHING) breathingPhase.duration.toInt() else 1500),
        label = "circleScale"
    )

    // --- Main UI Layout ---
    Box(
        modifier = Modifier.fillMaxSize().background(darkPurpleBg),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            tint = Color.White.copy(alpha = 0.8f),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(24.dp)
                .size(32.dp)
                .clickable { navController.popBackStack() }
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(250.dp),
                contentAlignment = Alignment.Center
            ) {
                // Circles and Play Button now correctly structured
                if (exerciseState == ExerciseState.IDLE) {
                    IdlePlayButton { exerciseState = ExerciseState.GET_READY }
                } else {
                    AnimationCircles(scale = scale, outerColor = basePurple, innerColor = lightPurple)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedContent(
                targetState = instructionText,
                transitionSpec = {
                    fadeIn(animationSpec = tween(1000)) togetherWith
                            fadeOut(animationSpec = tween(1000))
                }, label = "textAnimation"
            ) { text ->
                Text(
                    text = text,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// --- Helper UI Components ---

@Composable
private fun IdlePlayButton(onClick: () -> Unit) {
    // This uses AnimatedVisibility internally to handle its own fading
    AnimatedVisibility(
        visible = true, // It's always "visible" when this composable is on screen
        enter = fadeIn(animationSpec = tween(1000)),
        exit = fadeOut(animationSpec = tween(1000))
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.1f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Start Exercise",
                tint = Color.White,
                modifier = Modifier.size(100.dp)
            )
        }
    }
}

@Composable
private fun AnimationCircles(scale: Float, outerColor: Color, innerColor: Color) {
    // This uses AnimatedVisibility internally to handle its own fading
    AnimatedVisibility(
        visible = true, // It's always "visible" when this composable is on screen
        enter = fadeIn(animationSpec = tween(1000)),
        exit = fadeOut(animationSpec = tween(1000))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(outerColor.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(scale)
                    .clip(CircleShape)
                    .background(innerColor)
            )
        }
    }
}