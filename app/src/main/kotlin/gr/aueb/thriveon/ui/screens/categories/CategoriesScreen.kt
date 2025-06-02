package gr.aueb.thriveon.ui.screens.categories

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gr.aueb.thriveon.R
import gr.aueb.thriveon.ui.common.components.CustomSnackbarHost
import gr.aueb.thriveon.ui.common.components.ThriveOnCircularProgressIndicator
import gr.aueb.thriveon.ui.common.components.wrap.WrapButton
import gr.aueb.thriveon.ui.common.components.wrap.WrapScaffold
import gr.aueb.thriveon.ui.common.components.wrap.WrapTopAppBar
import gr.aueb.thriveon.ui.screens.categories.model.CategoriesContract
import gr.aueb.thriveon.ui.theme.Colors
import gr.aueb.thriveon.ui.theme.Typography
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    state: CategoriesContract.State,
    onEvent: (CategoriesContract.Event) -> Unit,
    effect: Flow<CategoriesContract.Effect>,
    onNavigateToHome: (String) -> Unit,
    onNavigateBack: () -> Unit,
) {

    val colors = MaterialTheme.Colors
    val typography = MaterialTheme.Typography
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        onEvent(CategoriesContract.Event.Init)
        effect.collect { effect ->
            when (effect) {
                CategoriesContract.Effect.NavigateToNext -> {
                    if (state.isUserPreferencesEmpty) {
                        onNavigateToHome("true")
                    } else {
                        onNavigateBack()
                    }
                }

                CategoriesContract.Effect.NavigateBack -> {
                    onNavigateBack()
                }

                CategoriesContract.Effect.DismissSnackbar -> {
                    snackbarHostState.currentSnackbarData?.dismiss()
                }

                is CategoriesContract.Effect.ShowSnackbar -> {
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

    if (state.isUserPreferencesEmpty) {
        BackHandler(enabled = true) {}
    }

    if (!state.isLoading) {
        WrapScaffold(
            containerColor = colors.backgroundMaroonDark,
            topBar = {
                WrapTopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.categories_screen_title),
                            style = MaterialTheme.typography.titleLarge,
                            color = colors.textWhite,
                            fontFamily = typography.gantari.fontFamily
                        )
                    },
                    navigationIcon = {
                        if (!state.isUserPreferencesEmpty) {
                            IconButton(
                                onClick = { onEvent(CategoriesContract.Event.OnBackClick) },
                                enabled = state.isBackButtonEnabled
                                ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null,
                                    tint = if(state.isBackButtonEnabled) colors.textWhite else colors.textWhite.copy(alpha = 0.4f)
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colors.backgroundMaroonDark
                    )
                )
            },
            snackbarHost = { CustomSnackbarHost(snackbarHostState) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                state.categories.forEach { category ->
                    val isSelected = state.selected.contains(category)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onEvent(
                                    CategoriesContract.Event.OnCategoryToggle(category)
                                )
                            }
                            .padding(vertical = 15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = null,
                            colors = CheckboxDefaults.colors(
                                checkedColor = colors.textOrange,
                                uncheckedColor = colors.textWhite,
                                checkmarkColor = colors.textBlack
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = category,
                            color = if (isSelected) colors.textOrange else colors.textWhite,
                            fontFamily = typography.istokWeb.fontFamily,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                WrapButton(
                    onClick = { onEvent(CategoriesContract.Event.OnSaveClick) },
                    enabled = state.isBackButtonEnabled,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.buttonOrange,
                        contentColor = colors.textBlack
                    )
                ) {
                    Text(
                        text = stringResource(R.string.categories_screen_save),
                        fontFamily = typography.gantari.fontFamily
                    )
                }
            }
        }
    } else {
        WrapScaffold(
            containerColor = colors.backgroundMaroonDark,
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ThriveOnCircularProgressIndicator()
            }
        }
    }
}
