package internetkafe.servis;

import internetkafe.model.Bilgisayar;
import internetkafe.model.Musteri;
import internetkafe.model.OdemeKaydi;
import internetkafe.model.OturumKaydi;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RaporServisi {

    private final MusteriServisi    musteriServisi;
    private final BilgisayarServisi bilgisayarServisi;
    private final OdemeServisi      odemeServisi;
    private final OrtamServisi      ortamServisi;
    private final VeritabaniServisi veritabaniServisi;

    public RaporServisi(MusteriServisi musteriServisi,
                        BilgisayarServisi bilgisayarServisi,
                        OdemeServisi odemeServisi,
                        OrtamServisi ortamServisi,
                        VeritabaniServisi veritabaniServisi) {
        this.musteriServisi    = musteriServisi;
        this.bilgisayarServisi = bilgisayarServisi;
        this.odemeServisi      = odemeServisi;
        this.ortamServisi      = ortamServisi;
        this.veritabaniServisi = veritabaniServisi;
    }

    public double getGunlukGelir() {
        return odemeServisi.getGunlukGelir();
    }

    public int getToplamOturumSayisi() {
        return bilgisayarServisi.tumOturumlariListele().size();
    }

    public int getAktifOturumSayisi() {
        return (int) bilgisayarServisi.tumOturumlariListele().stream()
                .filter(OturumKaydi::isAktif)
                .count();
    }

    public int getKapaliOturumSayisi() {
        return (int) bilgisayarServisi.tumOturumlariListele().stream()
                .filter(o -> !o.isAktif())
                .count();
    }

    public long getToplamKullanimDakikasi() {
        return bilgisayarServisi.tumOturumlariListele().stream()
                .filter(o -> !o.isAktif())
                .mapToLong(OturumKaydi::getSureDakika)
                .sum();
    }

    public double getOrtalamaOturumUcreti() {
        int kapali = getKapaliOturumSayisi();
        return kapali == 0 ? 0 : getGunlukGelir() / kapali;
    }

    public double getDolulukOrani() {
        List<Bilgisayar> tum = bilgisayarServisi.tumBilgisayarlariListele();
        if (tum.isEmpty()) return 0.0;
        long doluSayi = tum.stream().filter(pc -> !pc.isMusait()).count();
        return (double) doluSayi / tum.size() * 100;
    }

    public int getDoluBilgisayarSayisi() {
        return (int) bilgisayarServisi.tumBilgisayarlariListele().stream()
                .filter(pc -> !pc.isMusait())
                .count();
    }

    public int getBosBilgisayarSayisi() {
        return bilgisayarServisi.bosBilgisayarlariListele().size();
    }

    public int getKayitliMusteriSayisi() {
        return musteriServisi.tumMusterileriListele().size();
    }

    public int getAktifMusteriSayisi() {
        return musteriServisi.aktifMusterileriListele().size();
    }

    public List<Bilgisayar> tumBilgisayarlar() {
        return bilgisayarServisi.tumBilgisayarlariListele();
    }

    public List<OturumKaydi> tumOturumKayitlari() {
        return bilgisayarServisi.tumOturumlariListele();
    }

    public List<OdemeKaydi> tumOdemeKayitlari() {
        return odemeServisi.tumOdemeler();
    }

    public List<Musteri> tumMusteriler() {
        return musteriServisi.tumMusterileriListele();
    }

    public void gunlukRaporKaydet() {
        String tarih = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        double gelir    = getGunlukGelir();
        int    oturum   = getToplamOturumSayisi();
        long   sureDk   = getToplamKullanimDakikasi();

        String sqlCheck = "SELECT id FROM gunluk_rapor WHERE tarih = ?";
        String sqlUpdate = "UPDATE gunluk_rapor SET toplam_gelir = ?, toplam_oturum = ?, toplam_sure_dk = ? WHERE tarih = ?";
        String sqlInsert = "INSERT INTO gunluk_rapor (tarih, toplam_gelir, toplam_oturum, toplam_sure_dk) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement checkStmt = veritabaniServisi.getBaglanti().prepareStatement(sqlCheck);
            checkStmt.setString(1, tarih);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                PreparedStatement updateStmt = veritabaniServisi.getBaglanti().prepareStatement(sqlUpdate);
                updateStmt.setDouble(1, gelir);
                updateStmt.setInt(2, oturum);
                updateStmt.setLong(3, sureDk);
                updateStmt.setString(4, tarih);
                updateStmt.executeUpdate();
                updateStmt.close();
            } else {
                PreparedStatement insertStmt = veritabaniServisi.getBaglanti().prepareStatement(sqlInsert);
                insertStmt.setString(1, tarih);
                insertStmt.setDouble(2, gelir);
                insertStmt.setInt(3, oturum);
                insertStmt.setLong(4, sureDk);
                insertStmt.executeUpdate();
                insertStmt.close();
            }

            rs.close();
            checkStmt.close();

            System.out.printf("✅ D5: Günlük rapor kaydedildi → Tarih: %s | Gelir: %.2f TL | Oturum: %d | Süre: %d dk%n",
                tarih, gelir, oturum, sureDk);

        } catch (SQLException e) {
            System.err.println("D5 rapor kayıt hatası: " + e.getMessage());
        }
    }
}