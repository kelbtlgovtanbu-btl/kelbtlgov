package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.GovernmentLogo
import com.example.viewmodel.MainViewModel

@Composable
fun LoginScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val config by viewModel.rtConfig.collectAsStateWithLifecycle()
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val primaryColor = remember(config.primaryColor) {
        try {
            if (config.primaryColor.isNotEmpty()) {
                Color(android.graphics.Color.parseColor(config.primaryColor))
            } else {
                Color(0xFF0D47A1)
            }
        } catch (e: Exception) {
            Color(0xFF0D47A1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(primaryColor)
            .padding(24.dp)
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        GovernmentLogo(
            modifier = Modifier.size(100.dp).clip(RoundedCornerShape(20.dp)).background(Color.White).padding(8.dp),
            logoUrl = config.appLogo
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("DIGIBAT RT", color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
        Text("Portal Layanan Mandiri Kelurahan Batulicin", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)

        Spacer(modifier = Modifier.height(40.dp))

        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("MASUK KE SISTEM", fontWeight = FontWeight.Bold, color = primaryColor)
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = identifier,
                    onValueChange = { identifier = it },
                    label = { Text("NIK / ID Pengguna") },
                    placeholder = { Text("Masukkan NIK atau Kata Kunci") },
                    modifier = Modifier.fillMaxWidth().testTag("username_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color(0xFF0F172A),
                        unfocusedTextColor = Color(0xFF1E293B),
                        focusedLabelColor = Color(0xFF0F172A),
                        unfocusedLabelColor = Color(0xFF475569)
                    )
                )
                
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password (Opsional)") },
                    placeholder = { Text("Masukkan Password jika ada") },
                    modifier = Modifier.fillMaxWidth().testTag("password_input"),
                    singleLine = true,
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color(0xFF0F172A),
                        unfocusedTextColor = Color(0xFF1E293B),
                        focusedLabelColor = Color(0xFF0F172A),
                        unfocusedLabelColor = Color(0xFF475569)
                    )
                )
                
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Gunakan NIK Warga, 'admin', 'operator', atau 'rt03' untuk masuk sesuai peran.",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        isLoading = true
                        viewModel.performLogin(identifier, password) { success, msg ->
                            isLoading = false
                            if (!success) Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("login_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("MASUK", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        Text("© 2024 Kelurahan Batulicin - Kab. Tanah Bumbu", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
    }
}

@Composable
fun ProfileSetupScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val profile by viewModel.currentRtProfile.collectAsStateWithLifecycle()
    
    val loggedInRtNumber by viewModel.loggedInRtNumber.collectAsStateWithLifecycle()
    
    var inputNama by remember { mutableStateOf(profile?.namaRt ?: "") }
    var inputNoHp by remember { mutableStateOf(profile?.noWaRt?.ifEmpty { profile?.noHpRt ?: "" } ?: profile?.noHpRt ?: "") }
    var inputRt by remember { mutableStateOf(profile?.rtNumber ?: loggedInRtNumber ?: "01") }

    LaunchedEffect(profile) {
        profile?.let {
            if (inputNama.isEmpty()) inputNama = it.namaRt
            if (inputNoHp.isEmpty()) inputNoHp = it.noWaRt.ifEmpty { it.noHpRt }
            if (inputRt.isEmpty()) inputRt = it.rtNumber
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
            .padding(24.dp)
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(80.dp).background(Color(0xFF1E293B), RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.ManageAccounts, null, tint = Color.White, modifier = Modifier.size(40.dp))
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "E-Profile Otoritas RT",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Black,
            color = Color(0xFF0F172A)
        )
        Text(
            text = "Lengkapi profil digital resmi untuk kebutuhan persuratan dan identitas pada database Kelurahan.",
            textAlign = TextAlign.Center,
            color = Color(0xFF475569),
            style = MaterialTheme.typography.bodyMedium
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = inputNama,
            onValueChange = { inputNama = it },
            label = { Text("Nama Lengkap Ketua RT") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            leadingIcon = { Icon(Icons.Default.Person, null, tint = Color.Gray) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFF0F172A),
                unfocusedTextColor = Color(0xFF1E293B),
                focusedLabelColor = Color(0xFF0F172A),
                unfocusedLabelColor = Color(0xFF475569)
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = inputRt,
                onValueChange = { inputRt = it.take(3) },
                label = { Text("No. RT") },
                modifier = Modifier.weight(0.4f),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF0F172A),
                    unfocusedTextColor = Color(0xFF1E293B)
                )
            )
            OutlinedTextField(
                value = inputNoHp,
                onValueChange = { inputNoHp = it },
                label = { Text("WhatsApp Aktif") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Phone, null, tint = Color.Gray) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF0F172A),
                    unfocusedTextColor = Color(0xFF1E293B)
                )
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = {
                if (inputNama.isNotBlank() && inputNoHp.isNotBlank()) {
                    viewModel.completeRtProfile(inputNama, inputRt, inputNoHp, profile?.fotoRt ?: "") {
                        Toast.makeText(context, "Profil Otoritas diperbarui!", Toast.LENGTH_SHORT).show()
                        viewModel.navigateBack()
                    }
                } else {
                    Toast.makeText(context, "Nama dan WhatsApp wajib diisi!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A))
        ) {
            Text("Simpan & Perbarui Layanan", fontWeight = FontWeight.Bold)
        }
    }
}
