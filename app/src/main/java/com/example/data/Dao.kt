package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WargaDao {
    @Query("SELECT * FROM warga ORDER BY nama ASC")
    fun getAllWarga(): Flow<List<Warga>>

    @Query("SELECT * FROM warga WHERE nik = :nik LIMIT 1")
    suspend fun getWargaByNik(nik: String): Warga?

    @Query("SELECT * FROM warga WHERE nama LIKE '%' || :query || '%' OR nik LIKE '%' || :query || '%'")
    fun searchWarga(query: String): Flow<List<Warga>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWarga(warga: Warga)

    @Update
    suspend fun updateWarga(warga: Warga)

    @Delete
    suspend fun deleteWarga(warga: Warga)
}

@Dao
interface SuratDao {
    @Query("SELECT * FROM surat ORDER BY id DESC")
    fun getAllSurat(): Flow<List<Surat>>

    @Query("SELECT * FROM surat WHERE rtPembuat = :rtNumber ORDER BY id DESC")
    fun getSuratByRt(rtNumber: String): Flow<List<Surat>>

    @Query("SELECT * FROM surat WHERE id = :id LIMIT 1")
    suspend fun getSuratById(id: Int): Surat?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSurat(surat: Surat): Long

    @Update
    suspend fun updateSurat(surat: Surat)

    @Delete
    suspend fun deleteSurat(surat: Surat)
}

@Dao
interface RtConfigDao {
    @Query("SELECT * FROM rt_config WHERE id = 1 LIMIT 1")
    fun getConfigFlow(): Flow<RtConfig?>

    @Query("SELECT * FROM rt_config WHERE id = 1 LIMIT 1")
    suspend fun getConfig(): RtConfig?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveConfig(config: RtConfig)
}

@Dao
interface RtProfileDao {
    @Query("SELECT * FROM rt_profile ORDER BY rtNumber ASC")
    fun getAllProfiles(): Flow<List<RtProfile>>

    @Query("SELECT * FROM rt_profile WHERE rtNumber = :rtNumber LIMIT 1")
    suspend fun getProfileByRt(rtNumber: String): RtProfile?

    @Query("SELECT * FROM rt_profile WHERE rtNumber = :rtNumber LIMIT 1")
    fun getProfileFlowByRt(rtNumber: String): Flow<RtProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProfile(profile: RtProfile)

    @Delete
    suspend fun deleteProfile(profile: RtProfile)
}

@Dao
interface KegiatanDao {
    @Query("SELECT * FROM kegiatan ORDER BY id DESC")
    fun getAllKegiatan(): Flow<List<Kegiatan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKegiatan(kegiatan: Kegiatan)

    @Update
    suspend fun updateKegiatan(kegiatan: Kegiatan)

    @Delete
    suspend fun deleteKegiatan(kegiatan: Kegiatan)
}

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY role ASC, username ASC")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)
}

@Dao
interface AnnouncementDao {
    @Query("SELECT * FROM announcements ORDER BY date DESC, id DESC")
    fun getAllAnnouncements(): Flow<List<Announcement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: Announcement)

    @Delete
    suspend fun deleteAnnouncement(announcement: Announcement)
}

@Dao
interface CitizenReportDao {
    @Query("SELECT * FROM citizen_reports ORDER BY date DESC, id DESC")
    fun getAllReports(): Flow<List<CitizenReport>>

    @Query("SELECT * FROM citizen_reports WHERE reporterNik = :nik ORDER BY id DESC")
    fun getReportsByNik(nik: String): Flow<List<CitizenReport>>

    @Query("SELECT * FROM citizen_reports WHERE id = :id LIMIT 1")
    suspend fun getReportById(id: Int): CitizenReport?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: CitizenReport)

    @Update
    suspend fun updateReport(report: CitizenReport)

    @Delete
    suspend fun deleteReport(report: CitizenReport)
}
