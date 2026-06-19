package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.SettingsCategory
import com.example.ui.components.SettingsMenuButton
import com.example.viewmodel.MainViewModel

@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val config by viewModel.rtConfig.collectAsStateWithLifecycle()
    val profile by viewModel.currentRtProfile.collectAsStateWithLifecycle()
    val reports by viewModel.reportList.collectAsStateWithLifecycle()
    val announcements by viewModel.announcementList.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
            .verticalScroll(rememberScrollState())
            .imePadding()
    ) {
        // App Bar Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateBack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = Color(0xFF1E293B))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Konfigurasi Layanan RT",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = "Manajemen identitas, branding, dan data otoritas.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF475569)
                )
            }
        }

        if (currentRole == "SUPERADMIN") {
            SettingsCategory(title = "SUPERADMIN MENU") {
                SettingsMenuButton(
                    icon = Icons.Default.SettingsSuggest,
                    title = "Pengaturan Umum (Sistem)",
                    subtitle = "Logo, Warna, Format Surat, dsb.",
                    color = Color(0xFF3B82F6)
                ) {
                    viewModel.navigateTo(MainViewModel.Screen.PengaturanUmum)
                }
                SettingsMenuButton(
                    icon = Icons.Default.Groups,
                    title = "Data Profil RT",
                    subtitle = "CRUD Nama, WhatsApp, TTD, Stempel, & Lokasi.",
                    color = Color(0xFF10B981)
                ) {
                    viewModel.navigateTo(MainViewModel.Screen.RTProfileManagement)
                }
                SettingsMenuButton(
                    icon = Icons.Default.PersonSearch,
                    title = "Akun Pengguna",
                    subtitle = "Kelola kredensial login aplikasi.",
                    color = Color(0xFF8B5CF6)
                ) {
                    viewModel.navigateTo(MainViewModel.Screen.UserManagement)
                }
            }

            SettingsCategory(title = "HUBUNGAN WARGA & DATA") {
                SettingsMenuButton(
                    icon = Icons.Default.Campaign, 
                    title = "Pengumuman Global", 
                    subtitle = "${announcements.size} Pengumuman aktif.", 
                    color = Color(0xFFF59E0B)
                ) {
                    viewModel.navigateTo(MainViewModel.Screen.Pengumuman)
                }
                SettingsMenuButton(
                    icon = Icons.Default.HistoryEdu, 
                    title = "Laporan Warga Kelurahan", 
                    subtitle = "${reports.size} Total laporan masuk.", 
                    color = Color(0xFFEF4444)
                ) {
                    viewModel.navigateTo(MainViewModel.Screen.LaporRT)
                }
                SettingsMenuButton(
                    icon = Icons.Default.TableChart, 
                    title = "Import Data Warga (Excel)", 
                    subtitle = "Batch import database kependudukan.", 
                    color = Color(0xFF10B981)
                ) {
                    Toast.makeText(context, "Fitur Import Segera Hadir!", Toast.LENGTH_SHORT).show()
                }
            }
        } else if (currentRole == "RT") {
            SettingsCategory(title = "PROFIL RT") {
                Text("RT memiliki akses terbatas ke konfigurasi sistem.", fontSize = 12.sp, color = Color(0xFF64748B))
                Button(
                    onClick = { 
                        profile?.let { 
                            viewModel.navigateTo(MainViewModel.Screen.EditRtProfile(it.rtNumber)) 
                        } ?: viewModel.navigateTo(MainViewModel.Screen.ProfileSetup)
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Kelola Profil & Berkas RT")
                }
            }
        }
        
        SettingsCategory(title = "AKUN SAYA") {
             SettingsMenuButton(
                icon = Icons.Default.Logout,
                title = "Keluar Sesi",
                subtitle = "Akhiri sesi login saat ini.",
                color = Color(0xFF64748B)
            ) {
                viewModel.performLogout()
            }
        }
        
        Spacer(Modifier.height(40.dp))
    }
}
