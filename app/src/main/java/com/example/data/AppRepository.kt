package com.example.data

import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val wargaDao: WargaDao,
    private val suratDao: SuratDao,
    private val rtConfigDao: RtConfigDao,
    private val rtProfileDao: RtProfileDao,
    private val kegiatanDao: KegiatanDao,
    private val userDao: UserDao,
    private val announcementDao: AnnouncementDao,
    private val citizenReportDao: CitizenReportDao
) {
    val allWarga: Flow<List<Warga>> = wargaDao.getAllWarga()
    val allSurat: Flow<List<Surat>> = suratDao.getAllSurat()
    val rtConfig: Flow<RtConfig?> = rtConfigDao.getConfigFlow()
    val allProfiles: Flow<List<RtProfile>> = rtProfileDao.getAllProfiles()
    val allKegiatan: Flow<List<Kegiatan>> = kegiatanDao.getAllKegiatan()
    val allUsers: Flow<List<User>> = userDao.getAllUsers()
    val allAnnouncements: Flow<List<Announcement>> = announcementDao.getAllAnnouncements()
    val allReports: Flow<List<CitizenReport>> = citizenReportDao.getAllReports()

    fun getProfileFlowByRt(rtNumber: String): Flow<RtProfile?> = rtProfileDao.getProfileFlowByRt(rtNumber)

    suspend fun getProfileByRt(rtNumber: String): RtProfile? = rtProfileDao.getProfileByRt(rtNumber)

    suspend fun saveRtProfile(profile: RtProfile) = rtProfileDao.saveProfile(profile)

    suspend fun deleteRtProfile(profile: RtProfile) = rtProfileDao.deleteProfile(profile)

    fun searchWarga(query: String): Flow<List<Warga>> = wargaDao.searchWarga(query)

    suspend fun getWargaByNik(nik: String): Warga? = wargaDao.getWargaByNik(nik)

    suspend fun insertWarga(warga: Warga) = wargaDao.insertWarga(warga)

    suspend fun updateWarga(warga: Warga) = wargaDao.updateWarga(warga)

    suspend fun deleteWarga(warga: Warga) = wargaDao.deleteWarga(warga)

    fun getSuratByRt(rtNumber: String): Flow<List<Surat>> = suratDao.getSuratByRt(rtNumber)

    suspend fun getSuratById(id: Int): Surat? = suratDao.getSuratById(id)

    suspend fun insertSurat(surat: Surat): Long = suratDao.insertSurat(surat)

    suspend fun updateSurat(surat: Surat) = suratDao.updateSurat(surat)

    suspend fun deleteSurat(surat: Surat) = suratDao.deleteSurat(surat)

    suspend fun getRtConfig(): RtConfig? = rtConfigDao.getConfig()

    suspend fun saveRtConfig(config: RtConfig) = rtConfigDao.saveConfig(config)

    suspend fun insertKegiatan(kegiatan: Kegiatan) = kegiatanDao.insertKegiatan(kegiatan)

    suspend fun updateKegiatan(kegiatan: Kegiatan) = kegiatanDao.updateKegiatan(kegiatan)

    suspend fun deleteKegiatan(kegiatan: Kegiatan) = kegiatanDao.deleteKegiatan(kegiatan)

    suspend fun getUserByUsername(username: String): User? = userDao.getUserByUsername(username)

    suspend fun insertUser(user: User) = userDao.insertUser(user)

    suspend fun updateUser(user: User) = userDao.updateUser(user)

    suspend fun deleteUser(user: User) = userDao.deleteUser(user)

    suspend fun insertAnnouncement(announcement: Announcement) = announcementDao.insertAnnouncement(announcement)

    suspend fun deleteAnnouncement(announcement: Announcement) = announcementDao.deleteAnnouncement(announcement)

    suspend fun insertReport(report: CitizenReport) = citizenReportDao.insertReport(report)

    suspend fun updateReport(report: CitizenReport) = citizenReportDao.updateReport(report)

    suspend fun deleteReport(report: CitizenReport) = citizenReportDao.deleteReport(report)

    fun getReportsByNik(nik: String): Flow<List<CitizenReport>> = citizenReportDao.getReportsByNik(nik)

    suspend fun getReportById(id: Int): CitizenReport? = citizenReportDao.getReportById(id)
}
