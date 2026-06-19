package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.ui.components.*
import com.example.viewmodel.MainViewModel

import android.net.Uri
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

@Composable
fun LaporScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val loggedInWargaName by viewModel.loggedInWargaName.collectAsStateWithLifecycle()
    val loggedInNik by viewModel.loggedInNik.collectAsStateWithLifecycle()
    val loggedInRt by viewModel.loggedInRtNumber.collectAsStateWithLifecycle()
    
    var inputJudul by remember { mutableStateOf("") }
    var inputDeskripsi by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<String?>(null) }
    var coords by remember { mutableStateOf("") }

    val pageTitle = if (currentRole == "RT") "Lapor Kelurahan" else "Lapor RT"
    val hintText = if (currentRole == "RT") "Laporkan kendala wilayah RT Anda ke pusat Kelurahan disertai bukti foto." else "Laporkan kejadian atau keluhan di lingkungan Anda disertai foto ber-metatag."

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text(pageTitle, fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()).imePadding()) {
            Text(hintText, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            
            Spacer(modifier = Modifier.height(20.dp))
            
            OutlinedTextField(
                value = inputJudul,
                onValueChange = { inputJudul = it },
                label = { Text("Subjek Laporan") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = inputDeskripsi,
                onValueChange = { inputDeskripsi = it },
                label = { Text("Detail Laporan") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                shape = RoundedCornerShape(12.dp)
            )
            
            Spacer(modifier = Modifier.height(20.dp))

            Text("Bukti Dokumentasi", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            
            AssetUploadCard(
                title = "Foto Kejadian",
                currentImage = photoUri,
                onImageCaptured = { uri ->
                    photoUri = uri
                    // Simulate automated metadata burning for government tracking
                    coords = "-3.4${(100..999).random()}, 114.8${(100..999).random()}"
                    Toast.makeText(context, "Metadata Lokasi Tersemat Otomatis!", Toast.LENGTH_SHORT).show()
                }
            )

            if (coords.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, tint = Color.Red, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Lokasi Terdeteksi: $coords", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(30.dp))
            
            Button(
                onClick = {
                    if (inputJudul.isNotBlank()) {
                         viewModel.saveCitizenReport(
                            title = inputJudul,
                            content = inputDeskripsi,
                            nik = loggedInNik ?: "",
                            name = loggedInWargaName ?: "Warga",
                            photoPath = photoUri ?: "",
                            coords = coords
                        )
                        val successMsg = if (currentRole == "RT") "Laporan terkirim ke Kelurahan!" else "Laporan berhasil terkirim ke RT!"
                        Toast.makeText(context, successMsg, Toast.LENGTH_SHORT).show()
                        viewModel.navigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
            ) {
                Icon(Icons.Default.Send, null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Kirim Laporan Resmi", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
fun KeamananScreen(viewModel: MainViewModel) {
    val config by viewModel.rtConfig.collectAsStateWithLifecycle()
    
    val contacts = listOf(
        "Polisi" to (config.emergencyContact1.ifEmpty { "110" }),
        "Damkar" to (config.emergencyContact2.ifEmpty { "113" }),
        "Ambulans" to (config.emergencyContact3.ifEmpty { "118" }),
        "SAR" to (config.emergencyContact4.ifEmpty { "115" }),
        "PLN" to (config.emergencyContact5.ifEmpty { "123" })
    )

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Pusat Keamanan & Darurat", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Kontak Darurat Kelurahan Batulicin", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            
            contacts.forEach { (label, number) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(label, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                            Text(number, fontSize = 16.sp, color = Color(0xFF2563EB), fontWeight = FontWeight.Black)
                        }
                        IconButton(
                            onClick = { /* Call intent */ },
                            colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFFDCFCE7))
                        ) {
                            Icon(Icons.Default.Phone, null, tint = Color(0xFF166534))
                        }
                    }
                }
            }
            
            Spacer(Modifier.height(20.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                border = BorderStroke(1.dp, Color(0xFFFEE2E2))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, null, tint = Color(0xFFEF4444), modifier = Modifier.size(32.dp))
                    Spacer(Modifier.width(16.dp))
                    Text(
                        "Gunakan nomor di atas hanya untuk keadaan darurat yang membutuhkan penanganan segera.",
                        fontSize = 12.sp,
                        color = Color(0xFF991B1B)
                    )
                }
            }
        }
    }
}

@Composable
fun PengumumanScreen(viewModel: MainViewModel) {
    val announcements by viewModel.announcementList.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Pusat Pengumuman", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (announcements.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Belum ada pengumuman hari ini", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(announcements.sortedByDescending { it.id }) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = Color(0xFFDBEAFE),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        item.author,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E40AF)
                                    )
                                }
                                Spacer(Modifier.width(8.dp))
                                Text(item.date, fontSize = 10.sp, color = Color.Gray)
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(item.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(item.content, fontSize = 13.sp, color = Color.DarkGray)
                        }
                    }
                }
            }
        }
    }
}
