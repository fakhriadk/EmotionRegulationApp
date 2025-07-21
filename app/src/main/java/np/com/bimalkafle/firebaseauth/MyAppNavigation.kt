// file: MyAppNavigation.kt
package np.com.bimalkafle.firebaseauth

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import np.com.bimalkafle.firebaseauth.pages.*
import np.com.bimalkafle.firebaseauth.ui.theme.ChatViewModel
import np.com.bimalkafle.firebaseauth.ui.theme.ChatViewModelFactory

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun MyAppNavigation(authViewModel: AuthViewModel) { // Hapus parameter chatViewModel dan activityContext yang tidak perlu
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    var showPremiumDialog by remember { mutableStateOf(false) }

    val bottomNavItems = listOf(
        BottomNavItem("home", "Home", Icons.Default.Home),
        BottomNavItem("chatbot", "Chat", Icons.Default.Message),
        BottomNavItem("statistics", "Stats", Icons.Default.Assessment),
        BottomNavItem("profile", "Profile", Icons.Default.Person)
    )

    val routesWithBottomNav = bottomNavItems.map { it.route }

    if (showPremiumDialog) {
        PremiumDialog(
            onDismiss = { showPremiumDialog = false },
            navController = navController
        )
    }

    Scaffold(
        bottomBar = {
            if (currentRoute in routesWithBottomNav) {
                BottomNavigationBar(
                    navController = navController,
                    items = bottomNavItems,
                    onStatsClick = {
                        if (UserStatus.isPremium) {
                            navController.navigate("statistics")
                        } else {
                            showPremiumDialog = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") {
                LoginPage(navController = navController, authViewModel = authViewModel)
            }
            composable("signup") {
                SignupPage(navController = navController, authViewModel = authViewModel)
            }
            composable("home") {
                HomePage(
                    navController = navController,
                    authViewModel = authViewModel
                )
            }
            composable("chatbot") {
                val chatViewModel: ChatViewModel = viewModel(
                    factory = ChatViewModelFactory(authViewModel)
                )
                ChatPage(chatViewModel = chatViewModel)
            }
            composable("statistics") {
                val statisticsViewModel: StatisticsViewModel = viewModel(
                    factory = StatisticsViewModelFactory(authViewModel)
                )
                StatisticsPage(navController = navController, statisticsViewModel = statisticsViewModel)
            }
            composable("profile") {
                // ProfilePage juga tidak perlu activityContext jika kita tidak pakai recreate()
                ProfilePage(
                    navController = navController,
                    authViewModel = authViewModel
                )

            }
            composable("breathing") {
                BreathingPage(navController = navController)
            }
            composable("journal") {
                val journalViewModel: JournalViewModel = viewModel(
                    factory = JournalViewModelFactory(authViewModel)
                )
                JournalPage(navController = navController, journalViewModel = journalViewModel)
            }
            composable("privacy_policy") {
                PrivacyPolicyScreen(navController = navController)
            }
            composable("premium") {
                PremiumPage(navController = navController)
            }
            composable("community") {
                CommunityPage(navController = navController)
            }
        }
    }
}



@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    items: List<BottomNavItem>,
    onStatsClick: () -> Unit // Parameter baru untuk menangani klik statistik
) {
    val containerColor = MaterialTheme.colorScheme.surface
    val contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    val selectedContentColor = MaterialTheme.colorScheme.primary

    Surface(
        tonalElevation = 8.dp,
        shadowElevation = 12.dp,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        color = containerColor,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
    ) {
        NavigationBar(
            containerColor = containerColor,
            contentColor = contentColor,
            tonalElevation = 0.dp,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp)
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            items.forEach { item ->
                val selected = currentRoute == item.route

                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = if (selected) selectedContentColor else contentColor
                        )
                    },
                    selected = selected,
                    onClick = {
                        // --- LOGIKA ONCLICK YANG SUDAH DIPERBARUI ---
                        if (item.route == "statistics") {
                            onStatsClick() // Panggil lambda khusus
                        } else {
                            // Navigasi normal untuk item lain
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    alwaysShowLabel = false,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = selectedContentColor,
                        unselectedIconColor = contentColor,
                        indicatorColor = selectedContentColor.copy(alpha = 0.12f)
                    )
                )
            }
        }
    }
}