package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Warga
import com.example.viewmodel.MainViewModel
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition

@Composable
fun WargaListScreen(viewModel: MainViewModel) {
    val wargaList by viewModel.wargaList.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // App Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateBack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Database Warga",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF2C3E50)
                )
                Text(
                    text = "Data kependudukan digital paperless RT mandiri",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF475569)
                )
            }

            SmallFloatingActionButton(
                onClick = { viewModel.navigateTo(MainViewModel.Screen.AddWarga) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, "Tambah Warga")
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                 Icon(Icons.Default.Refresh, "Refresh")
            }
        }

        // Search Input Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("warga_search_input"),
            placeholder = { Text("Cari warga berdasarkan Nama atau NIK...") },
            leadingIcon = { Icon(Icons.Default.Search, "Cari") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                        Icon(Icons.Default.Close, "Hapus")
                    }
                }
            },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = Color(0xFF0F172A),
                unfocusedTextColor = Color(0xFF1E293B),
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color(0xFFE2E8F0)
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (wargaList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = "Pencarian kosong",
                        tint = Color(0xFFBDC3C7),
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Data Warga Tidak Ditemukan",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7F8C8D)
                    )
                    Text(
                        text = "Gunakan tombol + untuk mendaftarkan warga baru dengan scan AI OCR KTP.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFBDC3C7),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(wargaList, key = { it.nik }) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // Default option creates letter for this citizen
                                viewModel.navigateTo(MainViewModel.Screen.CreateLetter(item.nik))
                            },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.nama,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2C3E50)
                                    )
                                    Text(
                                        text = "NIK: ${item.nik}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF34495E),
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                if ((currentRole == "RT" || currentRole == "SUPERADMIN") && item.penerimaBantuan.isNotEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFFE0F2F1), RoundedCornerShape(12.dp))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text("Penerima Bansos", color = Color(0xFF00695C), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = item.alamat,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                maxLines = 2
                            )
                        }
                    }
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun AddWargaScreen(viewModel: MainViewModel) {
    val ocrLoading by viewModel.ocrLoading.collectAsStateWithLifecycle()
    val ocrMessage by viewModel.ocrMessage.collectAsStateWithLifecycle()
    val scannedWarga by viewModel.scannedWargaData.collectAsStateWithLifecycle()
    
    val context = LocalContext.current

    val loggedInRtNumber by viewModel.loggedInRtNumber.collectAsStateWithLifecycle()
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()

    var inputNik by remember { mutableStateOf("") }
    var inputNama by remember { mutableStateOf("") }
    var inputNoKk by remember { mutableStateOf("") }
    var inputTempatLahir by remember { mutableStateOf("") }
    var inputTanggalLahir by remember { mutableStateOf("") }
    var inputJenisKelamin by remember { mutableStateOf("Laki-laki") }
    var inputAgama by remember { mutableStateOf("Islam") }
    var inputKawin by remember { mutableStateOf("Belum Kawin") }
    var inputPekerjaan by remember { mutableStateOf("") }
    var inputAlamat by remember { mutableStateOf("") }
    var inputRtRw by remember { mutableStateOf(if (currentRole == "RT") "${loggedInRtNumber}/01" else "03/01") }

    LaunchedEffect(scannedWarga) {
        scannedWarga?.let {
            inputNik = it.nik
            inputNama = it.nama
            inputNoKk = it.noKk
            inputTempatLahir = it.tempatLahir
            inputTanggalLahir = it.tanggalLahir
            inputJenisKelamin = it.jenisKelamin
            inputAgama = it.agama
            inputKawin = it.statusPerkawinan
            inputPekerjaan = it.pekerjaan
            inputAlamat = it.alamat
            if (currentRole != "RT") {
                inputRtRw = it.rtRw
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
                if (bitmap != null) {
                    viewModel.runOcrOnKtp(bitmap) { _ ->
                        Toast.makeText(context, "Dokumen terbaca oleh Gemini AI OCR!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Berkas gambar rusak atau tidak terbaca", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Gagal mengambil gambar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val triggerMockScanner: () -> Unit = {
        val width = 640
        val height = 400
        val createdBitmap = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(createdBitmap)
        
        val cardPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#42A5F5") 
            style = android.graphics.Paint.Style.FILL
        }
        canvas.drawRoundRect(0f, 0f, width.toFloat(), height.toFloat(), 16f, 16f, cardPaint)
        
        val textPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 22f
            isAntiAlias = true
        }
        canvas.drawText("PROVINSI KALIMANTAN SELATAN", 120f, 40f, textPaint)
        canvas.drawText("KABUPATEN TANAH BUMBU", 150f, 70f, textPaint)
        
        val bodyPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 18f
            isAntiAlias = true
        }
        canvas.drawText("NIK             : 6305012505943890", 20f, 120f, bodyPaint)
        canvas.drawText("Nama            : ANDIKA PRADANA SUTRISNO", 20f, 150f, bodyPaint)
        canvas.drawText("Tempat/Tgl Lahir: Batulicin, 25 Mei 1994", 20f, 180f, bodyPaint)
        canvas.drawText("Jenis Kelamin   : Laki-laki", 20f, 210f, bodyPaint)
        canvas.drawText("Alamat          : Jl Samudra No 45 Kel Batulicin", 20f, 240f, bodyPaint)
        canvas.drawText("Agama           : Islam", 20f, 270f, bodyPaint)
        canvas.drawText("Status Kawin    : Belum Kawin", 20f, 300f, bodyPaint)
        canvas.drawText("Pekerjaan       : Karyawan Swasta", 20f, 330f, bodyPaint)

        viewModel.runOcrOnKtp(createdBitmap) {
            Toast.makeText(context, "Selesai memindai dokumen KTP warga!", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .imePadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateBack() }) {
                Icon(Icons.AutoMirrored.Default.ArrowBack, "Kembali")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Registrasi Warga Baru",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C3E50)
                )
                Text(
                    text = "Layanan pendaftaran mandiri cepat paperless",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF7F8C8D)
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE9F5FE)),
                    border = BorderStroke(1.dp, Color(0xFFBBDEFB)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Gunakan AI OCR Scanner Tanpa Fotokopi KTP",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1565C0),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Cukup arahkan kamera ke KTP fisik untuk pengisian formulir instan otomatis menggunakan kecerdasan buatan.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF546E7A),
                            textAlign = TextAlign.Center
                        )
                        
                        if (ocrLoading) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .background(Color.Black.copy(alpha = 0.05f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                val transition = rememberInfiniteTransition(label = "laser_transition")
                                val laserOffset by transition.animateFloat(
                                    initialValue = 0f,
                                    targetValue = 1f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(1500),
                                        repeatMode = RepeatMode.Reverse
                                    ),
                                    label = "laser_offset"
                                )
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawLine(
                                        color = Color.Red,
                                        start = androidx.compose.ui.geometry.Offset(0f, size.height * laserOffset),
                                        end = androidx.compose.ui.geometry.Offset(size.width, size.height * laserOffset),
                                        strokeWidth = 3.dp.toPx()
                                    )
                                }
                                Text(
                                    text = ocrMessage ?: "Kecerdasan Buatan sedang membaca dokumen...",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1565C0)
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { triggerMockScanner() },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("demo_camera_ocr_btn"),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.CameraAlt, "Camera")
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Pindai KTP", fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = { galleryLauncher.launch("image/*") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1565C0))
                                ) {
                                    Icon(Icons.Default.Photo, "Gallery")
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Pilih Galeri", fontSize = 12.sp)
                                }
                            }
                        }

                        ocrMessage?.let {
                            if (!ocrLoading) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF2E7D32),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "DATA PRIBADI WARGA",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF34495E),
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )

                        OutlinedTextField(
                            value = inputNik,
                            onValueChange = { inputNik = it.filter { ch -> ch.isDigit() }.take(16) },
                            label = { Text("Nomor Induk Kependudukan (NIK)") },
                            modifier = Modifier.fillMaxWidth().testTag("nik_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = inputNama,
                            onValueChange = { inputNama = it },
                            label = { Text("Nama Lengkap (Sesuai KTP)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = inputNoKk,
                            onValueChange = { inputNoKk = it.filter { ch -> ch.isDigit() }.take(16) },
                            label = { Text("Nomor Kartu Keluarga (KK)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = inputTempatLahir,
                                onValueChange = { inputTempatLahir = it },
                                label = { Text("Tempat Lahir") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = inputTanggalLahir,
                                onValueChange = { inputTanggalLahir = it },
                                label = { Text("Tgl Lahir") },
                                modifier = Modifier.weight(1.2f),
                                singleLine = true
                            )
                        }

                        Text("Jenis Kelamin", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { inputJenisKelamin = "Laki-laki" }) {
                                RadioButton(selected = inputJenisKelamin == "Laki-laki", onClick = { inputJenisKelamin = "Laki-laki" })
                                Text("Laki-laki")
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { inputJenisKelamin = "Perempuan" }) {
                                RadioButton(selected = inputJenisKelamin == "Perempuan", onClick = { inputJenisKelamin = "Perempuan" })
                                Text("Perempuan")
                            }
                        }

                        OutlinedTextField(
                            value = inputAgama,
                            onValueChange = { inputAgama = it },
                            label = { Text("Agama") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = inputKawin,
                            onValueChange = { inputKawin = it },
                            label = { Text("Status Perkawinan") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = inputPekerjaan,
                            onValueChange = { inputPekerjaan = it },
                            label = { Text("Pekerjaan") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = inputAlamat,
                            onValueChange = { inputAlamat = it },
                            label = { Text("Alamat Tinggal Lengkap") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = inputRtRw,
                            onValueChange = { if (currentRole != "RT") inputRtRw = it },
                            label = { Text("RT/RW") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = currentRole != "RT",
                            readOnly = currentRole == "RT",
                            singleLine = true
                        )
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        if (inputNik.length != 16) {
                            Toast.makeText(context, "NIK Harus 16 Digit", Toast.LENGTH_LONG).show()
                            return@Button
                        }
                        if (inputNama.isBlank()) {
                            Toast.makeText(context, "Nama Tidak Boleh Kosong", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        
                        val newWarga = Warga(
                            nik = inputNik,
                            nama = inputNama,
                            noKk = inputNoKk,
                            tempatLahir = inputTempatLahir,
                            tanggalLahir = inputTanggalLahir,
                            jenisKelamin = inputJenisKelamin,
                            agama = inputAgama,
                            statusPerkawinan = inputKawin,
                            pekerjaan = inputPekerjaan,
                            alamat = inputAlamat,
                            rtRw = inputRtRw
                        )

                        viewModel.saveWarga(newWarga) {
                            viewModel.clearOcrState()
                            Toast.makeText(context, "Data Warga ${inputNama} Tersimpan!", Toast.LENGTH_SHORT).show()
                            viewModel.navigateBack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp)
                        .testTag("save_warga_submit_btn"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Save, "Save")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Simpan Data Warga")
                }
            }
        }
    }
}
