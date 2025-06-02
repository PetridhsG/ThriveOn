package gr.aueb.thriveon.ui.common.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import gr.aueb.thriveon.R
import gr.aueb.thriveon.ui.theme.Colors
import gr.aueb.thriveon.ui.theme.Typography
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun BottomNavBar(
    selected: BottomNavDestination,
    onNavigate: (BottomNavDestination) -> Unit,
    showProfileAsSelected: Boolean = false,
) {
    val colors = MaterialTheme.Colors
    val typography = MaterialTheme.Typography

    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
        color = colors.backgroundMaroonLight,
        tonalElevation = 3.dp
    ) {
        NavigationBar(
            containerColor = colors.backgroundMaroonLight
        ) {
            BottomNavDestination.entries.forEach { destination ->
                val isSelected = if (destination == BottomNavDestination.Profile)
                    showProfileAsSelected else destination == selected

                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        if (!isSelected) {
                            onNavigate(destination)
                        }
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = destination.iconResId),
                            contentDescription = stringResource(id = destination.labelResId),
                            modifier = Modifier.size(28.dp),
                            tint = if (isSelected) colors.textOrange else colors.navigationIconGray
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(id = destination.labelResId),
                            color = if (isSelected) colors.textOrange else colors.navigationIconGray,
                            fontFamily = typography.istokWeb.fontFamily,
                            fontSize = 10.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = colors.backgroundMaroonLight
                    )
                )
            }
        }
    }
}

enum class BottomNavDestination(@StringRes val labelResId: Int, val iconResId: Int) {
    Home(R.string.nav_home, R.drawable.target),
    Feed(R.string.nav_feed, R.drawable.users_group),
    Search(R.string.nav_search, R.drawable.search),
    Notifications(R.string.nav_notifications, R.drawable.notifications),
    Profile(R.string.nav_profile, R.drawable.user)
}
