package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "warga")
data class Warga(
    @PrimaryKey val nik: String,
    val nama: String,
    val noKk: String,
    val tempatLahir: String,
    val tanggalLahir: String,
    val jenisKelamin: String,
    val agama: String,
    val statusPerkawinan: String,
    val pekerjaan: String,
    val alamat: String,
    val rtRw: String,
    val penerimaBantuan: String = "", // empty if none, otherwise description of assistance
    val fotoKtp: String = "",
    val fotoKk: String = ""
)

@Entity(tableName = "surat")
data class Surat(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nomorSurat: String,
    val registerNumber: String,
    val NIKPemohon: String,
    val namaPemohon: String,
    val jenisSurat: String, // Surat Pengantar Umum, Domisili, SKCK, Usaha, SKTM, dll.
    val keperluan: String,
    val tanggalSurat: String,
    val rtPembuat: String,
    val rwPembuat: String,
    val namaRt: String,
    val status: String, // PENDING, APPROVED
    val linkPdf: String = "",
    val qrCodeContent: String = "",
    val ttdPath: String = "",
    val stempelPath: String = "",
    val ktpAttachment: String = "",
    val syaratAttachment: String = ""
)

@Entity(tableName = "rt_config")
data class RtConfig(
    @PrimaryKey val id: Int = 1,
    val appLogo: String = "",
    val suratLogo: String = "",
    val dashboardInteractiveImage: String = "", 
    val primaryColor: String = "#1976D2",
    val secondaryColor: String = "#424242",
    val welcomeMessage: String = "Selamat Datang di Layanan Digital RT",
    val operatorContact: String = "",
    val formatNomorSurat: String = "{{NoUrut}}/SP/Kel.Btl-RT-{{NoRT}}/{{BulanRomawi}}/{{Tahun}}",
    val noWaKelurahan: String = "",
    val emergencyContact1: String = "", // Added for 5 contacts
    val emergencyContact2: String = "",
    val emergencyContact3: String = "",
    val emergencyContact4: String = "",
    val emergencyContact5: String = "",
    val kantorCoordinates: String = "",
    val noKantor: String = "",
    val webKelurahan: String = "https://kel-batulicin.tanahbumbukab.go.id",
    val emailKelurahan: String = "kelbtlgovtanbu@gmail.com"
)

@Entity(tableName = "rt_profile")
data class RtProfile(
    @PrimaryKey val rtNumber: String = "",
    val rwNumber: String = "01",
    val namaRt: String = "",
    val noHpRt: String = "",
    val fotoRt: String = "",
    val ttdImage: String = "",
    val stempelImage: String = "",
    val koordinatRumah: String = "",
    val koordinatPoskamling: String = "",
    val isProfileComplete: Boolean = false,
    val noWaRt: String = "",
    val noUrutRt: String = "", // For ranking or sorting
    val currentNoUrut: Int = 0,
    val alamatRumah: String = ""
)

@Entity(tableName = "announcements")
data class Announcement(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val date: String,
    val author: String,
    val imagePath: String = ""
)

@Entity(tableName = "citizen_reports")
data class CitizenReport(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val reporterNik: String,
    val reporterName: String,
    val title: String,
    val content: String,
    val date: String,
    val status: String = "DITERIMA", // DITERIMA, TINDAKLANJUTI, SELESAI
    val photoPath: String = "",
    val coordinates: String = "",
    val adminResponse: String = "",
    val updatedAt: String = ""
)

@Entity(tableName = "kegiatan")
data class Kegiatan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val judul: String,
    val deskripsi: String,
    val tanggal: String,
    val fotoPath: String = "",
    val kategori: String, // KEGIATAN_RT, LAPOR_WARGA, KEAMANAN, PENGUMUMAN, GALERY
    val status: String = "PENDING", // PENDING, DITERIMA, DITINDAKLANJUTI, SELESAI (for LAPOR_WARGA)
    val senderName: String = ""
)

@Entity(tableName = "users")
data class User(
    @PrimaryKey val username: String,
    val password: String,
    val role: String, // SUPERADMIN, OPERATOR, RT, WARGA
    val rtNumber: String? = null,
    val name: String? = null,
    val nik: String? = null
)
