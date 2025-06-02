package gr.aueb.thriveon.ui.screens.editProfile

import android.graphics.Color
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.yalantis.ucrop.UCrop
import gr.aueb.thriveon.R
import gr.aueb.thriveon.ui.common.components.CustomSnackbarHost
import gr.aueb.thriveon.ui.common.components.ThriveOnCircularProgressIndicator
import gr.aueb.thriveon.ui.common.components.spacers.VSpacer
import gr.aueb.thriveon.ui.common.components.wrap.*
import gr.aueb.thriveon.ui.screens.editProfile.model.EditProfileContract
import gr.aueb.thriveon.ui.theme.Colors
import gr.aueb.thriveon.ui.theme.Typography
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    state: EditProfileContract.State,
    onEvent: (EditProfileContract.Event) -> Unit,
    effect: Flow<EditProfileContract.Effect>,
    onNavigateBack: () -> Unit,
    onNavigateToCategories: () -> Unit,
    onLogout: () -> Unit,
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val cropImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val resultUri = UCrop.getOutput(result.data ?: return@rememberLauncherForActivityResult)
        resultUri?.let {
            onEvent(EditProfileContract.Event.OnProfileImageSelected(it))
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { sourceUri ->
            val destinationUri =
                Uri.fromFile(File(context.cacheDir, "cropped_${System.currentTimeMillis()}.jpg"))

            val options = UCrop.Options().apply {
                setCircleDimmedLayer(true)
                setShowCropFrame(false)
                setShowCropGrid(false)
                setHideBottomControls(false)
                setFreeStyleCropEnabled(false)
                setToolbarColor(Color.BLACK)
                setStatusBarColor(Color.BLACK)
                setToolbarWidgetColor(Color.WHITE)
            }

            val cropIntent = UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(1f, 1f)
                .withMaxResultSize(512, 512)
                .withOptions(options)
                .getIntent(context)

            cropImageLauncher.launch(cropIntent)
        }
    }

    LaunchedEffect(Unit) {
        onEvent(EditProfileContract.Event.Init)

        effect.collect { effect ->
            when (effect) {
                EditProfileContract.Effect.NavigateBack -> onNavigateBack()
                EditProfileContract.Effect.NavigateToCategories -> onNavigateToCategories()

                EditProfileContract.Effect.Logout -> onLogout()
                EditProfileContract.Effect.SnackbarEffect.DismissSnackbar -> {
                    snackbarHostState.currentSnackbarData?.dismiss()
                }

                is EditProfileContract.Effect.SnackbarEffect.ShowSnackbar -> {
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
        containerColor = MaterialTheme.Colors.backgroundMaroonDark,
        snackbarHost = { CustomSnackbarHost(snackbarHostState) },
        topBar = {
            WrapTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.edit_profile),
                        fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                        fontSize = 30.sp,
                        color = MaterialTheme.Colors.textOrange
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { onEvent(EditProfileContract.Event.OnBackClick) },
                        enabled = !state.isLoading
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null,
                            tint = if (!state.isLoading) MaterialTheme.Colors.textWhite else MaterialTheme.Colors.textWhite.copy(
                                alpha = 0.5f
                            )
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { onEvent(EditProfileContract.Event.OnLogoutClick) },
                        enabled = !state.isLoading
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.logout),
                            contentDescription = stringResource(R.string.logout),
                            tint = if (!state.isLoading) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.error.copy(
                                alpha = 0.5f
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.Colors.backgroundMaroonDark
                )
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                ThriveOnCircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.Colors.textOrange,
                                shape = CircleShape
                            )
                    ) {
                        AsyncImage(
                            model = state.profileImageUri ?: state.profilePictureUrl,
                            contentDescription = stringResource(R.string.profile_image_description),
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .offset(x = (-2).dp, y = (-2).dp)
                            .size(28.dp)
                            .background(
                                color = MaterialTheme.Colors.backgroundDark.copy(alpha = 0.8f),
                                shape = CircleShape
                            )
                            .border(
                                width = 0.5.dp,
                                color = MaterialTheme.Colors.textOrange,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.edit_profile),
                            tint = MaterialTheme.Colors.textOrange,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                VSpacer.Small()

                val usernameMinLength = 4
                val usernameMaxLength = 20
                Column(modifier = Modifier.fillMaxWidth()) {
                    WrapOutlinedTextField(
                        value = state.username,
                        onValueChange = {
                            if (it.length <= usernameMaxLength) {
                                onEvent(EditProfileContract.Event.OnUsernameChange(it))
                            }
                        },
                        label = {
                            Text(
                                stringResource(R.string.username),
                                fontFamily = MaterialTheme.Typography.gantari.fontFamily
                            )
                        },
                        isError = state.username.length < usernameMinLength,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 16.sp,
                            fontFamily = MaterialTheme.Typography.gantari.fontFamily
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = stringResource(
                            R.string.bio_characters_used,
                            state.username.length,
                            usernameMaxLength
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (state.username.length >= usernameMaxLength)
                            MaterialTheme.Colors.textOrange
                        else
                            MaterialTheme.Colors.textWhite,
                        fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(end = 4.dp)
                    )
                }

                val maxBioChars = 50
                Column(modifier = Modifier.fillMaxWidth()) {
                    WrapOutlinedTextField(
                        value = state.bio,
                        onValueChange = {
                            if (it.length <= maxBioChars) {
                                onEvent(EditProfileContract.Event.OnBioChange(it))
                            }
                        },
                        singleLine = false,
                        label = {
                            Text(
                                stringResource(R.string.bio),
                                fontFamily = MaterialTheme.Typography.gantari.fontFamily
                            )
                        },
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 16.sp,
                            fontFamily = MaterialTheme.Typography.gantari.fontFamily
                        ),
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = stringResource(
                            R.string.bio_characters_used,
                            state.bio.length,
                            maxBioChars
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (state.bio.length >= maxBioChars)
                            MaterialTheme.Colors.textOrange
                        else
                            MaterialTheme.Colors.textWhite,
                        fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                        modifier = Modifier.align(Alignment.End)
                    )
                }

                if (state.titles.isNotEmpty()) {
                    var expanded by remember { mutableStateOf(false) }

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        WrapOutlinedTextField(
                            value = state.equippedTitle.ifBlank { stringResource(R.string.select_title) },
                            onValueChange = {},
                            readOnly = true,
                            label = {
                                Text(
                                    text = stringResource(R.string.equipped_title),
                                    fontFamily = MaterialTheme.Typography.gantari.fontFamily
                                )
                            },
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 16.sp,
                                fontFamily = MaterialTheme.Typography.gantari.fontFamily
                            ),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryEditable, enabled = true)
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier
                                .background(MaterialTheme.Colors.backgroundMaroonLight)
                        ) {
                            state.titles.forEach { title ->
                                DropdownMenuItem(
                                    modifier = Modifier.background(MaterialTheme.Colors.backgroundMaroonLight),
                                    text = {
                                        Text(
                                            text = title,
                                            fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                                            color = MaterialTheme.Colors.textOrange
                                        )
                                    },
                                    onClick = {
                                        expanded = false
                                        onEvent(
                                            EditProfileContract.Event.OnEquippedTitleChange(
                                                title
                                            )
                                        )
                                    },
                                    colors = MenuDefaults.itemColors(
                                        textColor = MaterialTheme.Colors.textOrange
                                    )
                                )
                            }
                        }
                    }
                } else {
                    WrapOutlinedTextField(
                        value = stringResource(R.string.no_titles_unlocked),
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        label = {
                            Text(
                                text = stringResource(R.string.equipped_title),
                                fontFamily = MaterialTheme.Typography.gantari.fontFamily
                            )
                        },
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 16.sp,
                            fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                            color = MaterialTheme.Colors.textWhite
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.Colors.textOrange,
                            unfocusedBorderColor = MaterialTheme.Colors.textOrange,
                            disabledBorderColor = MaterialTheme.Colors.textOrange,
                            disabledTextColor = MaterialTheme.Colors.textWhite,
                            disabledLabelColor = MaterialTheme.Colors.textWhite.copy(alpha = 0.7f),
                            disabledTrailingIconColor = MaterialTheme.Colors.textWhite,
                            cursorColor = MaterialTheme.Colors.textOrange
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                VSpacer.Medium()

                WrapTextButton(
                    onClick = { onEvent(EditProfileContract.Event.OnEditCategoriesClick) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.edit_categories),
                        fontSize = 18.sp,
                        color = MaterialTheme.Colors.textOrange,
                        fontFamily = MaterialTheme.Typography.pragatiNarrow.fontFamily
                    )
                }

                VSpacer.Small()

                WrapButton(
                    onClick = { onEvent(EditProfileContract.Event.OnSaveClick) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.isUsernameValid
                ) {
                    Text(
                        text = stringResource(R.string.save_changes),
                        fontSize = 18.sp,
                        color = MaterialTheme.Colors.textBlack,
                        fontFamily = MaterialTheme.Typography.pragatiNarrow.fontFamily
                    )
                }

                if (state.isLogoutDialogAlertVisible) {
                    WrapAlertDialog(
                        onDismissRequest = {
                            onEvent(EditProfileContract.Event.LogoutAlertDialogEvent.Dismiss)
                        },
                        containerColor = MaterialTheme.Colors.backgroundMaroonLight,
                        title = {
                            Text(
                                text = stringResource(R.string.logout),
                                fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                                color = MaterialTheme.Colors.textWhite
                            )
                        },
                        text = {
                            Text(
                                text = stringResource(R.string.logout_confirmation),
                                fontFamily = MaterialTheme.Typography.gantari.fontFamily,
                                color = MaterialTheme.Colors.textWhite
                            )
                        },
                        confirmButton = {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { onEvent(EditProfileContract.Event.LogoutAlertDialogEvent.Cancel) },
                                    modifier = Modifier.weight(1f),
                                    border = BorderStroke(1.dp, MaterialTheme.Colors.buttonOrange),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.Colors.buttonOrange
                                    )
                                ) {
                                    Text(
                                        text = stringResource(R.string.cancel),
                                        fontFamily = MaterialTheme.Typography.pragatiNarrow.fontFamily,
                                        fontSize = 18.sp
                                    )
                                }

                                Button(
                                    onClick = { onEvent(EditProfileContract.Event.LogoutAlertDialogEvent.Confirm) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.Colors.buttonOrange
                                    )
                                ) {
                                    Text(
                                        text = stringResource(R.string.confirm),
                                        fontFamily = MaterialTheme.Typography.pragatiNarrow.fontFamily,
                                        color = MaterialTheme.Colors.textWhite,
                                        fontSize = 18.sp
                                    )
                                }
                            }
                        },
                        dismissButton = {}
                    )
                }
            }
        }
    }
}
