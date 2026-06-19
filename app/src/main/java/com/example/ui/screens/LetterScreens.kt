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
import androidx.compose.ui.draw.alpha
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
import java.text.SimpleDateFormat
import java.util.*

// --- SCREEN 5: PELAYANAN SURAT (LIST) ---
@Composable
fun PelayananSuratScreen(viewModel: MainViewModel) {
    val letters by viewModel.suratList.collectAsStateWithLifecycle()
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val loggedInRtNumber by viewModel.loggedInRtNumber.collectAsStateWithLifecycle()
    val currentNik by viewModel.loggedInNik.collectAsStateWithLifecycle()
    
    val filteredLetters = remember(letters, currentRole, loggedInRtNumber, currentNik) {
        when (currentRole) {
            "RT" -> letters.filter { it.rtPembuat == loggedInRtNumber }
            "WARGA" -> letters.filter { it.NIKPemohon == currentNik }
            else -> letters // Operator/Superadmin see all
        }
    }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Pelayanan Surat", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali")
                    }
                }
            )
        },
        floatingActionButton = {
            if (currentRole == "RT") {
                FloatingActionButton(
                    onClick = { viewModel.navigateTo(MainViewModel.Screen.CreateLetter()) },
                    containerColor = Color(0xFF0F172A),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, "Buat Surat")
                }
            }
        }
    ) { padding ->
        if (filteredLetters.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.HistoryEdu, null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                    Spacer(Modifier.height(16.dp))
                    Text("Belum ada riwayat surat digital", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredLetters.sortedByDescending { it.id }) { surat ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { viewModel.navigateTo(MainViewModel.Screen.LetterDetail(surat.id)) },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.size(48.dp).background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Description, null, tint = Color(0xFF475569))
                            }
                            Spacer(Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(surat.jenisSurat, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                                Text("Pemohon: ${surat.namaPemohon}", fontSize = 12.sp, color = Color.Gray)
                                Text(surat.tanggalSurat, fontSize = 11.sp, color = Color.LightGray)
                            }
                            Surface(
                                color = when(surat.status) {
                                    "PENDING" -> Color(0xFFFEF3C7)
                                    "APPROVED" -> Color(0xFFDCFCE7)
                                    else -> Color(0xFFF1F5F9)
                                },
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(
                                    surat.status,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when(surat.status) {
                                        "PENDING" -> Color(0xFFD97706)
                                        "APPROVED" -> Color(0xFF166534)
                                        else -> Color(0xFF475569)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- SCREEN 6: CREATE LETTER ---
@Composable
fun CreateLetterScreen(viewModel: MainViewModel, initialNik: String?) {
    val context = LocalContext.current
    val wargaList by viewModel.wargaList.collectAsStateWithLifecycle()
    val loggedInRtNumber by viewModel.loggedInRtNumber.collectAsStateWithLifecycle()
    val rtProfiles by viewModel.allRtProfiles.collectAsStateWithLifecycle()
    val profile = remember(rtProfiles, loggedInRtNumber) { rtProfiles.find { it.rtNumber == loggedInRtNumber } }

    var selectedNik by remember { mutableStateOf(initialNik ?: "") }
    var jenisSurat by remember { mutableStateOf("SURAT_KETERANGAN_DOMISILI") }
    var keperluan by remember { mutableStateOf("") }
    
    val selectedWarga = remember(wargaList, selectedNik) { wargaList.find { it.nik == selectedNik } }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Buat Surat Digital", fontWeight = FontWeight.Black) },
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
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Pilih Warga Pemohon", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            
            // Nik Selection (Simplified for now - usually a searchable dropdown)
            OutlinedTextField(
                value = selectedNik,
                onValueChange = { selectedNik = it },
                label = { Text("Masukkan NIK Warga") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ketik NIK 16 digit...") }
            )

            if (selectedWarga != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F9FF))
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF0EA5E9), modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Terdeteksi: ${selectedWarga.nama}", fontWeight = FontWeight.Bold, color = Color(0xFF0369A1))
                    }
                }
            } else if (selectedNik.isNotEmpty()) {
                Text("NIK tidak ditemukan di database unit RT $loggedInRtNumber", fontSize = 11.sp, color = Color.Red)
            }

            Divider()

            Text("Detail Administrasi", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            
            var expanded by remember { mutableStateOf(false) }
            Box(Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = jenisSurat.replace("_", " "),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Jenis Surat") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { expanded = true }) { Icon(Icons.Default.ArrowDropDown, null) }
                    }
                )
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    listOf("SURAT_KETERANGAN_DOMISILI", "SURAT_KETERANGAN_USAHA", "SURAT_PENGANTAR_NIKAH", "SURAT_KETERANGAN_TIDAK_MAMPU").forEach { 
                        DropdownMenuItem(
                            text = { Text(it.replace("_", " ")) },
                            onClick = { jenisSurat = it; expanded = false }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = keperluan,
                onValueChange = { keperluan = it },
                label = { Text("Keperluan / Tujuan") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                placeholder = { Text("Contoh: Syarat Pembukaan Rekening Bank") }
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if (selectedWarga != null && keperluan.isNotBlank()) {
                         viewModel.saveSurat(
                            nik = selectedNik,
                            nama = selectedWarga.nama,
                            jenis = jenisSurat,
                            keperluan = keperluan,
                            rt = loggedInRtNumber ?: ""
                        )
                        viewModel.navigateBack()
                        Toast.makeText(context, "Surat Digital Berhasil Diterbitkan!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Mohon lengkapi data pemohon dan keperluan", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                enabled = selectedWarga != null
            ) {
                Icon(Icons.Default.CloudDone, null)
                Spacer(Modifier.width(8.dp))
                Text("Terbitkan Surat Digital", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --- SCREEN 7: LETTER DETAIL & PRINT PREVIEW ---
@Composable
fun LetterDetailScreen(viewModel: MainViewModel, letterId: Int) {
    val letters by viewModel.suratList.collectAsStateWithLifecycle()
    val surat = remember(letters, letterId) { letters.find { it.id == letterId } }
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val config by viewModel.rtConfig.collectAsStateWithLifecycle()
    
    val rtProfiles by viewModel.allRtProfiles.collectAsStateWithLifecycle()
    val rtProfile = remember(rtProfiles, surat) { rtProfiles.find { it.rtNumber == surat?.rtPembuat } }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Detail Surat", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (surat?.status == "PENDING" && currentRole == "OPERATOR") {
                        IconButton(onClick = { 
                            viewModel.approveSurat(letterId)
                            Toast.makeText(context, "Surat Approved & Terarsip!", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Default.Verified, "Approve", tint = Color(0xFF059669))
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (surat == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Surat tidak ditemukan") }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(Color(0xFFF1F5F9))
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // E-SURAT PAPER UI (Simulated A4)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 800.dp) // Professional limit for wider screens
                        .align(Alignment.CenterHorizontally),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(0.dp), // Papers are usually sharp or very slightly rounded
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 40.dp, vertical = 50.dp) // Generous margins like a real letter
                    ) {
                        // KOP SURAT
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            GovernmentLogo(Modifier.size(60.dp))
                            Spacer(Modifier.width(16.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                                Text("PEMERINTAH KABUPATEN TANAH BUMBU", fontWeight = FontWeight.Bold, fontSize = 21.sp, color = Color(0xFF0F172A))
                                Text("KECAMATAN BATULICIN", fontWeight = FontWeight.Bold, fontSize = 21.sp, color = Color(0xFF0F172A))
                                Text("KELURAHAN BATULICIN", fontWeight = FontWeight.Bold, fontSize = 21.sp, color = Color(0xFF0F172A))
                                Text("KETUA RUKUN TETANGGA ${surat.rtPembuat}", fontWeight = FontWeight.Black, fontSize = 24.sp, color = Color(0xFF0F172A))
                                if (rtProfile != null && rtProfile.alamatRumah.isNotEmpty()) {
                                    Text("Alamat: ${rtProfile.alamatRumah}", fontSize = 9.sp, color = Color.Gray)
                                } else {
                                    Text("alamat jl. Raya Batulicin No.23 RT.06 RW.02 Kelurahan Batulicin", fontSize = 10.sp, color = Color.DarkGray)
                                }
                                if (config.webKelurahan.isNotEmpty() || config.emailKelurahan.isNotEmpty()) {
                                    val webText = if (config.webKelurahan.isNotEmpty()) "Web: ${config.webKelurahan}" else ""
                                    val emailText = if (config.emailKelurahan.isNotEmpty()) "Email: ${config.emailKelurahan}" else ""
                                    val separator = if (webText.isNotEmpty() && emailText.isNotEmpty()) " | " else ""
                                    Text("$webText$separator$emailText", fontSize = 9.sp, color = Color.Gray)
                                }
                            }
                        }
                        
                        Divider(modifier = Modifier.padding(vertical = 12.dp), thickness = 2.dp, color = Color.Black)
                        
                        Text(
                            surat.jenisSurat.replace("_", " "),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "Nomor: ${surat.nomorSurat.ifEmpty { "P-RESEVED/BATULICIN/${surat.id}" }}",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontSize = 11.sp
                        )
                        
                        Spacer(Modifier.height(24.dp))
                        
                        val rwNum = rtProfile?.rwNumber ?: "01"
                        Text("Yang bertanda tangan di bawah ini, Ketua RT.${surat.rtPembuat} RW.$rwNum Kelurahan Batulicin Kecamatan Batulicin, menerangkan bahwa:", fontSize = 13.sp)
                        
                        Spacer(Modifier.height(16.dp))
                        
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            DetailRow("Nama", surat.namaPemohon)
                            DetailRow("NIK", surat.NIKPemohon)
                            DetailRow("Keperluan", surat.keperluan)
                        }
                        
                        Spacer(Modifier.height(16.dp))
                        
                        Text("Demikian surat keterangan ini diberikan untuk dapat dipergunakan sebagaimana mestinya.", fontSize = 13.sp)
                        
                        Spacer(Modifier.height(40.dp))
                        
                        // SIGNATURE AREA
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Batulicin, ${surat.tanggalSurat}", fontSize = 12.sp)
                                Text("Ketua RT.${surat.rtPembuat} RW.$rwNum", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                
                                Box(modifier = Modifier.size(120.dp, 80.dp), contentAlignment = Alignment.Center) {
                                    // TTD Digital logic
                                    if (rtProfile?.stempelImage?.isNotEmpty() == true) {
                                        AsyncImage(
                                            model = rtProfile.stempelImage,
                                            contentDescription = null,
                                            modifier = Modifier.size(80.dp).align(Alignment.CenterStart),
                                            alpha = 0.6f
                                        )
                                    }
                                    
                                    if (rtProfile?.ttdImage?.isNotEmpty() == true) {
                                        AsyncImage(
                                            model = rtProfile.ttdImage,
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Fit
                                        )
                                    } else {
                                        Text("(Tanda Tangan Digital)", color = Color.LightGray, fontSize = 10.sp)
                                    }
                                }
                                
                                Text(rtProfile?.namaRt ?: "..........................", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                        
                        if (surat.status == "APPROVED") {
                            Spacer(Modifier.height(20.dp))
                            BatulicinMonumentGraphic(Modifier.size(40.dp).align(Alignment.CenterHorizontally).alpha(0.3f))
                        }
                    }
                }
                
                Spacer(Modifier.height(24.dp))
                
                Button(
                    onClick = { 
                        Toast.makeText(context, "Menyiapkan File PDF (Simulasi)...", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A))
                ) {
                    Icon(Icons.Default.Print, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Cetak / Simpan PDF")
                }
            }
        }
    }
}
