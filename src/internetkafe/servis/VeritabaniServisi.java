package internetkafe.servis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class VeritabaniServisi {

    private static final String DB_URL = "jdbc:sqlite:internetkafe.db";
    private Connection baglanti;

    public VeritabaniServisi() {
        baglantiKur();
        tablolariOlustur();
        veritabaniGuncelle();
    }

    private void veritabaniGuncelle() {
        try (Statement stmt = baglanti.createStatement()) {
            try {
                stmt.executeQuery("SELECT sifre FROM musteriler LIMIT 1").close();
            } catch (SQLException e) {
                System.out.println("⚠️  D1: 'sifre' sütunu eksik, tablo güncelleniyor...");
                stmt.execute("ALTER TABLE musteriler ADD COLUMN sifre TEXT DEFAULT '123'");
                System.out.println("✅ D1: 'sifre' sütunu başarıyla eklendi.");
            }
        } catch (SQLException e) {
            System.err.println("Veritabanı güncelleme hatası: " + e.getMessage());
        }
    }

    private void baglantiKur() {
        try {
            Class.forName("org.sqlite.JDBC");
            baglanti = DriverManager.getConnection(DB_URL);
            try (Statement stmt = baglanti.createStatement()) {
                stmt.execute("PRAGMA journal_mode=WAL");
                stmt.execute("PRAGMA foreign_keys=ON");
            }
            System.out.println("✅ Veritabanı bağlantısı kuruldu (internetkafe.db)");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ SQLite JDBC sürücüsü bulunamadı: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("❌ Veritabanı bağlantı hatası: " + e.getMessage());
        }
    }

    private void tablolariOlustur() {
        try (Statement stmt = baglanti.createStatement()) {

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS musteriler (" +
                "  musteri_id    INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  ad            TEXT    NOT NULL," +
                "  soyad         TEXT    NOT NULL," +
                "  telefon       TEXT    UNIQUE NOT NULL," +
                "  sifre         TEXT    DEFAULT '123'," +
                "  bakiye        REAL    DEFAULT 0.0," +
                "  aktif         INTEGER DEFAULT 0" +
                ")"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS bilgisayarlar (" +
                "  bilgisayar_id    INTEGER PRIMARY KEY," +
                "  tip              TEXT    NOT NULL," +
                "  saatlik_ucret    REAL    NOT NULL," +
                "  musait           INTEGER DEFAULT 1," +
                "  aktif_musteri_id INTEGER DEFAULT -1" +
                ")"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS oturum_kayitlari (" +
                "  oturum_id        INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  musteri_id       INTEGER NOT NULL," +
                "  bilgisayar_id    INTEGER NOT NULL," +
                "  baslangic_zamani TEXT    NOT NULL," +
                "  bitis_zamani     TEXT," +
                "  aktif            INTEGER DEFAULT 1," +
                "  FOREIGN KEY (musteri_id)    REFERENCES musteriler(musteri_id)," +
                "  FOREIGN KEY (bilgisayar_id) REFERENCES bilgisayarlar(bilgisayar_id)" +
                ")"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS odeme_kayitlari (" +
                "  odeme_id      INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  oturum_id     INTEGER NOT NULL," +
                "  musteri_id    INTEGER NOT NULL," +
                "  tutar         REAL    NOT NULL," +
                "  odeme_tipi    TEXT    NOT NULL," +
                "  odeme_zamani  TEXT    NOT NULL," +
                "  odendi        INTEGER DEFAULT 1," +
                "  FOREIGN KEY (oturum_id)  REFERENCES oturum_kayitlari(oturum_id)," +
                "  FOREIGN KEY (musteri_id) REFERENCES musteriler(musteri_id)" +
                ")"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS ortam_verileri (" +
                "  id             INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  sicaklik       REAL    NOT NULL," +
                "  klima_durumu   TEXT    NOT NULL," +
                "  kayit_zamani   TEXT    NOT NULL" +
                ")"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS gunluk_rapor (" +
                "  id              INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  tarih           TEXT    NOT NULL," +
                "  toplam_gelir    REAL    DEFAULT 0.0," +
                "  toplam_oturum   INTEGER DEFAULT 0," +
                "  toplam_sure_dk  INTEGER DEFAULT 0" +
                ")"
            );

            System.out.println("✅ Veritabanı tabloları oluşturuldu (D1-D5)");

        } catch (SQLException e) {
            System.err.println("❌ Tablo oluşturma hatası: " + e.getMessage());
        }
    }

    public Connection getBaglanti() {
        return baglanti;
    }

    public void kapat() {
        try {
            if (baglanti != null && !baglanti.isClosed()) {
                baglanti.close();
                System.out.println("✅ Veritabanı bağlantısı kapatıldı.");
            }
        } catch (SQLException e) {
            System.err.println("Veritabanı kapatma hatası: " + e.getMessage());
        }
    }
}
