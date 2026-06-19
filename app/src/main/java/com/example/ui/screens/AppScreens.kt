package com.example.ui.screens

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.viewmodel.MainViewModel

@Composable
fun AppNavigationWrapper(viewModel: MainViewModel) {
    Log.d("AppNavigationWrapper", "Rendering AppNavigationWrapper")
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val config by viewModel.rtConfig.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val suratList by viewModel.suratList.collectAsStateWithLifecycle()
    val loggedInRtNumber by viewModel.loggedInRtNumber.collectAsStateWithLifecycle()
    
    val pendingCount = remember(suratList, currentRole, loggedInRtNumber) {
        if (currentRole == "RT") {
            suratList.count { it.status == "PENDING" && it.rtPembuat == loggedInRtNumber }
        } else {
            suratList.count { it.status == "PENDING" }
        }
    }

    val defaultPrimary = MaterialTheme.colorScheme.primary
    val primaryColor = remember(config.primaryColor, defaultPrimary) {
        try {
            if (config.primaryColor.isNotEmpty()) {
                Color(android.graphics.Color.parseColor(config.primaryColor))
            } else {
                defaultPrimary
            }
        } catch (e: Exception) {
            defaultPrimary
        }
    }

    if (!isLoggedIn) {
        LoginScreen(viewModel)
    } else if (currentScreen is MainViewModel.Screen.ProfileSetup) {
        ProfileSetupScreen(viewModel)
    } else {
        androidx.activity.compose.BackHandler(enabled = currentScreen != MainViewModel.Screen.Dashboard) {
            viewModel.navigateBack()
        }
        Scaffold(
            bottomBar = {
                if (currentScreen in listOf(
                    MainViewModel.Screen.Dashboard, 
                    MainViewModel.Screen.WargaList, 
                    MainViewModel.Screen.PelayananSurat, 
                    MainViewModel.Screen.Settings,
                    MainViewModel.Screen.LaporRT,
                    MainViewModel.Screen.Keamanan,
                    MainViewModel.Screen.Pengumuman,
                    MainViewModel.Screen.Gallery,
                    MainViewModel.Screen.OperatorPanel,
                    MainViewModel.Screen.PengaturanUmum,
                    MainViewModel.Screen.RTProfileManagement,
                    MainViewModel.Screen.UserManagement,
                    MainViewModel.Screen.AppCustomization
                )) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
                        windowInsets = WindowInsets.navigationBars
                    ) {
                        NavigationBarItem(
                            selected = currentScreen == MainViewModel.Screen.Dashboard,
                            onClick = { 
                                if (currentScreen != MainViewModel.Screen.Dashboard) {
                                    viewModel.navigateTo(MainViewModel.Screen.Dashboard)
                                }
                            },
                            icon = { 
                                Box(
                                    modifier = if (currentScreen == MainViewModel.Screen.Dashboard) 
                                        Modifier.border(1.5.dp, primaryColor, RoundedCornerShape(8.dp)).padding(4.dp)
                                    else Modifier
                                ) {
                                    Icon(Icons.Default.Home, "Beranda") 
                                }
                            },
                            label = { 
                                Text(
                                    "Home", 
                                    fontSize = 11.sp, 
                                    fontWeight = if (currentScreen == MainViewModel.Screen.Dashboard) FontWeight.Bold else FontWeight.Medium,
                                    color = if (currentScreen == MainViewModel.Screen.Dashboard) primaryColor else Color(0xFF1E293B)
                                ) 
                            }
                        )
                        NavigationBarItem(
                            selected = currentScreen == MainViewModel.Screen.WargaList,
                            onClick = { 
                                if (currentScreen != MainViewModel.Screen.WargaList) {
                                    viewModel.navigateTo(MainViewModel.Screen.WargaList)
                                }
                            },
                            icon = { 
                                Box(
                                    modifier = if (currentScreen == MainViewModel.Screen.WargaList) 
                                        Modifier.border(1.5.dp, primaryColor, RoundedCornerShape(8.dp)).padding(4.dp)
                                    else Modifier
                                ) {
                                    Icon(Icons.Default.People, "Warga") 
                                }
                            },
                            label = { 
                                Text(
                                    "Warga", 
                                    fontSize = 11.sp, 
                                    fontWeight = if (currentScreen == MainViewModel.Screen.WargaList) FontWeight.Bold else FontWeight.Medium,
                                    color = if (currentScreen == MainViewModel.Screen.WargaList) primaryColor else Color(0xFF1E293B)
                                ) 
                            }
                        )
                        NavigationBarItem(
                            selected = currentScreen == MainViewModel.Screen.PelayananSurat,
                            onClick = { 
                                if (currentScreen != MainViewModel.Screen.PelayananSurat) {
                                    viewModel.navigateTo(MainViewModel.Screen.PelayananSurat)
                                }
                            },
                            icon = { 
                                Box(
                                    modifier = if (currentScreen == MainViewModel.Screen.PelayananSurat) 
                                        Modifier.border(1.5.dp, primaryColor, RoundedCornerShape(8.dp)).padding(4.dp)
                                    else Modifier
                                ) {
                                    if (pendingCount > 0 && currentRole != "WARGA") {
                                        BadgedBox(badge = { Badge { Text(pendingCount.toString()) } }) {
                                            Icon(Icons.Default.Description, "Surat")
                                        }
                                    } else {
                                        Icon(Icons.Default.Description, "Surat")
                                    }
                                }
                            },
                            label = { 
                                Text(
                                    "Surat", 
                                    fontSize = 11.sp, 
                                    fontWeight = if (currentScreen == MainViewModel.Screen.PelayananSurat) FontWeight.Bold else FontWeight.Medium,
                                    color = if (currentScreen == MainViewModel.Screen.PelayananSurat) primaryColor else Color(0xFF1E293B)
                                ) 
                            }
                        )
                        NavigationBarItem(
                            selected = currentScreen == MainViewModel.Screen.Gallery,
                            onClick = { 
                                if (currentScreen != MainViewModel.Screen.Gallery) {
                                    viewModel.navigateTo(MainViewModel.Screen.Gallery)
                                }
                            },
                            icon = { 
                                Box(
                                    modifier = if (currentScreen == MainViewModel.Screen.Gallery) 
                                        Modifier.border(1.5.dp, primaryColor, RoundedCornerShape(8.dp)).padding(4.dp)
                                    else Modifier
                                ) {
                                    Icon(Icons.Default.Collections, "Galery") 
                                }
                            },
                            label = { 
                                Text(
                                    "Galery", 
                                    fontSize = 11.sp, 
                                    fontWeight = if (currentScreen == MainViewModel.Screen.Gallery) FontWeight.Bold else FontWeight.Medium,
                                    color = if (currentScreen == MainViewModel.Screen.Gallery) primaryColor else Color(0xFF1E293B)
                                ) 
                            }
                        )
                        if (currentRole == "SUPERADMIN" || currentRole == "OPERATOR" || currentRole == "RT") {
                            NavigationBarItem(
                                selected = currentScreen == MainViewModel.Screen.OperatorPanel,
                                onClick = { 
                                    if (currentScreen != MainViewModel.Screen.OperatorPanel) {
                                        viewModel.navigateTo(MainViewModel.Screen.OperatorPanel)
                                    }
                                },
                                icon = { 
                                    Box(
                                        modifier = if (currentScreen == MainViewModel.Screen.OperatorPanel) 
                                            Modifier.border(1.5.dp, primaryColor, RoundedCornerShape(8.dp)).padding(4.dp)
                                        else Modifier
                                    ) {
                                        Icon(Icons.Default.DisplaySettings, "Panel")
                                    }
                                },
                                label = { 
                                    Text(
                                        "Panel", 
                                        fontSize = 11.sp, 
                                        fontWeight = if (currentScreen == MainViewModel.Screen.OperatorPanel) FontWeight.Bold else FontWeight.Medium,
                                        color = if (currentScreen == MainViewModel.Screen.OperatorPanel) primaryColor else Color(0xFF1E293B)
                                    ) 
                                }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (val screen = currentScreen) {
                    is MainViewModel.Screen.Dashboard -> {
                        DashboardScreen(viewModel)
                    }
                    is MainViewModel.Screen.WargaList -> WargaListScreen(viewModel)
                    is MainViewModel.Screen.PelayananSurat -> PelayananSuratScreen(viewModel)
                    is MainViewModel.Screen.Settings -> SettingsScreen(viewModel)
                    is MainViewModel.Screen.AddWarga -> AddWargaScreen(viewModel)
                    is MainViewModel.Screen.CreateLetter -> CreateLetterScreen(viewModel, screen.predefinedWargaNik)
                    is MainViewModel.Screen.LetterDetail -> LetterDetailScreen(viewModel, screen.letterId)
                    is MainViewModel.Screen.AddActivity -> AddActivityScreen(viewModel)
                    is MainViewModel.Screen.LaporRT -> {
                        if (currentRole == "WARGA") {
                            LaporScreen(viewModel)
                        } else {
                            ReportLifecyclePanel(viewModel)
                        }
                    }
                    is MainViewModel.Screen.Keamanan -> KeamananScreen(viewModel)
                    is MainViewModel.Screen.Pengumuman -> PengumumanScreen(viewModel)
                    is MainViewModel.Screen.Gallery -> GalleryScreen(viewModel)
                    is MainViewModel.Screen.OperatorPanel -> OperatorPanelScreen(viewModel)
                    is MainViewModel.Screen.PengaturanUmum -> PengaturanUmumScreen(viewModel)
                    is MainViewModel.Screen.RTProfileManagement -> RTProfileManagementScreen(viewModel)
                    is MainViewModel.Screen.EditRtProfile -> EditRtProfileScreen(viewModel, screen.rtNum)
                    is MainViewModel.Screen.UserManagement -> UserManagementScreen(viewModel)
                    is MainViewModel.Screen.AppCustomization -> AppCustomizationScreen(viewModel)
                    is MainViewModel.Screen.LaporMandiri -> LaporScreen(viewModel)
                    is MainViewModel.Screen.ProfileSetup -> ProfileSetupScreen(viewModel)
                }
            }
        }
    }
}
