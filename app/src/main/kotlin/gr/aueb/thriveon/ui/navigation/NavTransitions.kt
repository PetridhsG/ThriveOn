package gr.aueb.thriveon.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.ui.graphics.TransformOrigin

fun AnimatedContentTransitionScope<*>.enterTransition() =
    scaleIn(
        initialScale = 0.95f,
        animationSpec = tween(durationMillis = 500),
        transformOrigin = TransformOrigin.Center
    ) + fadeIn(animationSpec = tween(durationMillis = 500))

fun AnimatedContentTransitionScope<*>.exitTransition() =
    scaleOut(
        targetScale = 1.05f,
        animationSpec = tween(durationMillis = 500),
        transformOrigin = TransformOrigin.Center
    ) + fadeOut(animationSpec = tween(durationMillis = 500))

fun AnimatedContentTransitionScope<*>.popEnterTransition() =
    scaleIn(
        initialScale = 1.05f,
        animationSpec = tween(durationMillis = 500),
        transformOrigin = TransformOrigin.Center
    ) + fadeIn(animationSpec = tween(durationMillis = 500))

fun AnimatedContentTransitionScope<*>.popExitTransition() =
    scaleOut(
        targetScale = 0.95f,
        animationSpec = tween(durationMillis = 500),
        transformOrigin = TransformOrigin.Center
    ) + fadeOut(animationSpec = tween(durationMillis = 500))

