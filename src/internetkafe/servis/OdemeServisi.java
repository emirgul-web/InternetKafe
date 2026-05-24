package internetkafe.servis;

import internetkafe.model.Musteri;
import internetkafe.model.OdemeKaydi;
import internetkafe.model.OturumKaydi;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OdemeServisi {

    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private MusteriServisi    musteriServisi;
    private BilgisayarServisi bilgisayarServisi;
    private VeritabaniServisi veritabaniServisi;

    private List<OdemeKaydi> odemeler;
    private int              odemeIdSayaci;

    private double gunlukGelir;

    public OdemeServisi(MusteriServisi musteriServisi,
                        BilgisayarServisi bilgisayarServisi,
                        VeritabaniServisi veritabaniServisi) {
        this.musteriServisi    = musteriServisi;
        this.bilgisayarServisi = bilgisayarServisi;
        this.veritabaniServisi = veritabaniServisi;
        this.odemeler          = new ArrayList<>();
        this.odemeIdSayaci     = 1;
        this.gunlukGelir       = 0.0;

        veritabanindanYukle();
    }

    private void veritabanindanYukle() {
        String sql = "SELECT odeme_id, oturum_id, musteri_id, tutar, odeme_tipi, odeme_zamani FROM odeme_kayitlari ORDER BY odeme_id";
        try (Statement stmt = veritabaniServisi.getBaglanti().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int oId = rs.getInt("odeme_id");
                OdemeKaydi kayit = new OdemeKaydi(
                    oId,
                    rs.getInt("oturum_id"),
                    rs.getInt("musteri_id"),
                    rs.getDouble("tutar"),
                    rs.getString("odeme_tipi")
                );
                odemeler.add(kayit);
                gunlukGelir += rs.getDouble("tutar");

                if (oId >= odemeIdSayaci) {
                    odemeIdSayaci = oId + 1;
                }
            }
            System.out.println("✅ D3: " + odemeler.size() + " ödeme kaydı veritabanından yüklendi.");
        } catch (SQLException e) {
            System.err.println("D3 yükleme hatası: " + e.getMessage());
        }
    }

    public boolean odemeAl(int musteriId, String odemeTipi) {
        OturumKaydi oturum = bilgisayarServisi.aktifOturumGetir(musteriId);
        if (oturum == null) {
            System.out.println("❌ Bu müşterinin aktif oturumu yok!");
            return false;
        }

        int oturumId = oturum.getOturumId();

        double tutar = bilgisayarServisi.ucretHesapla(musteriId);
        if (tutar < 0) {
            return false;
        }

        Musteri musteri = musteriServisi.idIleAra(musteriId);
        if (musteri == null) {
            System.out.println("❌ Müşteri bulunamadı!");
            return false;
        }

        boolean odemeOnayi = false;
        switch (odemeTipi) {
            case OdemeKaydi.TIP_BAKIYE:
                if (musteri.getBakiye() < tutar) {
                    System.out.printf("⚠️  Yetersiz bakiye! Gereken: %.2f TL | Mevcut: %.2f TL%n",
                        tutar, musteri.getBakiye());
                    return false;
                }
                odemeOnayi = true;
                break;
            case OdemeKaydi.TIP_NAKIT:
            case OdemeKaydi.TIP_KART:
                odemeOnayi = true;
                break;
            default:
                System.out.println("❌ Geçersiz ödeme tipi!");
                return false;
        }

        if (odemeOnayi) {
            if (odemeTipi.equals(OdemeKaydi.TIP_BAKIYE)) {
                musteri.bakiyeDus(tutar);
            }

            bilgisayarServisi.oturumKapat(musteriId);

            OdemeKaydi kayit = new OdemeKaydi(
                odemeIdSayaci++,
                oturumId,
                musteriId,
                tutar,
                odemeTipi
            );
            odemeler.add(kayit);
            gunlukGelir += tutar;

            odemeDBKaydet(kayit);

            System.out.printf("✅ Ödeme tamamlandı! (%s) | Tutar: %.2f TL%n",
                odemeTipi, tutar);

            musteriServisi.musteriCikis(musteriId);
            return true;
        }

        return false;
    }

    public List<OdemeKaydi> tumOdemeler() {
        return new ArrayList<>(odemeler);
    }

    public double getGunlukGelir() {
        return gunlukGelir;
    }

    public void gunuSifirla() {
        this.gunlukGelir = 0.0;
        System.out.println("🔄 Günlük sayaç sıfırlandı.");
    }

    private void odemeDBKaydet(OdemeKaydi kayit) {
        String sql = "INSERT INTO odeme_kayitlari (odeme_id, oturum_id, musteri_id, tutar, odeme_tipi, odeme_zamani, odendi) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = veritabaniServisi.getBaglanti().prepareStatement(sql)) {
            pstmt.setInt(1, kayit.getOdemeId());
            pstmt.setInt(2, kayit.getOturumId());
            pstmt.setInt(3, kayit.getMusteriId());
            pstmt.setDouble(4, kayit.getTutar());
            pstmt.setString(5, kayit.getOdemeTipi());
            pstmt.setString(6, kayit.getOdemeZamani().format(FORMATTER));
            pstmt.setInt(7, kayit.isOdendi() ? 1 : 0);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("D3 kayıt hatası: " + e.getMessage());
        }
    }
}
