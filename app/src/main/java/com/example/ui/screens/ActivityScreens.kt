package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.ui.components.AssetUploadCard
import com.example.viewmodel.MainViewModel

@Composable
fun AddActivityScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    var inputJudul by remember { mutableStateOf("") }
    var inputDeskripsi by remember { mutableStateOf("") }
    var selectedKategori by remember { mutableStateOf("KEGIATAN_RT") }
    var photoUri by remember { mutableStateOf<String?>(null) }

    val listKategori = listOf(
        "KEGIATAN_RT" to "Kegiatan RT Mandiri",
        "PENGUMUMAN" to "Pengumuman",
        "KEAMANAN" to "Keamanan & Ronda",
        "GALERY" to "Galery RT"
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()).imePadding()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { viewModel.navigateBack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Tambah Konten", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Text("Kategori Konten", fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listKategori.forEach { (key, label) ->
                FilterChip(
                    selected = selectedKategori == key,
                    onClick = { selectedKategori = key },
                    label = { Text(label, fontSize = 10.sp) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = inputJudul,
            onValueChange = { inputJudul = it },
            label = { Text("Judul Konten/Kegiatan") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        OutlinedTextField(
            value = inputDeskripsi,
            onValueChange = { inputDeskripsi = it },
            label = { Text("Deskripsi Detil") },
            modifier = Modifier.fillMaxWidth().height(150.dp),
            shape = RoundedCornerShape(12.dp)
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Text("Media Pendukung", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        AssetUploadCard(
            title = "Foto Kegiatan",
            currentImage = photoUri,
            onImageCaptured = { photoUri = it }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = {
                if (inputJudul.isEmpty()) {
                    Toast.makeText(context, "Judul wajib diisi!", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.saveKegiatan(inputJudul, inputDeskripsi, selectedKategori, photoUri ?: "") {
                        Toast.makeText(context, "Konten Berhasil Diterbitkan!", Toast.LENGTH_SHORT).show()
                        viewModel.navigateBack()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
        ) {
            Text("Terbitkan Sekarang", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun GalleryScreen(viewModel: MainViewModel) {
    val kegiatan by viewModel.kegiatanList.collectAsStateWithLifecycle()
    val galleryItems = remember(kegiatan) {
        kegiatan.filter { it.kategori == "GALERY" || it.fotoPath.isNotEmpty() }
            .sortedByDescending { it.tanggal }
    }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Galery RT", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        if (galleryItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Collections, null, modifier = Modifier.size(80.dp), tint = Color.LightGray)
                    Spacer(Modifier.height(16.dp))
                    Text("Belum ada dokumentasi.", color = Color.Gray)
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(galleryItems, key = { it.id }) { kgt ->
                    Card(
                        modifier = Modifier.fillMaxWidth().height(180.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            if (kgt.fotoPath.isNotEmpty()) {
                                AsyncImage(
                                    model = kgt.fotoPath,
                                    contentDescription = kgt.judul,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Box(modifier = Modifier.fillMaxSize().background(Color.LightGray), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Image, null, tint = Color.Gray)
                                }
                            }
                            
                            // Caption overlay
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .background(Color.Black.copy(alpha = 0.6f))
                                    .padding(8.dp)
                            ) {
                                Column {
                                    Text(kgt.judul, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(kgt.tanggal, color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
