package gr.aueb.thriveon.ui.screens.signIn

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions.*
import com.google.android.gms.common.api.ApiException
import gr.aueb.thriveon.R
import gr.aueb.thriveon.ui.common.components.CustomSnackbarHost
import gr.aueb.thriveon.ui.common.components.ThriveOnCircularProgressIndicator
import gr.aueb.thriveon.ui.common.components.wrap.WrapButton
import gr.aueb.thriveon.ui.common.components.wrap.WrapScaffold
import gr.aueb.thriveon.ui.screens.signIn.model.SignInContract
import gr.aueb.thriveon.ui.theme.Colors
import gr.aueb.thriveon.ui.theme.Typography
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
@Composable
fun SignInScreen(
    state: SignInContract.State,
    onEvent: (SignInContract.Event) -> Unit,
    effect: Flow<SignInContract.Effect>,
    onNavigateToSignUpCategories: () -> Unit,
    onNavigateToHome: (String) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val colors = MaterialTheme.Colors
    val typography = MaterialTheme.Typography
    val snackbarHostState = remember { SnackbarHostState() }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            onEvent(SignInContract.Event.OnGoogleSignInSuccess(account))
        } catch (e: ApiException) {
            onEvent(
                SignInContract.Event.OnGoogleSignInFailed(
                    e.message ?: context.getString(R.string.sign_in_screen_sign_in_failed)
                )
            )
        }
    }

    BackHandler(enabled = true) {}

    LaunchedEffect(Unit) {
        onEvent(SignInContract.Event.Init)
        effect.collect { effect ->
            when (effect) {
                SignInContract.Effect.LaunchGoogleAccountPicker -> {
                    val gso = Builder(DEFAULT_SIGN_IN)
                        .requestIdToken(context.getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build()
                    val googleClient = GoogleSignIn.getClient(context, gso)
                    launcher.launch(googleClient.signInIntent)
                }

                SignInContract.Effect.NavigateToHome -> {
                    onNavigateToHome("true")
                }

                SignInContract.Effect.NavigateToSignUpCategories -> {
                    onNavigateToSignUpCategories()
                }

                SignInContract.Effect.DismissSnackbar -> {
                    snackbarHostState.currentSnackbarData?.dismiss()
                }

                is SignInContract.Effect.ShowSnackbar -> {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = effect.message,
                            duration = SnackbarDuration.Indefinite
                        )
                    }

                    coroutineScope.launch {
                        delay(3000)
                        snackbarHostState.currentSnackbarData?.dismiss()
                    }
                }
            }
        }
    }

    WrapScaffold(
        containerColor = colors.backgroundMaroonDark,
        snackbarHost = { CustomSnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 100.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when {
                    state.isLoading -> {
                        ThriveOnCircularProgressIndicator()
                    }

                    else -> {
                        AsyncImage(
                            model = R.drawable.thrive_on_logo,
                            contentDescription = null,
                            modifier = Modifier
                                .width(225.dp)
                                .clickable {
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        "https://shattereddisk.github.io/rickroll/rickroll.mp4".toUri()
                                    )
                                    context.startActivity(intent)
                                }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (state.isSignedIn)
                                stringResource(R.string.sign_in_screen_welcome_back)
                            else
                                stringResource(R.string.sign_in_screen_signing_in),
                            color = colors.textWhite,
                            fontFamily = typography.gantari.fontFamily
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (!state.isSignedIn) {
                            OutlinedButton(
                                onClick = {
                                    onEvent(SignInContract.Event.OnGoogleAccountRequest)
                                },
                                modifier = Modifier
                                    .width(150.dp)
                                    .align(Alignment.CenterHorizontally),
                                border = BorderStroke(1.dp, MaterialTheme.Colors.buttonOrange),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = colors.textWhite
                                )
                            ) {
                                AsyncImage(
                                    model = R.drawable.google_logo,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(30.dp)
                                        .padding(end = 8.dp)
                                )

                                Text(
                                    text = stringResource(R.string.sign_in_screen_sign_in_google),
                                    fontFamily = typography.gantari.fontFamily
                                )
                            }
                        } else {
                            WrapButton(
                                onClick = { onEvent(SignInContract.Event.OnLoginClick) },
                                modifier = Modifier
                                    .width(200.dp)
                                    .align(Alignment.CenterHorizontally),
                            ) {
                                Text(
                                    text = stringResource(R.string.sign_in_screen_continue),
                                    fontFamily = typography.gantari.fontFamily
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            WrapButton(
                                onClick = { onEvent(SignInContract.Event.OnSignOutClick) },
                                modifier = Modifier
                                    .width(200.dp)
                                    .align(Alignment.CenterHorizontally),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colors.signOutRed,
                                    contentColor = colors.textWhite
                                )
                            ) {
                                Text(
                                    text = stringResource(R.string.sign_in_screen_sign_out),
                                    fontFamily = typography.gantari.fontFamily
                                )
                            }
                        }
                    }
                }
            }

            Text(
                text = stringResource(R.string.sign_in_screen_footer_text),
                color = colors.textWhite,
                fontFamily = typography.pragatiNarrow.fontFamily,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
                maxLines = 2
            )
        }
    }
}
