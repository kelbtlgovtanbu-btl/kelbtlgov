package com.example.data

import android.content.Context
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Warga::class, Surat::class, RtConfig::class, Kegiatan::class, User::class, Announcement::class, CitizenReport::class, RtProfile::class],
    version = 14,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wargaDao(): WargaDao
    abstract fun suratDao(): SuratDao
    abstract fun rtConfigDao(): RtConfigDao
    abstract fun rtProfileDao(): RtProfileDao
    abstract fun kegiatanDao(): KegiatanDao
    abstract fun userDao(): UserDao
    abstract fun announcementDao(): AnnouncementDao
    abstract fun citizenReportDao(): CitizenReportDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "digibat_db"
                )
                .addCallback(DatabaseCallback(scope))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            populateDbRaw(db)
        }

        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
            super.onDestructiveMigration(db)
            populateDbRaw(db)
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            android.util.Log.d("AppDatabase", "Database opened successfully.")
        }

        private fun populateDbRaw(db: SupportSQLiteDatabase) {
            android.util.Log.d("AppDatabase", "Populating database with sample data...")
            // 1. Global App Config
            val configValues = ContentValues().apply {
                put("id", 1)
                put("appLogo", "")
                put("suratLogo", "")
                put("dashboardInteractiveImage", "")
                put("primaryColor", "#1976D2")
                put("secondaryColor", "#424242")
                put("welcomeMessage", "Selamat Datang di Layanan Digital RT")
                put("operatorContact", "")
                put("formatNomorSurat", "{{NoUrut}}/SP/Kel.Btl-RT-{{NoRT}}/{{BulanRomawi}}/{{Tahun}}")
                put("webKelurahan", "https://kel-batulicin.tanahbumbukab.go.id")
                put("emailKelurahan", "kelbtlgovtanbu@gmail.com")
            }
            db.insert("rt_config", SQLiteDatabase.CONFLICT_REPLACE, configValues)

            // 2. Sample RT Profile
            val rtProfileList = listOf(
                ContentValues().apply {
                    put("rtNumber", "03")
                    put("rwNumber", "01")
                    put("namaRt", "H. Ahmad Fauzi, S.E.")
                    put("noHpRt", "081234567890")
                    put("fotoRt", "")
                    put("ttdImage", "PRE_LOADED_SIGNATURE")
                    put("stempelImage", "PRE_LOADED_STEMPEL")
                    put("koordinatRumah", "-3.454, 115.987")
                    put("koordinatPoskamling", "-3.456, 115.989")
                    put("isProfileComplete", 1)
                    put("noWaRt", "081234567890")
                    put("noUrutRt", "03")
                    put("currentNoUrut", 5)
                    put("alamatRumah", "Jl. Raya Batulicin No. 100 RT 03/RW 01, Kel. Batulicin")
                },
                ContentValues().apply {
                    put("rtNumber", "01")
                    put("rwNumber", "01")
                    put("namaRt", "M. Yusuf")
                    put("noHpRt", "085244556677")
                    put("fotoRt", "")
                    put("ttdImage", "")
                    put("stempelImage", "")
                    put("koordinatRumah", "-3.450, 115.980")
                    put("isProfileComplete", 1)
                    put("noWaRt", "085244556677")
                    put("noUrutRt", "01")
                    put("currentNoUrut", 0)
                    put("alamatRumah", "Jl. Pelabuhan No. 12 RT 01/RW 01, Kel. Batulicin")
                },
                ContentValues().apply {
                    put("rtNumber", "02")
                    put("rwNumber", "01")
                    put("namaRt", "Slamet Riyadi")
                    put("noHpRt", "085299887766")
                    put("fotoRt", "")
                    put("isProfileComplete", 1)
                    put("noWaRt", "085299887766")
                    put("noUrutRt", "02")
                    put("currentNoUrut", 0)
                    put("alamatRumah", "Jl. Hasanuddin No. 45 RT 02/RW 01, Kel. Batulicin")
                }
            )
            rtProfileList.forEach { db.insert("rt_profile", SQLiteDatabase.CONFLICT_REPLACE, it) }

            // 2. Sample Warga Database
            val listWarga = listOf(
                ContentValues().apply {
                    put("nik", "6305011212850001")
                    put("nama", "Budi Hartono")
                    put("noKk", "6305010202100001")
                    put("tempatLahir", "Batulicin")
                    put("tanggalLahir", "1985-12-12")
                    put("jenisKelamin", "Laki-laki")
                    put("agama", "Islam")
                    put("statusPerkawinan", "Kawin")
                    put("pekerjaan", "Pegawai Swasta")
                    put("alamat", "Jl. Raya Batulicin No. 24 RT 03/RW 01, Kel. Batulicin")
                    put("rtRw", "03/01")
                    put("penerimaBantuan", "")
                    put("fotoKtp", "placeholder_ktp_budi")
                    put("fotoKk", "")
                },
                ContentValues().apply {
                    put("nik", "6305014506920002")
                    put("nama", "Siti Aminah")
                    put("noKk", "6305010202100001")
                    put("tempatLahir", "Kotabaru")
                    put("tanggalLahir", "1992-06-15")
                    put("jenisKelamin", "Perempuan")
                    put("agama", "Islam")
                    put("statusPerkawinan", "Kawin")
                    put("pekerjaan", "Mengurus Rumah Tangga")
                    put("alamat", "Jl. Raya Batulicin No. 24 RT 03/RW 01, Kel. Batulicin")
                    put("rtRw", "03/01")
                    put("penerimaBantuan", "")
                    put("fotoKtp", "")
                    put("fotoKk", "")
                },
                ContentValues().apply {
                    put("nik", "6305012301900003")
                    put("nama", "Agus Prasetyo")
                    put("noKk", "6305010908150002")
                    put("tempatLahir", "Banjarmasin")
                    put("tanggalLahir", "1990-01-23")
                    put("jenisKelamin", "Laki-laki")
                    put("agama", "Kristen")
                    put("statusPerkawinan", "Belum Kawin")
                    put("pekerjaan", "Wiraswasta")
                    put("alamat", "Jl. Merdeka Gang Damai No. 8 RT 03/RW 01, Kel. Batulicin")
                    put("rtRw", "03/01")
                    put("penerimaBantuan", "Penerima BLT Desa Mandiri")
                    put("fotoKtp", "")
                    put("fotoKk", "")
                },
                ContentValues().apply {
                    put("nik", "6305016503950004")
                    put("nama", "Dewi Lestari")
                    put("noKk", "6305010908150002")
                    put("tempatLahir", "Batulicin")
                    put("tanggalLahir", "1995-03-25")
                    put("jenisKelamin", "Perempuan")
                    put("agama", "Kristen")
                    put("statusPerkawinan", "Belum Kawin")
                    put("pekerjaan", "Karyawan Honorer")
                    put("alamat", "Jl. Merdeka Gang Damai No. 8 RT 03/RW 01, Kel. Batulicin")
                    put("rtRw", "03/01")
                    put("penerimaBantuan", "")
                    put("fotoKtp", "")
                    put("fotoKk", "")
                },
                ContentValues().apply {
                    put("nik", "6305011708780005")
                    put("nama", "Eko Prasetyo Utomo")
                    put("noKk", "6305011505050003")
                    put("tempatLahir", "Surabaya")
                    put("tanggalLahir", "1978-08-17")
                    put("jenisKelamin", "Laki-laki")
                    put("agama", "Islam")
                    put("statusPerkawinan", "Cerai Hidup")
                    put("pekerjaan", "Buruh Harian Lepas")
                    put("alamat", "Jl. Kusuma Bangsa No. 12B RT 03/RW 01, Kel. Batulicin")
                    put("rtRw", "03/01")
                    put("penerimaBantuan", "Bantuan Sembako Sosial")
                    put("fotoKtp", "")
                    put("fotoKk", "")
                }
            )
            listWarga.forEach { db.insert("warga", SQLiteDatabase.CONFLICT_REPLACE, it) }

            // 3. Sample Announcement / Activities / Security
            val kegiatanList = listOf(
                ContentValues().apply {
                    put("judul", "Kerja Bakti Lingkungan RT 03")
                    put("deskripsi", "Pembersihan saluran drainase utama menjelang musim hujan dan penataan taman balai warga RT 03.")
                    put("tanggal", "Minggu, 24 Mei 2026")
                    put("fotoPath", "")
                    put("kategori", "KEGIATAN_RT")
                    put("status", "SELESAI")
                    put("senderName", "RT 03")
                },
                ContentValues().apply {
                    put("judul", "Patroli Siskamling Malam Hari")
                    put("deskripsi", "Pengaktifan kembali ronda malam teratur demi menjaga ketertiban lingkungan luar warga RT 03.")
                    put("tanggal", "Sabtu, 23 Mei 2026")
                    put("fotoPath", "")
                    put("kategori", "KEAMANAN_LINGKUNGAN")
                    put("status", "SELESAI")
                    put("senderName", "RT 03")
                },
                ContentValues().apply {
                    put("judul", "Penyaluran Beras Sejahtera")
                    put("deskripsi", "Pembagian sembako bulanan untuk warga penerima manfaat bantuan sosial kelurahan.")
                    put("tanggal", "Kamis, 21 Mei 2026")
                    put("fotoPath", "")
                    put("kategori", "BANTUAN")
                    put("status", "SELESAI")
                    put("senderName", "RT 03")
                }
            )
            kegiatanList.forEach { db.insert("kegiatan", SQLiteDatabase.CONFLICT_REPLACE, it) }

            // 4. Sample letters
            val suratList = listOf(
                ContentValues().apply {
                    put("nomorSurat", "001/SP/Kel.Btl-RT-03/V/2026")
                    put("registerNumber", "REG-20260520-001")
                    put("NIKPemohon", "6305011212850001")
                    put("namaPemohon", "Budi Hartono")
                    put("jenisSurat", "Surat Domisili")
                    put("keperluan", "Syarat melamar pekerjaan di PT Batulicin Coal")
                    put("tanggalSurat", "2026-05-20")
                    put("rtPembuat", "03")
                    put("rwPembuat", "01")
                    put("namaRt", "H. Ahmad Fauzi, S.E.")
                    put("status", "APPROVED")
                    put("linkPdf", "")
                    put("qrCodeContent", "CERT-DIGIBAT-001-BUDI")
                    put("ttdPath", "")
                    put("stempelPath", "")
                    put("ktpAttachment", "")
                    put("syaratAttachment", "")
                },
                ContentValues().apply {
                    put("nomorSurat", "002/SP/Kel.Btl-RT-03/V/2026")
                    put("registerNumber", "REG-20260522-002")
                    put("NIKPemohon", "6305014506920002")
                    put("namaPemohon", "Siti Aminah")
                    put("jenisSurat", "Surat Pengantar SKCK")
                    put("keperluan", "Pengurusan SKCK untuk mendaftar CPNS")
                    put("tanggalSurat", "2026-05-22")
                    put("rtPembuat", "03")
                    put("rwPembuat", "01")
                    put("namaRt", "H. Ahmad Fauzi, S.E.")
                    put("status", "APPROVED")
                    put("linkPdf", "")
                    put("qrCodeContent", "CERT-DIGIBAT-002-SITI")
                    put("ttdPath", "")
                    put("stempelPath", "")
                    put("ktpAttachment", "")
                    put("syaratAttachment", "")
                },
                ContentValues().apply {
                    put("nomorSurat", "003/SP/Kel.Btl-RT-03/V/2026")
                    put("registerNumber", "REG-20260525-003")
                    put("NIKPemohon", "6305011708780005")
                    put("namaPemohon", "Eko Prasetyo Utomo")
                    put("jenisSurat", "Surat Keterangan Tidak Mampu (SKTM)")
                    put("keperluan", "Pengajuan beasiswa sekolah anak")
                    put("tanggalSurat", "2026-05-25")
                    put("rtPembuat", "03")
                    put("rwPembuat", "01")
                    put("namaRt", "H. Ahmad Fauzi, S.E.")
                    put("status", "PENDING")
                    put("linkPdf", "")
                    put("qrCodeContent", "CERT-DIGIBAT-003-EKO")
                    put("ttdPath", "")
                    put("stempelPath", "")
                    put("ktpAttachment", "")
                    put("syaratAttachment", "")
                }
            )
            suratList.forEach { db.insert("surat", SQLiteDatabase.CONFLICT_REPLACE, it) }

            // 5. Initial Users
            val userList = listOf(
                ContentValues().apply {
                    put("username", "bono")
                    put("password", "Bono")
                    put("role", "SUPERADMIN")
                    put("name", "Bono (Superadmin)")
                },
                ContentValues().apply {
                    put("username", "admin")
                    put("password", "admin123")
                    put("role", "SUPERADMIN")
                },
                ContentValues().apply {
                    put("username", "operator")
                    put("password", "operator123")
                    put("role", "OPERATOR")
                    put("name", "Operator Batulicin")
                },
                ContentValues().apply {
                    put("username", "rt03")
                    put("password", "rt03-pass")
                    put("role", "RT")
                    put("rtNumber", "03")
                    put("name", "RT 03")
                }
            )
            userList.forEach { db.insert("users", SQLiteDatabase.CONFLICT_REPLACE, it) }
            android.util.Log.d("AppDatabase", "Database population complete.")
        }
    }
}
