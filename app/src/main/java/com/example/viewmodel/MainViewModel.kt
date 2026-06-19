package com.example.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.AppRepository
import com.example.data.GeminiOcrClient
import com.example.data.Kegiatan
import com.example.data.RtConfig
import com.example.data.Surat
import com.example.data.Warga
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.widget.Toast

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = AppRepository(
        database.wargaDao(),
        database.suratDao(),
        database.rtConfigDao(),
        database.rtProfileDao(),
        database.kegiatanDao(),
        database.userDao(),
        database.announcementDao(),
        database.citizenReportDao()
    )

    private val firebaseSync = com.example.data.FirebaseSyncManager()

    init {
        android.util.Log.d("MainViewModel", "Initializing MainViewModel...")
        refreshRtProfiles()
    }

    fun refreshRtProfiles() {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                android.util.Log.d("MainViewModel", "Checking Firebase for profile updates...")
                val remoteProfiles = firebaseSync.getAllProfilesFromFirebase()
                if (remoteProfiles.isNotEmpty()) {
                    android.util.Log.d("MainViewModel", "Found ${remoteProfiles.size} remote profiles. Syncing with local DB.")
                    remoteProfiles.forEach { remote ->
                        repository.saveRtProfile(remote)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Failed to refresh RT profiles from Firebase: ${e.message}")
            }
        }
    }

    // Roles: "SUPERADMIN", "RT", "OPERATOR", "WARGA"
    private val _currentRole = MutableStateFlow("WARGA")
    val currentRole: StateFlow<String> = _currentRole.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _loggedInRtNumber = MutableStateFlow<String?>(null)
    val loggedInRtNumber: StateFlow<String?> = _loggedInRtNumber.asStateFlow()

    private val _loggedInNik = MutableStateFlow<String?>(null)
    val loggedInNik: StateFlow<String?> = _loggedInNik.asStateFlow()

    private val _loggedInWargaName = MutableStateFlow<String?>(null)
    val loggedInWargaName: StateFlow<String?> = _loggedInWargaName.asStateFlow()

    // Navigation State
    sealed class Screen {
        object Dashboard : Screen()
        object WargaList : Screen()
        object PelayananSurat : Screen()
        object Settings : Screen() // Superadmin Only
        object AddWarga : Screen()
        data class CreateLetter(val predefinedWargaNik: String? = null) : Screen()
        data class LetterDetail(val letterId: Int) : Screen()
        object AddActivity : Screen()
        object LaporRT : Screen()
        object Keamanan : Screen()
        object Pengumuman : Screen()
        object Gallery : Screen()
        object ProfileSetup : Screen() // For RT
        object UserManagement : Screen() // Superadmin
        object AppCustomization : Screen() // Superadmin
        object OperatorPanel : Screen() // Operator
        object PengaturanUmum : Screen() // New: Superadmin General Settings
        object RTProfileManagement : Screen() // New: Superadmin RT Profiles
        data class EditRtProfile(val rtNum: String) : Screen() // New: Edit RT Profile details
        object LaporMandiri : Screen() // New: Direct reporting for RTs
    }

    private val _navigationStack = MutableStateFlow<List<Screen>>(listOf(Screen.Dashboard))
    val currentScreen: StateFlow<Screen> = _navigationStack
        .map { it.lastOrNull() ?: Screen.Dashboard }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.Eagerly, Screen.Dashboard)

    fun navigateTo(screen: Screen) {
        android.util.Log.d("MainViewModel", "Navigating to: $screen")
        val current = _navigationStack.value.toMutableList()
        if (current.lastOrNull() == screen) return
        
        current.add(screen)
        _navigationStack.value = current
    }

    fun navigateBack() {
        android.util.Log.d("MainViewModel", "Navigating back")
        val current = _navigationStack.value.toMutableList()
        if (current.size > 1) {
            current.removeAt(current.size - 1)
            _navigationStack.value = current
        }
    }

    fun formatDateToIndo(dateStr: String): String {
        if (dateStr.isBlank()) return ""
        return try {
            // Attempt to parse standard YYYY-MM-DD first
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val outputFormat = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID"))
            val date = inputFormat.parse(dateStr)
            if (date != null) outputFormat.format(date) else dateStr
        } catch (e: Exception) {
            dateStr
        }
    }

    // Citizens (Warga) Searching & Data State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    @OptIn(kotlinx.coroutines.FlowPreview::class, kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val wargaList: StateFlow<List<Warga>> = _searchQuery
        .flatMapLatest { query ->
            android.util.Log.d("MainViewModel", "Querying Warga with: '$query'")
            if (query.isBlank()) {
                repository.allWarga
            } else {
                repository.searchWarga(query)
            }
        }
        .onEach { android.util.Log.d("MainViewModel", "Warga Flow emitted: ${it.size} items") }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Config, Letters, Profiles flows
    val rtConfig: StateFlow<com.example.data.RtConfig> = repository.rtConfig
        .map { it ?: com.example.data.RtConfig() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), com.example.data.RtConfig())

    val allRtProfiles: StateFlow<List<com.example.data.RtProfile>> = repository.allProfiles
        .onEach { android.util.Log.d("MainViewModel", "RT Profiles Flow emitted: ${it.size} items") }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val currentRtProfile: StateFlow<com.example.data.RtProfile?> = _loggedInRtNumber
        .flatMapLatest { rt ->
            if (rt != null) repository.getProfileFlowByRt(rt)
            else kotlinx.coroutines.flow.flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val suratList: StateFlow<List<Surat>> = _loggedInRtNumber
        .flatMapLatest { rt ->
            if (rt != null) repository.getSuratByRt(rt)
            else repository.allSurat
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val kegiatanList: StateFlow<List<Kegiatan>> = repository.allKegiatan
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<com.example.data.User>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val announcementList: StateFlow<List<com.example.data.Announcement>> = repository.allAnnouncements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reportList: StateFlow<List<com.example.data.CitizenReport>> = repository.allReports
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // OCR scanning state
    private val _ocrLoading = MutableStateFlow(false)
    val ocrLoading: StateFlow<Boolean> = _ocrLoading.asStateFlow()

    private val _ocrMessage = MutableStateFlow<String?>(null)
    val ocrMessage: StateFlow<String?> = _ocrMessage.asStateFlow()

    // Setup active form states
    private val _scannedWargaData = MutableStateFlow<Warga?>(null)
    val scannedWargaData: StateFlow<Warga?> = _scannedWargaData.asStateFlow()

    fun performLogin(usernameInput: String, passwordInput: String, onResult: (Boolean, String) -> Unit) {
        val username = usernameInput.trim()
        val password = passwordInput.trim()
        
        android.util.Log.d("MainViewModel", "Performing login for: '$username'")
        viewModelScope.launch {
            // Priority for pre-defined hardcoded superadmin
            if (username.lowercase() == "bono" && (password == "Bono" || password == "bono")) {
                _currentRole.value = "SUPERADMIN"
                _loggedInRtNumber.value = null
                _loggedInNik.value = null
                _loggedInWargaName.value = "Bono (Superadmin)"
                _isLoggedIn.value = true
                onResult(true, "Login Superadmin Berhasil!")
                return@launch
            }

            // Check database for users
            val user = repository.getUserByUsername(username.lowercase())
            if (user != null) {
                if (user.password == password) {
                    _currentRole.value = user.role
                    _loggedInRtNumber.value = user.rtNumber
                    _loggedInNik.value = user.nik
                    _loggedInWargaName.value = user.name ?: user.username
                    _isLoggedIn.value = true
                    
                    if (user.role == "RT") {
                        val profile = repository.getProfileByRt(user.rtNumber ?: "")
                        if (profile == null || !profile.isProfileComplete) {
                            navigateTo(Screen.ProfileSetup)
                        }
                    }
                    
                    onResult(true, "Login ${user.role} Berhasil!")
                    return@launch
                } else {
                    android.util.Log.w("MainViewModel", "Password mismatch for user: $username")
                    onResult(false, "Password salah untuk ID tersebut.")
                    return@launch
                }
            }

            // Fallback for WARGA login using NIK as username
            val citizen = repository.getWargaByNik(username)
            if (citizen != null) {
                _currentRole.value = "WARGA"
                _loggedInRtNumber.value = citizen.rtRw.split("/").firstOrNull()?.trim() ?: "01"
                _loggedInNik.value = citizen.nik
                _loggedInWargaName.value = citizen.nama
                _isLoggedIn.value = true
                onResult(true, "Login Warga Berhasil! Selamat datang, ${citizen.nama}.")
                return@launch
            }

            android.util.Log.w("MainViewModel", "Login failed: User not found for '$username'")
            onResult(false, "Login Gagal! ID tidak terdaftar atau Salah Password.")
        }
    }

    fun completeRtProfile(name: String, rt: String, phone: String, photo: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val existingProfile = repository.getProfileByRt(rt)
                val updated = (existingProfile ?: com.example.data.RtProfile(rtNumber = rt)).copy(
                    namaRt = name,
                    rtNumber = rt,
                    noHpRt = phone,
                    noWaRt = phone,
                    fotoRt = photo,
                    isProfileComplete = true
                )
                repository.saveRtProfile(updated)
                firebaseSync.syncRtProfile(updated)
                
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    _loggedInRtNumber.value = rt
                    _loggedInWargaName.value = name
                    onComplete()
                }
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Failed to complete profile: ${e.message}")
            }
        }
    }

    fun performLogout() {
        viewModelScope.launch {
            _isLoggedIn.value = false
            _loggedInRtNumber.value = null
            _loggedInNik.value = null
            _loggedInWargaName.value = null
            _navigationStack.value = listOf(Screen.Dashboard)
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun runOcrOnKtp(bitmap: Bitmap, onComplete: (Warga) -> Unit) {
        viewModelScope.launch {
            _ocrLoading.value = true
            _ocrMessage.value = "Menghubungkan ke Gemini AI OCR..."
            val result = GeminiOcrClient.performOcr(bitmap)
            _ocrLoading.value = false
            when (result) {
                is GeminiOcrClient.OcrResult.Success -> {
                    _scannedWargaData.value = result.warga
                    _ocrMessage.value = if (result.isSimulated) {
                        "Simulasi OCR Berhasil! (API limit/menggunakan data simulasi default)."
                    } else {
                        "Gemini OCR KTP Berhasil mengekstrak data!"
                    }
                    onComplete(result.warga)
                }
                is GeminiOcrClient.OcrResult.Error -> {
                    _ocrMessage.value = result.message
                }
            }
        }
    }

    fun clearOcrState() {
        _scannedWargaData.value = null
        _ocrMessage.value = null
    }

    // Create Citizen
    fun saveWarga(warga: Warga, onComplete: () -> Unit = {}) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                android.util.Log.d("MainViewModel", "Saving Warga: ${warga.nama}")
                repository.insertWarga(warga)
                android.util.Log.d("MainViewModel", "Warga successfully saved to local DB: ${warga.nik}")
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    onComplete()
                }
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Error saving Warga: ${e.message}", e)
            }
        }
    }

    fun approveSurat(suratId: Int, onComplete: () -> Unit = {}) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val surat = suratList.value.find { it.id == suratId } ?: return@launch
                
                // Auto generate next sequence number
                val profile = repository.getProfileByRt(surat.rtPembuat)
                val currentSeq = profile?.currentNoUrut ?: 0
                val nextSeq = currentSeq + 1
                
                // Format Roman Month
                val currentMonth = SimpleDateFormat("M", Locale.getDefault()).format(Date()).toIntOrNull() ?: 5
                val romanMonths = listOf("", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII")
                val monthRomawi = if (currentMonth in 1..12) romanMonths[currentMonth] else "V"
                val currentYear = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())
                
                val config = rtConfig.value
                val noUrutStr = nextSeq.toString().padStart(3, '0')
                val generatedNoSurat = config.formatNomorSurat
                    .replace("{{NoUrut}}", noUrutStr)
                    .replace("{{NoRT}}", surat.rtPembuat)
                    .replace("{{BulanRomawi}}", monthRomawi)
                    .replace("{{Tahun}}", currentYear)

                repository.updateSurat(surat.copy(
                    status = "APPROVED",
                    nomorSurat = generatedNoSurat
                ))
                
                // Update sequence in profile
                if (profile != null) {
                    repository.saveRtProfile(profile.copy(currentNoUrut = nextSeq))
                }
                
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    onComplete()
                }
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Error approving letter: ${e.message}")
            }
        }
    }

    // Alias for UI consistency
    fun saveSurat(nik: String, nama: String, jenis: String, keperluan: String, rt: String) {
        val warga = Warga(nik = nik, nama = nama, noKk = "", tempatLahir = "", tanggalLahir = "", jenisKelamin = "", agama = "", statusPerkawinan = "", pekerjaan = "", alamat = "", rtRw = "$rt/01")
        generateLetter(warga, jenis, keperluan)
    }

    fun approveLetter(surat: Surat, onComplete: () -> Unit = {}) = approveSurat(surat.id, onComplete)

    fun deleteWarga(warga: Warga, onComplete: () -> Unit = {}) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                repository.deleteWarga(warga)
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    onComplete()
                }
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Error deleting warga: ${e.message}")
            }
        }
    }

    fun updateWargaAid(nik: String, desc: String) {
        viewModelScope.launch {
            val warga = repository.getWargaByNik(nik)
            if (warga != null) {
                repository.insertWarga(warga.copy(penerimaBantuan = desc))
            }
        }
    }

    // Announcement Management
    fun saveAnnouncement(title: String, content: String, author: String, imagePath: String = "", onComplete: () -> Unit = {}) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val dateStr = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID")).format(Date())
                val ann = com.example.data.Announcement(
                    title = title,
                    content = content,
                    date = dateStr,
                    author = author,
                    imagePath = imagePath
                )
                repository.insertAnnouncement(ann)
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    onComplete()
                }
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Error saving announcement: ${e.message}")
            }
        }
    }

    fun deleteAnnouncement(ann: com.example.data.Announcement) {
        viewModelScope.launch {
            repository.deleteAnnouncement(ann)
        }
    }

    // Citizen Report Management (Lifecycle requested by user)
    fun saveCitizenReport(title: String, content: String, nik: String, name: String, photoPath: String = "", coords: String = "", onComplete: () -> Unit = {}) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
                val rep = com.example.data.CitizenReport(
                    reporterNik = nik,
                    reporterName = name,
                    title = title,
                    content = content,
                    date = dateStr,
                    status = "DITERIMA",
                    photoPath = photoPath,
                    coordinates = coords,
                    updatedAt = dateStr
                )
                repository.insertReport(rep)
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    onComplete()
                }
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Error saving report: ${e.message}")
            }
        }
    }

    fun printSurat(context: android.content.Context, surat: Surat) {
        // In a real app, we would use PrintManager with a custom PrintDocumentAdapter
        // or a library like IText to generate a PDF and then open it.
        // For this applet, we simulate the "Download PDF" action.
        android.widget.Toast.makeText(context, "Menciptakan Berkas PDF Resmi Surat ${surat.registerNumber}...", android.widget.Toast.LENGTH_LONG).show()
        
        viewModelScope.launch {
            kotlinx.coroutines.delay(2000)
            android.widget.Toast.makeText(context, "PDF Berhasil Diunduh ke /Downloads!", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    fun updateReportStatus(reportId: Int, newStatus: String, response: String = "", onComplete: () -> Unit = {}) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val report = repository.getReportById(reportId)
                if (report != null) {
                    val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
                    val updated = report.copy(
                        status = newStatus,
                        adminResponse = response,
                        updatedAt = dateStr
                    )
                    repository.updateReport(updated)
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                        onComplete()
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Error updating report status: ${e.message}")
            }
        }
    }
    fun deleteReport(report: com.example.data.CitizenReport, onComplete: () -> Unit = {}) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                repository.deleteReport(report)
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    onComplete()
                }
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Error deleting report: ${e.message}")
            }
        }
    }

    // Settings config saver (Enhanced for Superadmin - Global)
    fun saveGeneralSettings(
        appLogo: String = "",
        suratLogo: String = "",
        dashboardInteractiveImage: String = "",
        primaryColor: String = "",
        secondaryColor: String = "",
        welcomeMsg: String = "",
        opContact: String = "",
        formatPattern: String = "",
        noWaKel: String = "",
        emergency1: String = "",
        emergency2: String = "",
        emergency3: String = "",
        emergency4: String = "",
        emergency5: String = "",
        kantorCoords: String = "",
        noKtr: String = "",
        webKel: String = "",
        emailKel: String = "",
        onComplete: () -> Unit = {}
    ) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val current = rtConfig.value
                val updated = current.copy(
                    appLogo = if (appLogo.isNotEmpty()) appLogo else current.appLogo,
                    suratLogo = if (suratLogo.isNotEmpty()) suratLogo else current.suratLogo,
                    dashboardInteractiveImage = if (dashboardInteractiveImage.isNotEmpty()) dashboardInteractiveImage else current.dashboardInteractiveImage,
                    primaryColor = if (primaryColor.isNotEmpty()) primaryColor else current.primaryColor,
                    secondaryColor = if (secondaryColor.isNotEmpty()) secondaryColor else current.secondaryColor,
                    welcomeMessage = if (welcomeMsg.isNotEmpty()) welcomeMsg else current.welcomeMessage,
                    operatorContact = opContact, 
                    formatNomorSurat = if (formatPattern.isNotEmpty()) formatPattern else current.formatNomorSurat,
                    noWaKelurahan = if (noWaKel.isNotEmpty()) noWaKel else current.noWaKelurahan,
                    emergencyContact1 = if (emergency1.isNotEmpty()) emergency1 else current.emergencyContact1,
                    emergencyContact2 = if (emergency2.isNotEmpty()) emergency2 else current.emergencyContact2,
                    emergencyContact3 = if (emergency3.isNotEmpty()) emergency3 else current.emergencyContact3,
                    emergencyContact4 = if (emergency4.isNotEmpty()) emergency4 else current.emergencyContact4,
                    emergencyContact5 = if (emergency5.isNotEmpty()) emergency5 else current.emergencyContact5,
                    kantorCoordinates = if (kantorCoords.isNotEmpty()) kantorCoords else current.kantorCoordinates,
                    noKantor = if (noKtr.isNotEmpty()) noKtr else current.noKantor,
                    webKelurahan = if (webKel.isNotEmpty()) webKel else current.webKelurahan,
                    emailKelurahan = if (emailKel.isNotEmpty()) emailKel else current.emailKelurahan
                )
                repository.saveRtConfig(updated)
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    onComplete()
                }
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Error saving config: ${e.message}")
            }
        }
    }

    fun saveRtProfile(
        rtNum: String,
        rwNum: String,
        bossName: String,
        signature: String = "",
        stamp: String = "",
        bossPhoto: String = "",
        waRt: String = "",
        coords: String = "",
        poskamlingCoords: String = "",
        noUrut: String = "",
        homeAddress: String = "",
        oldRtNum: String? = null,
        onComplete: () -> Unit = {}
    ) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                android.util.Log.d("MainViewModel", "Preparing to save RT Profile: $rtNum")
                // If RT Number changed, we need to handle the old record
                if (oldRtNum != null && oldRtNum != rtNum) {
                    repository.getProfileByRt(oldRtNum)?.let { oldProf ->
                        repository.deleteRtProfile(oldProf)
                        android.util.Log.d("MainViewModel", "Deleted old RT Profile: $oldRtNum")
                    }
                }
                
                val existing = repository.getProfileByRt(rtNum)
                val updated = (existing ?: com.example.data.RtProfile(rtNumber = rtNum)).copy(
                    rtNumber = rtNum,
                    rwNumber = rwNum,
                    namaRt = bossName,
                    noHpRt = waRt.ifEmpty { existing?.noHpRt ?: "" },
                    noWaRt = waRt.ifEmpty { existing?.noWaRt ?: "" },
                    ttdImage = signature.ifEmpty { existing?.ttdImage ?: "" },
                    stempelImage = stamp.ifEmpty { existing?.stempelImage ?: "" },
                    fotoRt = bossPhoto.ifEmpty { existing?.fotoRt ?: "" },
                    koordinatRumah = coords.ifEmpty { existing?.koordinatRumah ?: "" },
                    koordinatPoskamling = poskamlingCoords.ifEmpty { existing?.koordinatPoskamling ?: "" },
                    noUrutRt = noUrut.ifEmpty { existing?.noUrutRt ?: "" },
                    isProfileComplete = true,
                    alamatRumah = homeAddress.ifEmpty { existing?.alamatRumah ?: "" }
                )
                
                repository.saveRtProfile(updated)
                android.util.Log.d("MainViewModel", "RT Profile successfully saved locally: $rtNum")
                
                // Sync to firebase in background, don't wait for it if UI needs to move
                viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                    try {
                        firebaseSync.syncRtProfile(updated)
                        android.util.Log.d("MainViewModel", "RT Profile synced to Firebase: $rtNum")
                    } catch (e: Exception) {
                        android.util.Log.e("MainViewModel", "Firebase sync failed for $rtNum: ${e.message}")
                    }
                }
                
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    onComplete()
                }
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "CRITICAL: Failed to save RT profile $rtNum locally: ${e.message}", e)
            }
        }
    }

    fun deleteRtProfile(profile: com.example.data.RtProfile) {
        viewModelScope.launch {
            repository.deleteRtProfile(profile)
            firebaseSync.deleteRtProfile(profile.rtNumber)
        }
    }

    // Letter automation logic
    fun generateLetter(
        warga: Warga,
        jenisSurat: String,
        keperluan: String,
        ktpImg: String = "",
        syaratImg: String = ""
    ) {
        viewModelScope.launch {
            val config = rtConfig.value
            val profile = currentRtProfile.value ?: com.example.data.RtProfile(rtNumber = _loggedInRtNumber.value ?: "01")
            val nextNumber = profile.currentNoUrut + 1
            
            // Format Roman Month
            val currentMonth = SimpleDateFormat("M", Locale.getDefault()).format(Date()).toIntOrNull() ?: 5
            val romanMonths = listOf("", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII")
            val monthRomawi = if (currentMonth in 1..12) romanMonths[currentMonth] else "V"
            val currentYear = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())

            // Auto format letter code
            val noUrutStr = nextNumber.toString().padStart(3, '0')
            val generatedNoSurat = config.formatNomorSurat
                .replace("{{NoUrut}}", noUrutStr)
                .replace("{{NoRT}}", profile.rtNumber)
                .replace("{{BulanRomawi}}", monthRomawi)
                .replace("{{Tahun}}", currentYear)

            val timestampUuid = System.currentTimeMillis().toString().takeLast(6)
            val regNumber = "REG-$currentYear${currentMonth.toString().padStart(2, '0')}-$timestampUuid"
            val qrContent = "https://digibat-rt.batulicin.id/verify/$regNumber"

            // Always use creator's RT/RW for the letter authority, not the citizen's residing RT/RW 
            // unless the creator is a Superadmin/Operator who might be acting on behalf of an RT.
            // But for a Ketua RT user, it MUST use their own RT info.
            val authorityRt = profile.rtNumber
            val authorityRw = profile.rwNumber

            val newSurat = Surat(
                nomorSurat = generatedNoSurat,
                registerNumber = regNumber,
                NIKPemohon = warga.nik,
                namaPemohon = warga.nama,
                jenisSurat = jenisSurat,
                keperluan = keperluan,
                tanggalSurat = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID")).format(Date()),
                rtPembuat = authorityRt,
                rwPembuat = authorityRw,
                namaRt = profile.namaRt,
                status = "PENDING", 
                qrCodeContent = qrContent,
                ttdPath = profile.ttdImage,
                stempelPath = profile.stempelImage,
                ktpAttachment = ktpImg,
                syaratAttachment = syaratImg
            )

            repository.insertSurat(newSurat)
            repository.saveRtProfile(profile.copy(currentNoUrut = nextNumber))
            
            navigateTo(Screen.PelayananSurat)
        }
    }

    fun deleteSurat(surat: Surat) {
        viewModelScope.launch {
            repository.deleteSurat(surat)
        }
    }

    fun saveKegiatan(judul: String, deskripsi: String, kategori: String, photoPath: String = "", sender: String = "", onComplete: () -> Unit = {}) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                android.util.Log.d("MainViewModel", "Saving Kegiatan: $judul")
                val dateStr = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID")).format(Date())
                val kgt = Kegiatan(
                    judul = judul,
                    deskripsi = deskripsi,
                    tanggal = dateStr,
                    kategori = kategori,
                    fotoPath = photoPath,
                    senderName = sender,
                    status = "PENDING"
                )
                repository.insertKegiatan(kgt)
                android.util.Log.d("MainViewModel", "Kegiatan successfully saved to DB")
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    onComplete()
                }
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Error saving kegiatan: ${e.message}")
            }
        }
    }

    fun updateKegiatanStatus(kegiatan: Kegiatan, newStatus: String) {
        viewModelScope.launch {
            repository.updateKegiatan(kegiatan.copy(status = newStatus))
        }
    }

    // User Management for Superadmin
    fun addOrUpdateUser(user: com.example.data.User, oldUsername: String? = null) {
        viewModelScope.launch {
            if (oldUsername != null && oldUsername != user.username) {
                repository.getUserByUsername(oldUsername)?.let {
                    repository.deleteUser(it)
                }
            }
            repository.insertUser(user)
            firebaseSync.syncUser(user)
        }
    }

    fun deleteUser(user: com.example.data.User) {
        viewModelScope.launch {
            repository.deleteUser(user)
        }
    }

    fun clearLocalDatabase(onComplete: () -> Unit = {}) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                database.clearAllTables()
                android.util.Log.w("MainViewModel", "Local database CLEARED manually.")
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    onComplete()
                }
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Failed to clear database: ${e.message}")
            }
        }
    }
}
