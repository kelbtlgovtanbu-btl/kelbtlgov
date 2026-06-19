package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.viewmodel.MainViewModel
import com.example.ui.components.AssetUploadCard
import com.example.ui.components.SignaturePad

@Composable
fun SettingsCategory(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(title, fontWeight = FontWeight.Black, fontSize = 12.sp, color = Color(0xFF334155), letterSpacing = 0.5.sp)
            content()
        }
    }
}

@Composable
fun AssetPickRow(title: String, current: String, enabled: Boolean = true, onPick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().alpha(if (enabled) 1f else 0.6f)) {
        Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569), modifier = Modifier.padding(bottom = 8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(if (enabled) Color.White else Color(0xFFF1F5F9))
                .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                .clickable(enabled = enabled, onClick = onPick)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                if (enabled) Icons.Default.UploadFile else Icons.Default.Lock, 
                null, 
                tint = if (enabled) Color(0xFF3B82F6) else Color.Gray
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = if (current.isEmpty()) "Pilih File Gambar..." else "File Terpilih: ${current.takeLast(20)}",
                fontSize = 14.sp,
                color = if (current.isEmpty()) Color(0xFF94A3B8) else Color(0xFF0F172A),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            if (current.isNotEmpty()) {
                Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF10B981), modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun ContrastTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    subtitle: String? = null,
    placeholder: String? = null,
    modifier: Modifier = Modifier,
    maxLines: Int = 1,
    enabled: Boolean = true
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
        if (subtitle != null) {
            Text(subtitle, fontSize = 11.sp, color = Color(0xFF64748B), modifier = Modifier.padding(bottom = 2.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            maxLines = maxLines,
            enabled = enabled,
            placeholder = { Text(placeholder ?: "Masukkan $label...", color = Color(0xFF94A3B8)) },
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = if (enabled) Color(0xFF0F172A) else Color.Gray,
                unfocusedTextColor = if (enabled) Color(0xFF1E293B) else Color.Gray,
                focusedBorderColor = Color(0xFF3B82F6),
                unfocusedBorderColor = Color(0xFFCBD5E1),
                focusedContainerColor = if (enabled) Color.White else Color(0xFFF1F5F9),
                unfocusedContainerColor = if (enabled) Color.White else Color(0xFFF1F5F9)
            )
        )
    }
}

@Composable
fun SettingsAssetButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector, 
    label: String, 
    modifier: Modifier = Modifier,
    currentValue: String = "",
    onSelect: () -> Unit
) {
    val context = LocalContext.current
    val hasAsset = currentValue.isNotEmpty()
    
    Surface(
        onClick = { 
            onSelect()
            Toast.makeText(context, "$label berhasil diunggah (Simulasi)!", Toast.LENGTH_SHORT).show() 
        },
        modifier = modifier.height(44.dp),
        shape = RoundedCornerShape(8.dp),
        color = if (hasAsset) Color(0xFFF0FDF4) else Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, if (hasAsset) Color(0xFF22C55E) else Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (hasAsset) Icons.Default.CheckCircle else icon, 
                null, 
                modifier = Modifier.size(16.dp), 
                tint = if (hasAsset) Color(0xFF16A34A) else Color(0xFF475569)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (hasAsset) Color(0xFF166534) else Color(0xFF1E293B))
        }
    }
}

@Composable
fun RTProfileManagementScreen(viewModel: MainViewModel) {
    val profilesRaw by viewModel.allRtProfiles.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    
    LaunchedEffect(Unit) {
        viewModel.refreshRtProfiles()
    }
    
    val profiles = remember(profilesRaw, searchQuery) {
        profilesRaw.filter { 
            it.rtNumber.contains(searchQuery, ignoreCase = true) || 
            it.namaRt.contains(searchQuery, ignoreCase = true) ||
            it.noUrutRt.contains(searchQuery, ignoreCase = true)
        }.sortedWith(
            compareBy<com.example.data.RtProfile> { it.noUrutRt.toIntOrNull() ?: 999 }
                .thenBy { it.rtNumber }
        )
    }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { 
                    Column {
                        Text("Kelurahan Batulicin", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text("Data Profil RT", fontWeight = FontWeight.Black)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshRtProfiles() }) {
                        Icon(Icons.Default.Refresh, "Refresh")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.navigateTo(MainViewModel.Screen.EditRtProfile("NEW")) },
                containerColor = Color(0xFF0F172A),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, "Tambah RT")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFF8FAFC)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Cari Berdasarkan RT atau Nama...") },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, null, tint = Color.Gray)
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        cursorColor = Color(0xFF0F172A),
                        focusedBorderColor = Color(0xFF0F172A),
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    ),
                    singleLine = true
                )
            }
            
            item {
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Pimpinan Unit RT", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    Surface(
                        color = Color(0xFF0F172A),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            text = "${profiles.size}",
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            items(profiles, key = { it.rtNumber }) { profile ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.navigateTo(MainViewModel.Screen.EditRtProfile(profile.rtNumber)) },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(50.dp),
                            shape = CircleShape,
                            color = Color(0xFFF1F5F9)
                        ) {
                            if (profile.fotoRt.isNotEmpty()) {
                                AsyncImage(
                                    model = profile.fotoRt,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(profile.rtNumber, fontWeight = FontWeight.Black, color = Color(0xFF64748B))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = profile.namaRt, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                            Text(text = "RUKUN TETANGGA ${profile.rtNumber} / RW ${profile.rwNumber}", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
                        }
                        IconButton(onClick = { viewModel.deleteRtProfile(profile) }) {
                            Icon(Icons.Default.DeleteOutline, null, tint = Color(0xFFEF4444))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditRtProfileScreen(viewModel: MainViewModel, rtNum: String) {
    val profiles by viewModel.allRtProfiles.collectAsStateWithLifecycle()
    val profile = remember(profiles, rtNum) { profiles.find { it.rtNumber == rtNum } }
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val isSuperAdmin = currentRole == "SUPERADMIN"
    val isNew = rtNum == "NEW"
    val context = LocalContext.current
    
    var nameInput by remember { mutableStateOf(profile?.namaRt ?: "") }
    var rtNumberInput by remember { mutableStateOf(if (isNew) "" else (profile?.rtNumber ?: rtNum)) }
    var rwNumberInput by remember { mutableStateOf(profile?.rwNumber ?: "01") }
    var waInput by remember { mutableStateOf(profile?.noWaRt?.ifEmpty { profile?.noHpRt ?: "" } ?: profile?.noHpRt ?: "") }
    var coordsInput by remember { mutableStateOf(profile?.koordinatRumah ?: "") }
    var bossPhotoInput by remember { mutableStateOf(profile?.fotoRt ?: "") }
    var signatureInput by remember { mutableStateOf(profile?.ttdImage ?: "") }
    var stampInput by remember { mutableStateOf(profile?.stempelImage ?: "") }
    var noUrutInput by remember { mutableStateOf(profile?.noUrutRt ?: "") }
    var alamatRumahInput by remember { mutableStateOf(profile?.alamatRumah ?: "") }

    LaunchedEffect(profile) {
        profile?.let {
            nameInput = it.namaRt
            rtNumberInput = it.rtNumber
            rwNumberInput = it.rwNumber
            waInput = it.noWaRt.ifEmpty { it.noHpRt }
            coordsInput = it.koordinatRumah
            bossPhotoInput = it.fotoRt
            signatureInput = it.ttdImage
            stampInput = it.stempelImage
            noUrutInput = it.noUrutRt
            alamatRumahInput = it.alamatRumah
        }
    }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { 
                    Column {
                        Text("Kelurahan Batulicin > RT ${if (isNew) "Baru" else rtNum}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text(if (isNew) "Tambah Data Profil RT" else "Profil RT $rtNum", fontWeight = FontWeight.Bold) 
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if (rtNumberInput.isBlank()) {
                        Toast.makeText(context, "Nomor RT WAJIB diisi!", Toast.LENGTH_SHORT).show()
                        return@ExtendedFloatingActionButton
                    }
                    if (coordsInput.isBlank()) {
                        Toast.makeText(context, "Koordinat rumah RT WAJIB diisi!", Toast.LENGTH_SHORT).show()
                        return@ExtendedFloatingActionButton
                    }
                    viewModel.saveRtProfile(
                        rtNum = rtNumberInput.trim(),
                        rwNum = rwNumberInput.trim(),
                        bossName = nameInput.trim(),
                        signature = signatureInput,
                        stamp = stampInput,
                        bossPhoto = bossPhotoInput,
                        waRt = waInput.trim(),
                        coords = coordsInput.trim(),
                        noUrut = noUrutInput.trim(),
                        homeAddress = alamatRumahInput.trim(),
                        oldRtNum = if (isNew) null else rtNum
                    ) {
                        Toast.makeText(context, "Profil RT $rtNumberInput Berhasil Disimpan!", Toast.LENGTH_SHORT).show()
                        viewModel.navigateBack()
                    }
                },
                icon = { Icon(Icons.Default.CloudDone, null) },
                text = { Text("Simpan Profil") },
                containerColor = Color(0xFF059669),
                contentColor = Color.White
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (!isSuperAdmin) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                    border = BorderStroke(1.dp, Color(0xFFF59E0B))
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, null, tint = Color(0xFFD97706))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Beberapa data sensitif hanya dapat diubah oleh Superadmin Kelurahan untuk keamanan identitas otoritas RT.",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF92400E)
                        )
                    }
                }
            }

            SettingsCategory(title = "STRUKTUR PENGURUS RT") {
                ContrastTextField(
                    value = nameInput, 
                    onValueChange = { nameInput = it }, 
                    label = "Nama RT (Nama Lengkap Ketua RT)"
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ContrastTextField(
                        value = rtNumberInput, 
                        onValueChange = { rtNumberInput = it }, 
                        label = "Nomor RT", 
                        modifier = Modifier.weight(1f),
                        enabled = isSuperAdmin || isNew
                    )
                    ContrastTextField(
                        value = noUrutInput, 
                        onValueChange = { noUrutInput = it }, 
                        label = "No. Urut RT", 
                        modifier = Modifier.weight(1f),
                        placeholder = "Contoh: 1"
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ContrastTextField(
                        value = rwNumberInput, 
                        onValueChange = { rwNumberInput = it }, 
                        label = "RW", 
                        modifier = Modifier.weight(1f),
                        enabled = isSuperAdmin
                    )
                    ContrastTextField(
                        value = waInput, 
                        onValueChange = { waInput = it }, 
                        label = "Nomor Whatsapp RT",
                        modifier = Modifier.weight(2f),
                        placeholder = "Contoh: 081234567890"
                    )
                }
                ContrastTextField(
                    value = alamatRumahInput,
                    onValueChange = { alamatRumahInput = it },
                    label = "Alamat Rumah Ketua RT",
                    placeholder = "Contoh: Jl. Nusantara No. 5 RT 03/RW 01",
                    maxLines = 2
                )
            }

            SettingsCategory(title = "LOKASI & KOORDINAT") {
                ContrastTextField(
                    value = coordsInput, 
                    onValueChange = { coordsInput = it }, 
                    label = "Titik Koordinat Rumah RT",
                    placeholder = "Contoh: -3.456, 114.789"
                )
                Button(
                    onClick = { 
                        coordsInput = "-3.4${(100..999).random()}, 114.8${(100..999).random()}"
                        Toast.makeText(context, "Titik Koordinat Berhasil Didapatkan!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Icon(Icons.Default.MyLocation, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Deteksi Lokasi Otomatis")
                }
            }

            SettingsCategory(title = "BERKAS DIGITAL RT") {
                Text("Foto Ketua RT", style = MaterialTheme.typography.labelSmall)
                AssetUploadCard(title = "Foto RT", currentImage = bossPhotoInput, onImageCaptured = { bossPhotoInput = it })
                
                Spacer(Modifier.height(8.dp))
                Text("Tanda Tangan (TTD) RT", style = MaterialTheme.typography.labelSmall)
                AssetUploadCard(title = "TTD RT", currentImage = signatureInput, onImageCaptured = { signatureInput = it })

                Spacer(Modifier.height(8.dp))
                Text("Stempel RT", style = MaterialTheme.typography.labelSmall)
                AssetUploadCard(title = "Stempel RT", currentImage = stampInput, onImageCaptured = { stampInput = it })
            }
            
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun UserManagementScreen(viewModel: MainViewModel) {
    val users by viewModel.allUsers.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedUserUsername by remember { mutableStateOf<String?>(null) }
    
    var usernameForm by remember { mutableStateOf("") }
    var passwordForm by remember { mutableStateOf("") }
    var roleForm by remember { mutableStateOf("RT") }
    var nameForm by remember { mutableStateOf("") }
    var rtNumberForm by remember { mutableStateOf("") }
    var nikForm by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateBack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = Color(0xFF1E293B))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Manajemen Database User", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = Color(0xFF0F172A))
                Text("Kelola akun otoritas RT dan Operator", style = MaterialTheme.typography.bodySmall, color = Color(0xFF475569))
            }
            Button(
                onClick = { 
                    usernameForm = ""
                    passwordForm = ""
                    roleForm = "RT"
                    nameForm = ""
                    rtNumberForm = ""
                    nikForm = ""
                    selectedUserUsername = null
                    showAddDialog = true 
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.PersonAdd, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("Tambah", fontSize = 12.sp)
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(users, key = { it.username }) { user ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable {
                        usernameForm = user.username
                        passwordForm = user.password
                        roleForm = user.role
                        nameForm = user.name ?: ""
                        rtNumberForm = user.rtNumber ?: ""
                        nikForm = user.nik ?: ""
                        selectedUserUsername = user.username
                        showAddDialog = true
                    },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier.size(44.dp).background(Color(0xFFF1F5F9), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                           Icon(
                                imageVector = when(user.role) {
                                    "SUPERADMIN" -> Icons.Default.Shield
                                    "OPERATOR" -> Icons.Default.DisplaySettings
                                    else -> Icons.Default.Person
                                },
                                contentDescription = null,
                                tint = Color(0xFF334155),
                                modifier = Modifier.size(24.dp)
                           )
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(user.name ?: user.username, fontWeight = FontWeight.Black, color = Color(0xFF1E293B))
                            Text("Akses: ${user.role} ${if (user.rtNumber != null) "- RT ${user.rtNumber}" else ""}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF475569))
                            Text("ID: ${user.username}", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                        }
                        IconButton(onClick = { viewModel.deleteUser(user) }) {
                            Icon(Icons.Default.Delete, null, tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
    }

    if (showAddDialog) {
        val context = LocalContext.current
        val existingMatch = remember(users, usernameForm) { 
            users.find { it.username.lowercase() == usernameForm.lowercase() && usernameForm.isNotEmpty() } 
        }
        val isEditing = existingMatch != null
        val dialogTitle = if (isEditing) "Edit User: ${usernameForm}" else "Tambah User Baru"
        
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(dialogTitle) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = usernameForm, 
                        onValueChange = { if (!isEditing) usernameForm = it }, 
                        enabled = !isEditing,
                        label = { Text("Username (ID Login)") }, 
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = nameForm, 
                        onValueChange = { nameForm = it }, 
                        label = { Text("Nama Lengkap") }, 
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = nikForm, 
                        onValueChange = { nikForm = it }, 
                        label = { Text("NIK (Opsional)") }, 
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = passwordForm, 
                        onValueChange = { passwordForm = it }, 
                        label = { Text("Password") }, 
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Text("Role Akses", style = MaterialTheme.typography.labelMedium, color = Color(0xFF475569))
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.horizontalScroll(rememberScrollState())) {
                        RadioButton(selected = roleForm == "RT", onClick = { roleForm = "RT" })
                        Text("RT", color = Color(0xFF1E293B))
                        Spacer(Modifier.width(16.dp))
                        RadioButton(selected = roleForm == "OPERATOR", onClick = { roleForm = "OPERATOR" })
                        Text("Operator", color = Color(0xFF1E293B))
                        Spacer(Modifier.width(16.dp))
                        RadioButton(selected = roleForm == "SUPERADMIN", onClick = { roleForm = "SUPERADMIN" })
                        Text("Superadmin", color = Color(0xFF1E293B))
                    }

                    if (roleForm == "RT") {
                        OutlinedTextField(
                            value = rtNumberForm, 
                            onValueChange = { rtNumberForm = it }, 
                            label = { Text("Nomor RT") }, 
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color(0xFF0F172A),
                                unfocusedTextColor = Color(0xFF1E293B),
                                focusedLabelColor = Color(0xFF0F172A),
                                unfocusedLabelColor = Color(0xFF475569)
                            )
                        )
                    }
                }
            },
            confirmButton = {
                val currentUsers = users
                Button(onClick = {
                    if (usernameForm.isNotEmpty() && passwordForm.isNotEmpty()) {
                        viewModel.addOrUpdateUser(com.example.data.User(
                            username = usernameForm.lowercase().trim(),
                            password = passwordForm.trim(),
                            role = roleForm,
                            name = nameForm.trim(),
                            nik = nikForm.trim().ifBlank { null },
                            rtNumber = if (roleForm == "RT") rtNumberForm.trim() else null
                        ), oldUsername = selectedUserUsername)
                        Toast.makeText(context, "User ${usernameForm.trim()} berhasil disimpan!", Toast.LENGTH_SHORT).show()
                        showAddDialog = false
                    } else {
                        Toast.makeText(context, "ID dan Password wajib diisi!", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Batal") }
            }
        )
    }
}

@Composable
fun AppCustomizationScreen(viewModel: MainViewModel) {
    val config by viewModel.rtConfig.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var welcomeMsg by remember { mutableStateOf(config.welcomeMessage) }
    var primaryColorHex by remember { mutableStateOf(config.primaryColor) }
    var opContactPhone by remember { mutableStateOf(config.operatorContact) }

    LaunchedEffect(config) {
        welcomeMsg = config.welcomeMessage
        primaryColorHex = config.primaryColor
        opContactPhone = config.operatorContact
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .imePadding()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { viewModel.navigateBack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("Branding Aplikasi", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Sesuaikan tampilan publik aplikasi", style = MaterialTheme.typography.bodySmall)
            }
        }
        
        Spacer(Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = welcomeMsg,
                    onValueChange = { welcomeMsg = it },
                    label = { Text("Pesan Selamat Datang") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color(0xFF0F172A),
                        unfocusedTextColor = Color(0xFF1E293B),
                        focusedLabelColor = Color(0xFF0F172A),
                        unfocusedLabelColor = Color(0xFF475569)
                    )
                )
                
                OutlinedTextField(
                    value = primaryColorHex,
                    onValueChange = { primaryColorHex = it },
                    label = { Text("Warna Tema (Hex, e.g #1976D2)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color(0xFF0F172A),
                        unfocusedTextColor = Color(0xFF1E293B),
                        focusedLabelColor = Color(0xFF0F172A),
                        unfocusedLabelColor = Color(0xFF475569)
                    )
                )

                OutlinedTextField(
                    value = opContactPhone,
                    onValueChange = { opContactPhone = it },
                    label = { Text("Kontak WhatsApp Operator (e.g 628...)") },
                    placeholder = { Text("Untuk koordinasi surat selesai") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color(0xFF0F172A),
                        unfocusedTextColor = Color(0xFF1E293B),
                        focusedLabelColor = Color(0xFF0F172A),
                        unfocusedLabelColor = Color(0xFF475569)
                    )
                )

                Button(
                    onClick = {
                        viewModel.saveGeneralSettings(
                            primaryColor = primaryColorHex,
                            welcomeMsg = welcomeMsg,
                            opContact = opContactPhone
                        )
                        Toast.makeText(context, "Tampilan aplikasi diperbarui!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Simpan Perubahan")
                }
                
                Spacer(Modifier.height(16.dp))
                
                var showResetConfirm by remember { mutableStateOf(false) }
                OutlinedButton(
                    onClick = { showResetConfirm = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    border = BorderStroke(1.dp, Color.Red)
                ) {
                    Icon(Icons.Default.DeleteForever, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Reset Database Lokal (Wipe All)")
                }
                
                if (showResetConfirm) {
                    AlertDialog(
                        onDismissRequest = { showResetConfirm = false },
                        title = { Text("Konfirmasi Reset") },
                        text = { Text("Seluruh data warga, RT, dan profil akan dihapus dari perangkat ini. Data yang sudah tersinkron ke Cloud tetap aman. Lanjutkan?") },
                        confirmButton = {
                            TextButton(onClick = {
                                viewModel.clearLocalDatabase {
                                    showResetConfirm = false
                                    Toast.makeText(context, "Database dibersihkan. Memuat ulang...", Toast.LENGTH_LONG).show()
                                    viewModel.navigateTo(MainViewModel.Screen.Dashboard)
                                }
                            }) {
                                Text("YA, RESET", color = Color.Red)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showResetConfirm = false }) {
                                Text("BATAL")
                            }
                        }
                    )
                }
            }
        }
        
        Spacer(Modifier.height(20.dp))
        
        Text("Aset Gambar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssetUploadCardInner("Logo App", Icons.Default.AddPhotoAlternate, Modifier.weight(1f))
            AssetUploadCardInner("Banner App", Icons.Default.Image, Modifier.weight(1f))
        }
    }
}

@Composable
fun AssetUploadCardInner(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(100.dp).clickable { /* Mock upload */ },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = Color.Gray)
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun PengaturanUmumScreen(viewModel: MainViewModel) {
    val config by viewModel.rtConfig.collectAsStateWithLifecycle()
    
    var logoInput by remember(config) { mutableStateOf(config.appLogo) }
    var suratLogoInput by remember(config) { mutableStateOf(config.suratLogo) }
    var interactiveInput by remember(config) { mutableStateOf(config.dashboardInteractiveImage) }
    var primaryColorInput by remember(config) { mutableStateOf(config.primaryColor) }
    var secondaryColorInput by remember(config) { mutableStateOf(config.secondaryColor) }
    var welcomeInput by remember(config) { mutableStateOf(config.welcomeMessage) }
    var formatInput by remember(config) { mutableStateOf(config.formatNomorSurat) }
    var opContactInput by remember(config) { mutableStateOf(config.operatorContact) }
    var noWaKelInput by remember(config) { mutableStateOf(config.noWaKelurahan) }
    var emergency1 by remember(config) { mutableStateOf(config.emergencyContact1) }
    var emergency2 by remember(config) { mutableStateOf(config.emergencyContact2) }
    var emergency3 by remember(config) { mutableStateOf(config.emergencyContact3) }
    var emergency4 by remember(config) { mutableStateOf(config.emergencyContact4) }
    var emergency5 by remember(config) { mutableStateOf(config.emergencyContact5) }
    var kantorCoordsInput by remember(config) { mutableStateOf(config.kantorCoordinates) }
    var noKantorInput by remember(config) { mutableStateOf(config.noKantor) }
    var webKelInput by remember(config) { mutableStateOf(config.webKelurahan) }
    var emailKelInput by remember(config) { mutableStateOf(config.emailKelurahan) }

    val logoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { logoInput = it.toString() }
    }
    val suratLogoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { suratLogoInput = it.toString() }
    }
    val interactivePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { interactiveInput = it.toString() }
    }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Pengaturan Umum", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    viewModel.saveGeneralSettings(
                        appLogo = logoInput,
                        suratLogo = suratLogoInput,
                        dashboardInteractiveImage = interactiveInput,
                        primaryColor = primaryColorInput,
                        secondaryColor = secondaryColorInput,
                        welcomeMsg = welcomeInput,
                        opContact = opContactInput,
                        formatPattern = formatInput,
                        noWaKel = noWaKelInput,
                        emergency1 = emergency1,
                        emergency2 = emergency2,
                        emergency3 = emergency3,
                        emergency4 = emergency4,
                        emergency5 = emergency5,
                        kantorCoords = kantorCoordsInput,
                        noKtr = noKantorInput,
                        webKel = webKelInput,
                        emailKel = emailKelInput
                    )
                    viewModel.navigateBack()
                },
                icon = { Icon(Icons.Default.Save, null) },
                text = { Text("Simpan Perubahan") },
                containerColor = Color(0xFF2563EB),
                contentColor = Color.White
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Branding Assets
            SettingsCategory(title = "BRANDING & VISUAL") {
                AssetPickRow(title = "Logo Aplikasi", current = logoInput) { logoPicker.launch("image/*") }
                AssetPickRow(title = "Logo Surat", current = suratLogoInput) { suratLogoPicker.launch("image/*") }
                AssetPickRow(title = "Gambar Interaktif Dashboard", current = interactiveInput) { interactivePicker.launch("image/*") }
                
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ContrastTextField(
                        value = primaryColorInput,
                        onValueChange = { primaryColorInput = it },
                        label = "Warna Primer (Hex)",
                        modifier = Modifier.weight(1f)
                    )
                    ContrastTextField(
                        value = secondaryColorInput,
                        onValueChange = { secondaryColorInput = it },
                        label = "Warna Sekunder (Hex)",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // App Content
            SettingsCategory(title = "KONTAK & LOKASI KELURAHAN") {
                ContrastTextField(
                    value = welcomeInput,
                    onValueChange = { welcomeInput = it },
                    label = "Pesan Selamat Datang",
                    maxLines = 2
                )
                ContrastTextField(
                    value = noWaKelInput,
                    onValueChange = { noWaKelInput = it },
                    label = "Nomor WhatsApp Kelurahan",
                    placeholder = "Contoh: 08123456789"
                )
                ContrastTextField(
                    value = noKantorInput,
                    onValueChange = { noKantorInput = it },
                    label = "Nomor Kantor Kelurahan",
                    placeholder = "Contoh: (0511) 123456"
                )
                ContrastTextField(
                    value = kantorCoordsInput,
                    onValueChange = { kantorCoordsInput = it },
                    label = "Titik Koordinat Kantor",
                    placeholder = "Latitude, Longitude"
                )
                ContrastTextField(
                    value = webKelInput,
                    onValueChange = { webKelInput = it },
                    label = "Website Kelurahan",
                    placeholder = "Contoh: https://kel-batulicin.tanahbumbukab.go.id"
                )
                ContrastTextField(
                    value = emailKelInput,
                    onValueChange = { emailKelInput = it },
                    label = "Email Kelurahan",
                    placeholder = "Contoh: kelbtlgovtanbu@gmail.com"
                )
            }

            SettingsCategory(title = "5 KONTAK DARURAT") {
                ContrastTextField(value = emergency1, onValueChange = { emergency1 = it }, label = "Kontak Darurat 1", placeholder = "Nama: Nomor")
                ContrastTextField(value = emergency2, onValueChange = { emergency2 = it }, label = "Kontak Darurat 2", placeholder = "Nama: Nomor")
                ContrastTextField(value = emergency3, onValueChange = { emergency3 = it }, label = "Kontak Darurat 3", placeholder = "Nama: Nomor")
                ContrastTextField(value = emergency4, onValueChange = { emergency4 = it }, label = "Kontak Darurat 4", placeholder = "Nama: Nomor")
                ContrastTextField(value = emergency5, onValueChange = { emergency5 = it }, label = "Kontak Darurat 5", placeholder = "Nama: Nomor")
            }

            // Administrasi
            SettingsCategory(title = "ADMINISTRASI SURAT") {
                ContrastTextField(
                    value = formatInput,
                    onValueChange = { formatInput = it },
                    label = "Format Nomor Surat",
                    subtitle = "Template: {{NoUrut}}/SP/{{NoRT}}/{{BulanRomawi}}/{{Tahun}}"
                )
            }
            
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun OperatorPanelScreen(viewModel: MainViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Reg. Surat", "Pengumuman", "Laporan Warga")

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF1F5F9))) {
        Row(
            modifier = Modifier.fillMaxWidth().background(Color.White).padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateBack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Panel Operator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        TabRow(selectedTabIndex = selectedTab, containerColor = Color.White) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontSize = 12.sp) }
                )
            }
        }

        when (selectedTab) {
            0 -> LetterRegistrationPanel(viewModel)
            1 -> AnnouncementManagementPanel(viewModel)
            2 -> ReportLifecyclePanel(viewModel)
        }
    }
}

@Composable
fun LetterRegistrationPanel(viewModel: MainViewModel) {
    val letters by viewModel.suratList.collectAsStateWithLifecycle()
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Daftar Registrasi Surat", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(letters, key = { it.id }) { surat ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { viewModel.navigateTo(MainViewModel.Screen.LetterDetail(surat.id)) },
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                text = "No: ${surat.nomorSurat}", 
                                style = MaterialTheme.typography.labelSmall, 
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF64748B)
                            )
                            Text(
                                text = surat.status, 
                                color = if (surat.status == "PENDING") Color(0xFFF59E0B) else Color(0xFF10B981), 
                                fontWeight = FontWeight.Bold, 
                                fontSize = 10.sp
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = surat.namaPemohon, 
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = surat.jenisSurat.replace("_", " "), 
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF1E293B),
                            fontWeight = FontWeight.Medium
                        )
                        
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Event, null, modifier = Modifier.size(12.dp), tint = Color(0xFF94A3B8))
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = surat.tanggalSurat,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF64748B)
                            )
                        }
                        
                        Text(
                            text = "Keperluan: ${surat.keperluan}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF475569),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        
                        // Action Buttons for RT/Admin
                        if (surat.status == "PENDING" && (currentRole == "RT" || currentRole == "SUPERADMIN" || currentRole == "OPERATOR")) {
                            Spacer(Modifier.height(8.dp))
                            Button(
                                onClick = { 
                                    viewModel.approveSurat(surat.id)
                                    Toast.makeText(context, "Surat berhasil disetujui!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Setujui (Approve)")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnnouncementManagementPanel(viewModel: MainViewModel) {
    val announcements by viewModel.announcementList.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    var showDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("UMUM") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Manajemen Pengumuman", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Button(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, null)
                Text("Baru")
            }
        }
        
        Spacer(Modifier.height(12.dp))
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(announcements, key = { it.id }) { ann ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(ann.title, fontWeight = FontWeight.Bold)
                            Text(ann.content, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        IconButton(onClick = { viewModel.deleteAnnouncement(ann) }) {
                            Icon(Icons.Default.Delete, null, tint = Color.Red)
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Buat Pengumuman Baru") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Judul") })
                    OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Isi Pengumuman") }, minLines = 3)
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        viewModel.saveAnnouncement(title, content, category)
                        showDialog = false
                        title = ""; content = ""
                        Toast.makeText(context, "Pengumuman Diterbitkan!", Toast.LENGTH_SHORT).show()
                    }
                }) { Text("Terbitkan") }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Batal") } }
        )
    }
}

@Composable
fun ReportLifecyclePanel(viewModel: MainViewModel) {
    val reports by viewModel.reportList.collectAsStateWithLifecycle()
    
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Daftar Laporan Warga", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(reports, key = { it.id }) { rpt ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Surface(color = Color(0xFFF1F5F9), shape = RoundedCornerShape(4.dp)) {
                                Text(rpt.status, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(rpt.date, style = MaterialTheme.typography.labelSmall)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(rpt.title, fontWeight = FontWeight.Bold)
                        Text(rpt.content, style = MaterialTheme.typography.bodyMedium)
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Status: ${rpt.status}", fontWeight = FontWeight.Bold, color = Color(0xFF2563EB), fontSize = 12.sp)
                            if (rpt.status != "SELESAI") {
                                TextButton(onClick = { viewModel.updateReportStatus(rpt.id, "SELESAI") }) {
                                    Text("Tandai Selesai")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
