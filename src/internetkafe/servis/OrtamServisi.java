package internetkafe.servis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class OrtamServisi {

    private static final double ESIK_SICAKLIK  = 24.0;
    private static final double HISTEREZ       = 2.0;
    private static final double MIN_SICAKLIK   = 18.0;
    private static final double MAX_SICAKLIK   = 35.0;

    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private double  anlikSicaklik;
    private boolean klimaAcik;
    private int     klimaDegisimSayisi;
    private Random  rastgele;

    private ArduinoOkuyucu arduinoOkuyucu;
    private boolean arduinoAktif = false;

    private VeritabaniServisi veritabaniServisi;

    public OrtamServisi(VeritabaniServisi veritabaniServisi) {
        this.veritabaniServisi = veritabaniServisi;
        this.rastgele             = new Random();
        this.klimaAcik            = false;
        this.klimaDegisimSayisi   = 0;
        this.anlikSicaklik        = 22.0;

        try {
            arduinoOkuyucu = new ArduinoOkuyucu();
            arduinoOkuyucu.baglan("COM3");
            arduinoAktif = arduinoOkuyucu.isBagli();
            if (arduinoAktif) {
                System.out.println("✅ Gerçek sensör verileri kullanılıyor.");
            } else {
                System.out.println("ℹ️  Simülasyon modu aktif (Arduino bağlı değil).");
            }
        } catch (Throwable t) {
            arduinoOkuyucu = null;
            arduinoAktif   = false;
            System.out.println("ℹ️  Simülasyon modu aktif (jSerialComm yüklenemedi: "
                               + t.getClass().getSimpleName() + ").");
        }
    }

    public double sicaklikOku() {
        if (arduinoAktif && arduinoOkuyucu != null) {
            try {
                if (arduinoOkuyucu.isBagli()) {
                    anlikSicaklik = arduinoOkuyucu.getSonSicaklik();
                    anlikSicaklik = Math.max(MIN_SICAKLIK, Math.min(MAX_SICAKLIK, anlikSicaklik));
                    ortamVerisiKaydet();
                    return anlikSicaklik;
                }
            } catch (Throwable t) {
                arduinoAktif = false;
                System.err.println("Arduino okuma hatası, simülasyona geçildi: " + t.getMessage());
            }
        }

        double degisim = (rastgele.nextDouble() * 3.0) - 1.5;
        anlikSicaklik += degisim;
        anlikSicaklik  = Math.max(MIN_SICAKLIK, Math.min(MAX_SICAKLIK, anlikSicaklik));
        anlikSicaklik  = Math.round(anlikSicaklik * 10.0) / 10.0;

        ortamVerisiKaydet();

        return anlikSicaklik;
    }

    public void otomatikKlimaKontrol() {
        double sicaklik = sicaklikOku();
        String oncekiDurum = klimaAcik ? "AÇIK" : "KAPALI";

        if (!klimaAcik && sicaklik > ESIK_SICAKLIK) {
            klimaAcik = true;
            klimaDegisimSayisi++;
            System.out.printf("🌡️  Sıcaklık: %.1f°C → Klima AÇILDI (%d. değişim)%n",
                sicaklik, klimaDegisimSayisi);
        } else if (klimaAcik && sicaklik < (ESIK_SICAKLIK - HISTEREZ)) {
            klimaAcik = false;
            klimaDegisimSayisi++;
            System.out.printf("❄️  Sıcaklık: %.1f°C → Klima KAPATILDI (%d. değişim)%n",
                sicaklik, klimaDegisimSayisi);
        } else {
            System.out.printf("🌡️  Sıcaklık: %.1f°C → Klima: %s (değişim yok)%n",
                sicaklik, oncekiDurum);
        }
    }

    public void klimaManuelKontrol(boolean ac) {
        if (this.klimaAcik == ac) {
            System.out.println("ℹ️  Klima zaten " + (ac ? "açık." : "kapalı."));
            return;
        }
        this.klimaAcik = ac;
        klimaDegisimSayisi++;
        System.out.println("🔧 Klima manuel olarak " + (ac ? "AÇILDI." : "KAPATILDI."));
    }

    public double  getAnlikSicaklik()      { return anlikSicaklik; }
    public boolean isKlimaAcik()           { return klimaAcik; }
    public int     getKlimaDegisimSayisi() { return klimaDegisimSayisi; }

    private void ortamVerisiKaydet() {
        String sql = "INSERT INTO ortam_verileri (sicaklik, klima_durumu, kayit_zamani) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = veritabaniServisi.getBaglanti().prepareStatement(sql)) {
            pstmt.setDouble(1, anlikSicaklik);
            pstmt.setString(2, klimaAcik ? "AÇIK" : "KAPALI");
            pstmt.setString(3, LocalDateTime.now().format(FORMATTER));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("D4 kayıt hatası: " + e.getMessage());
        }
    }
}