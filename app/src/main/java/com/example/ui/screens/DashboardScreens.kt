package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.ui.components.*
import com.example.viewmodel.MainViewModel
import android.content.Intent
import android.net.Uri

@Composable
fun DashboardScreen(viewModel: MainViewModel) {
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val loggedInWargaName by viewModel.loggedInWargaName.collectAsStateWithLifecycle()
    val loggedInNik by viewModel.loggedInNik.collectAsStateWithLifecycle()
    val loggedInRtNumber by viewModel.loggedInRtNumber.collectAsStateWithLifecycle()
    val announcements by viewModel.announcementList.collectAsStateWithLifecycle()
    val rtProfiles by viewModel.allRtProfiles.collectAsStateWithLifecycle()
    val config by viewModel.rtConfig.collectAsStateWithLifecycle()
    val profile = remember(rtProfiles, loggedInRtNumber) { rtProfiles.find { it.rtNumber == loggedInRtNumber } }
    
    val listSurat by viewModel.suratList.collectAsStateWithLifecycle()
    val listReport by viewModel.reportList.collectAsStateWithLifecycle()
    
    val pendingSuratCount = remember(listSurat, currentRole, loggedInRtNumber) {
        if (currentRole == "RT") {
            listSurat.count { it.status == "PENDING" && it.rtPembuat == loggedInRtNumber }
        } else {
            listSurat.count { it.status == "PENDING" }
        }
    }
    
    val pendingReportCount = remember(listReport) {
        listReport.count { it.status.uppercase() == "PENDING" || it.status.uppercase() == "DI PROSES" }
    }
    
    val context = LocalContext.current
    val primaryColor = try {
        if (config.primaryColor.isNotEmpty()) Color(android.graphics.Color.parseColor(config.primaryColor))
        else Color(0xFF0F172A)
    } catch (e: Exception) { Color(0xFF0F172A) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // DASHBOARD HEADER
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                ) {
                    // Modern Gradient Background
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .drawBehind {
                                drawRect(
                                    Brush.verticalGradient(
                                        listOf(primaryColor, primaryColor.copy(alpha = 0.8f))
                                    )
                                )
                            }
                    )
                    
                    // Batulicin Monument Visual
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        BatulicinMonumentGraphic(
                            modifier = Modifier
                                .size(300.dp)
                                .alpha(0.15f)
                                .graphicsLayer(translationX = 100f)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "DIGIBAT-RT",
                                    fontWeight = FontWeight.Black,
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 12.sp,
                                    letterSpacing = 2.sp
                                )
                                Text(
                                    "Halo, ${loggedInWargaName?.split(" ")?.firstOrNull() ?: "User"}",
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    lineHeight = 28.sp
                                )
                                Text(
                                    "Akses: $currentRole",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 12.sp
                                )
                            }
                            
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color.White.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (profile?.fotoRt?.isNotEmpty() == true) {
                                    AsyncImage(
                                        model = profile.fotoRt,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(Icons.Default.Notifications, null, tint = Color.White)
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Action Card in Header
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Pelayanan Mandiri RT ${loggedInRtNumber ?: ".."}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color(0xFF0F172A)
                                    )
                                    Text(
                                        "Kelurahan Batulicin, Kalimantan Selatan",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }
                                
                                Button(
                                    onClick = { viewModel.navigateTo(MainViewModel.Screen.PelayananSurat) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text("Buka Layanan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            item {
                if (pendingSuratCount > 0 || pendingReportCount > 0) {
                    Card(
                        modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                        border = BorderStroke(1.dp, Color(0xFFFCA5A5))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.NotificationsActive, "Alert", tint = Color(0xFFEF4444), modifier = Modifier.size(28.dp))
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("Pemberitahuan Sistem", fontWeight = FontWeight.Black, fontSize = 14.sp, color = Color(0xFF991B1B))
                                val letterText = if (pendingSuratCount > 0) "$pendingSuratCount Pengajuan Surat" else ""
                                val reportText = if (pendingReportCount > 0) "$pendingReportCount Laporan Warga" else ""
                                val andText = if (pendingSuratCount > 0 && pendingReportCount > 0) " dan " else ""
                                Text(
                                    "Ada $letterText$andText$reportText baru yang memerlukan tindak lanjut Anda.",
                                    fontSize = 12.sp,
                                    color = Color(0xFF7F1D1D)
                                )
                            }
                        }
                    }
                }
            }

            item {
                // ANNOUNCEMENT SECTION
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Pengumuman Terbaru",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = Color(0xFF0F172A)
                            )
                            if (announcements.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFEF4444), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("BARU", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        TextButton(onClick = { viewModel.navigateTo(MainViewModel.Screen.Pengumuman) }) {
                            Text("Lihat Semua", color = primaryColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                    
                    if (announcements.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth().height(120.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Tidak ada pengumuman hari ini", color = Color.Gray, fontSize = 13.sp)
                            }
                        }
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(bottom = 8.dp)
                        ) {
                            items(announcements.take(5)) { item ->
                                Card(
                                    modifier = Modifier.width(280.dp).clickable { },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Surface(
                                            color = primaryColor.copy(alpha = 0.1f),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                item.author,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = primaryColor
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            item.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            item.content,
                                            fontSize = 11.sp,
                                            color = Color.Gray,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                // FEATURE GRID
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        "Layanan Administrasi",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        FeatureIconModern("Data Warga", Icons.Default.Groups, Color(0xFF3B82F6), Modifier.weight(1f)) {
                            viewModel.navigateTo(MainViewModel.Screen.WargaList)
                        }
                        FeatureIconModern(
                            label = "Buat Surat",
                            icon = Icons.Default.Description,
                            color = Color(0xFF10B981),
                            modifier = Modifier.weight(1f),
                            badgeCount = pendingSuratCount
                        ) {
                            viewModel.navigateTo(MainViewModel.Screen.CreateLetter())
                        }
                        FeatureIconModern(
                            label = "Lapor RT",
                            icon = Icons.Default.Flag,
                            color = Color(0xFFF59E0B),
                            modifier = Modifier.weight(1f),
                            badgeCount = pendingReportCount
                        ) {
                            viewModel.navigateTo(MainViewModel.Screen.LaporRT)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        FeatureIconModern("Keamanan", Icons.Default.Security, Color(0xFF6366F1), Modifier.weight(1f)) {
                            viewModel.navigateTo(MainViewModel.Screen.Keamanan)
                        }
                        FeatureIconModern("Kas RT", Icons.Default.Payments, Color(0xFFEC4899), Modifier.weight(1f)) {
                           // Future feature
                        }
                        FeatureIconModern("Lainnya", Icons.Default.MoreHoriz, Color(0xFF64748B), Modifier.weight(1f)) {
                            viewModel.navigateTo(MainViewModel.Screen.PelayananSurat)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(40.dp)) }
        }
    }
}

@Composable
fun FeatureIconModern(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    badgeCount: Int = 0,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(color.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
            }
            if (badgeCount > 0) {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopEnd)
                        .background(Color(0xFFEF4444), CircleShape)
                        .defaultMinSize(minWidth = 18.dp, minHeight = 18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = badgeCount.toString(),
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun WargaDashboardScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val config by viewModel.rtConfig.collectAsStateWithLifecycle()
    val profile by viewModel.currentRtProfile.collectAsStateWithLifecycle()
    val listWarga by viewModel.wargaList.collectAsStateWithLifecycle()
    val listSurat by viewModel.suratList.collectAsStateWithLifecycle()
    
    val loggedInNik by viewModel.loggedInNik.collectAsStateWithLifecycle()
    val loggedInWargaName by viewModel.loggedInWargaName.collectAsStateWithLifecycle()

    var selectedJenisSurat by remember { mutableStateOf("Surat Pengantar Umum") }
    var inputKeperluan by remember { mutableStateOf("") }
    
    var ktpUri by remember { mutableStateOf<String?>(null) }
    var syaratUri by remember { mutableStateOf<String?>(null) }

    val ktpPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { ktpUri = it.toString() }
    }
    val syaratPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { syaratUri = it.toString() }
    }

    val listJenisSurat = listOf(
        "Surat Pengantar Umum",
        "Surat Domisili",
        "Surat Pengantar SKCK",
        "Surat Keterangan Usaha",
        "Surat Keterangan Tidak Mampu (SKTM)"
    )

    val myLetters = remember(listSurat, loggedInNik) { listSurat.filter { it.NIKPemohon == loggedInNik } }
    val primaryColor = remember(config.primaryColor) {
        try { 
            if (config.primaryColor.isNotEmpty()) {
                Color(android.graphics.Color.parseColor(config.primaryColor)) 
            } else {
                Color(0xFF039BE5)
            }
        } catch (e: Exception) { Color(0xFF039BE5) }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F7FA))) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Box(modifier = Modifier.fillMaxWidth().background(primaryColor).statusBarsPadding().padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            GovernmentLogo(
                                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(8.dp)).background(Color.White).padding(2.dp),
                                logoUrl = config.appLogo
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "Layanan Mandiri Warga", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                                Text(text = "$loggedInWargaName", color = Color.White.copy(alpha = 0.9f), style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        IconButton(onClick = { viewModel.performLogout() }) { Icon(Icons.Default.Logout, null, tint = Color.White) }
                    }
                }
            }

            item {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "PENGAJUAN SURAT MANDIRI",
                                fontWeight = FontWeight.Bold,
                                color = primaryColor,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Pilih Jenis Surat:", style = MaterialTheme.typography.bodySmall)
                            Spacer(modifier = Modifier.height(4.dp))
                            listJenisSurat.forEach { jenis ->
                                Row(modifier = Modifier.fillMaxWidth().clickable { selectedJenisSurat = jenis }.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(selected = selectedJenisSurat == jenis, onClick = { selectedJenisSurat = jenis })
                                    Text(jenis, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                            OutlinedTextField(
                                value = inputKeperluan, 
                                onValueChange = { inputKeperluan = it }, 
                                label = { Text("Keperluan") }, 
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color(0xFF0F172A),
                                    unfocusedTextColor = Color(0xFF1E293B),
                                    focusedLabelColor = Color(0xFF0F172A),
                                    unfocusedLabelColor = Color(0xFF475569)
                                )
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { ktpPicker.launch("image/*") }, 
                                    modifier = Modifier.weight(1f), 
                                    colors = ButtonDefaults.buttonColors(containerColor = if (ktpUri != null) Color(0xFF2E7D32) else Color.Gray)
                                ) {
                                    Icon(if (ktpUri != null) Icons.Default.CheckCircle else Icons.Default.PhotoCamera, null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (ktpUri != null) "KTP SIAP" else "FOTO E-KTP", fontSize = 10.sp)
                                }
                                Button(
                                    onClick = { syaratPicker.launch("image/*") }, 
                                    modifier = Modifier.weight(1f), 
                                    colors = ButtonDefaults.buttonColors(containerColor = if (syaratUri != null) Color(0xFF2E7D32) else Color.Gray)
                                ) {
                                    Icon(if (syaratUri != null) Icons.Default.CheckCircle else Icons.Default.UploadFile, null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (syaratUri != null) "SYARAT SIAP" else "FOTO SYARAT", fontSize = 10.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    if (inputKeperluan.isNotBlank() && ktpUri != null) {
                                        val citizen = listWarga.find { it.nik == loggedInNik }
                                        citizen?.let { viewModel.saveSurat(it.nik, it.nama, selectedJenisSurat, inputKeperluan, it.rtRw.split("/").firstOrNull()?.trim() ?: "") }
                                        inputKeperluan = ""; ktpUri = null; syaratUri = null
                                        Toast.makeText(context, "Pengajuan terkirim!", Toast.LENGTH_SHORT).show()
                                    } else if (ktpUri == null) {
                                        Toast.makeText(context, "Wajib lampirkan Foto E-KTP", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                                enabled = inputKeperluan.isNotBlank() && ktpUri != null
                            ) { Text("KIRIM PENGAJUAN") }
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "KONTAK & LOKASI RT", fontWeight = FontWeight.Bold, color = primaryColor)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(60.dp).clip(CircleShape).background(Color(0xFFF1F5F9)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (profile?.fotoRt?.isNotEmpty() == true) {
                                    AsyncImage(model = profile?.fotoRt, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                                } else {
                                    Icon(Icons.Default.Person, null, tint = Color.LightGray, modifier = Modifier.size(40.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(text = profile?.namaRt?.ifEmpty { "Ketua RT ${profile?.rtNumber ?: "-"}" } ?: "Ketua RT ${profile?.rtNumber ?: "-"}", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                Text(text = "Wilayah RT ${profile?.rtNumber ?: "-"}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW)
                                        val waUrl = "https://api.whatsapp.com/send?phone=${(profile?.noWaRt ?: "").replace("+","").replace(" ","")}&text=Halo Ketua RT, saya ingin bertanya..."
                                        intent.data = Uri.parse(waUrl)
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "WhatsApp tidak terpasang", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Icon(Icons.Default.Message, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Chat WA", fontSize = 11.sp)
                            }

                            Button(
                                onClick = {
                                    if (profile?.koordinatRumah?.isNotEmpty() == true) {
                                        try {
                                            val gmmIntentUri = Uri.parse("geo:0,0?q=${profile?.koordinatRumah}")
                                            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                            mapIntent.setPackage("com.google.android.apps.maps")
                                            context.startActivity(mapIntent)
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Gagal membuka Maps", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        Toast.makeText(context, "Koordinat RT belum diatur", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA4335)),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Lokasi RT", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            item {
                Text(text = "RIWAYAT SURAT SAYA", fontWeight = FontWeight.Bold, color = Color(0xFF475569))
            }

            items(myLetters) { letter ->
                Card(modifier = Modifier.fillMaxWidth().clickable { viewModel.navigateTo(MainViewModel.Screen.LetterDetail(letter.id)) }, colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(letter.status, fontWeight = FontWeight.Bold, color = if (letter.status == "PENDING") Color(0xFFF57F17) else Color(0xFF2E7D32))
                            Text(letter.tanggalSurat, style = MaterialTheme.typography.labelSmall)
                        }
                        Text(letter.jenisSurat.replace("_", " "), fontWeight = FontWeight.Bold)
                        Text(letter.keperluan, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
