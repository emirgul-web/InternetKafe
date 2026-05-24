package internetkafe.servis;

import internetkafe.model.Bilgisayar;
import internetkafe.model.Musteri;
import internetkafe.model.OturumKaydi;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BilgisayarServisi {

    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Map<Integer, Bilgisayar>  bilgisayarlar;
    private Map<Integer, OturumKaydi> aktifOturumlar;
    private List<OturumKaydi>         tumOturumlar;
    private MusteriServisi            musteriServisi;
    private VeritabaniServisi         veritabaniServisi;

    private int oturumIdSayaci;

    public BilgisayarServisi(MusteriServisi musteriServisi, VeritabaniServisi veritabaniServisi) {
        this.musteriServisi    = musteriServisi;
        this.veritabaniServisi = veritabaniServisi;
        this.bilgisayarlar   = new HashMap<>();
        this.aktifOturumlar  = new HashMap<>();
        this.tumOturumlar    = new ArrayList<>();
        this.oturumIdSayaci  = 1;

        bilgisayarlariYukle();
        oturumlariYukle();
    }

    private void bilgisayarlariYukle() {
        String sql = "SELECT bilgisayar_id, tip, saatlik_ucret, musait, aktif_musteri_id FROM bilgisayarlar";
        try (Statement stmt = veritabaniServisi.getBaglanti().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("bilgisayar_id");
                Bilgisayar pc = new Bilgisayar(id, rs.getString("tip"), rs.getDouble("saatlik_ucret"));
                int musteriId = rs.getInt("aktif_musteri_id");
                if (musteriId > 0 && rs.getInt("musait") == 0) {
                    pc.musteriAta(musteriId);
                }
                bilgisayarlar.put(id, pc);
            }
        } catch (SQLException e) {
            System.err.println("D2 bilgisayar yükleme hatası: " + e.getMessage());
        }

        if (bilgisayarlar.isEmpty()) {
            bilgisayarlariKur();
        } else {
            System.out.println("✅ D2: " + bilgisayarlar.size() + " bilgisayar veritabanından yüklendi.");
        }
    }

    private void oturumlariYukle() {
        String sql = "SELECT oturum_id, musteri_id, bilgisayar_id, baslangic_zamani, bitis_zamani, aktif " +
                     "FROM oturum_kayitlari ORDER BY oturum_id";
        try (Statement stmt = veritabaniServisi.getBaglanti().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int oId = rs.getInt("oturum_id");
                int mId = rs.getInt("musteri_id");
                int bId = rs.getInt("bilgisayar_id");
                OturumKaydi oturum = new OturumKaydi(oId, mId, bId);

                boolean aktif = rs.getInt("aktif") == 1;
                if (!aktif) {
                    oturum.oturumKapat();
                }

                tumOturumlar.add(oturum);
                if (aktif) {
                    aktifOturumlar.put(mId, oturum);
                }

                if (oId >= oturumIdSayaci) {
                    oturumIdSayaci = oId + 1;
                }
            }
            System.out.println("✅ D2: " + tumOturumlar.size() + " oturum kaydı veritabanından yüklendi.");
        } catch (SQLException e) {
            System.err.println("D2 oturum yükleme hatası: " + e.getMessage());
        }
    }

    private void bilgisayarlariKur() {
        for (int i = 1; i <= 5; i++) {
            Bilgisayar pc = new Bilgisayar(i, "Normal", 10.0);
            bilgisayarlar.put(i, pc);
            bilgisayarDBKaydet(pc);
        }
        for (int i = 6; i <= 8; i++) {
            Bilgisayar pc = new Bilgisayar(i, "Gaming", 20.0);
            bilgisayarlar.put(i, pc);
            bilgisayarDBKaydet(pc);
        }
        System.out.println("✅ D2: 8 bilgisayar veritabanına kaydedildi.");
    }

    public OturumKaydi oturumBaslat(int musteriId, int bilgisayarId, int paketDk) {
        Musteri musteri = musteriServisi.idIleAra(musteriId);
        if (musteri == null) {
            System.out.println("❌ Müşteri bulunamadı (ID: " + musteriId + ")");
            return null;
        }
        if (!musteri.isAktif()) {
            System.out.println("⚠️  Müşteri giriş yapmamış! Önce giriş yapın.");
            return null;
        }

        if (aktifOturumlar.containsKey(musteriId)) {
            System.out.println("⚠️  Bu müşterinin zaten aktif oturumu var!");
            return null;
        }

        Bilgisayar pc;
        if (bilgisayarId == -1) {
            pc = ilkBosPC();
        } else {
            pc = bilgisayarlar.get(bilgisayarId);
        }

        if (pc == null) {
            System.out.println("❌ Bilgisayar bulunamadı!");
            return null;
        }
        if (!pc.isMusait()) {
            System.out.println("❌ Seçilen bilgisayar dolu!");
            return null;
        }

        pc.musteriAta(musteriId);
        bilgisayarDurumGuncelle(pc);

        OturumKaydi oturum = new OturumKaydi(oturumIdSayaci++, musteriId, pc.getBilgisayarId(), paketDk);
        aktifOturumlar.put(musteriId, oturum);
        tumOturumlar.add(oturum);
        oturumDBKaydet(oturum);

        System.out.printf("✅ Oturum açıldı → Müşteri #%d | PC #%d (%s) | %.0f TL/saat%n",
            musteriId, pc.getBilgisayarId(), pc.getTip(), pc.getSaatlikUcret());

        return oturum;
    }

    public double ucretHesapla(int musteriId) {
        OturumKaydi oturum = aktifOturumlar.get(musteriId);
        if (oturum == null) return -1;

        Bilgisayar pc = bilgisayarlar.get(oturum.getBilgisayarId());
        double sure = oturum.getSureSaat();

        if (oturum.getPaketDk() > 0) {
            sure = oturum.getPaketDk() / 60.0;
        }

        double ucret = sure * (pc != null ? pc.getSaatlikUcret() : 10.0);
        if (ucret < 1.0 && oturum.getSureDakika() > 0) ucret = 1.0;

        return ucret;
    }

    public double oturumKapat(int musteriId) {
        OturumKaydi oturum = aktifOturumlar.get(musteriId);

        if (oturum == null) {
            System.out.println("❌ Bu müşterinin aktif oturumu yok!");
            return -1;
        }

        double ucret = ucretHesapla(musteriId);

        Bilgisayar pc = bilgisayarlar.get(oturum.getBilgisayarId());
        if (pc != null) {
            pc.serbestBirak();
            bilgisayarDurumGuncelle(pc);
        }

        oturum.oturumKapat();
        aktifOturumlar.remove(musteriId);
        oturumDurumGuncelle(oturum);

        System.out.printf("⏹️  Oturum kapatıldı → Süre: %d dk | Ücret: %.2f TL%n",
            oturum.getSureDakika(), ucret);

        return ucret;
    }

    public OturumKaydi aktifOturumGetir(int musteriId) {
        return aktifOturumlar.get(musteriId);
    }

    public List<OturumKaydi> tumOturumlariListele() {
        return new ArrayList<>(tumOturumlar);
    }

    public List<Bilgisayar> tumBilgisayarlariListele() {
        return new ArrayList<>(bilgisayarlar.values());
    }

    public List<Bilgisayar> bosBilgisayarlariListele() {
        List<Bilgisayar> boslar = new ArrayList<>();
        for (Bilgisayar pc : bilgisayarlar.values()) {
            if (pc.isMusait()) boslar.add(pc);
        }
        return boslar;
    }

    private Bilgisayar ilkBosPC() {
        for (Bilgisayar pc : bilgisayarlar.values()) {
            if (pc.isMusait()) return pc;
        }
        return null;
    }

    private void bilgisayarDBKaydet(Bilgisayar pc) {
        String sql = "INSERT OR REPLACE INTO bilgisayarlar (bilgisayar_id, tip, saatlik_ucret, musait, aktif_musteri_id) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = veritabaniServisi.getBaglanti().prepareStatement(sql)) {
            pstmt.setInt(1, pc.getBilgisayarId());
            pstmt.setString(2, pc.getTip());
            pstmt.setDouble(3, pc.getSaatlikUcret());
            pstmt.setInt(4, pc.isMusait() ? 1 : 0);
            pstmt.setInt(5, pc.getAktifMusteriId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("D2 bilgisayar kayıt hatası: " + e.getMessage());
        }
    }

    private void bilgisayarDurumGuncelle(Bilgisayar pc) {
        String sql = "UPDATE bilgisayarlar SET musait = ?, aktif_musteri_id = ? WHERE bilgisayar_id = ?";
        try (PreparedStatement pstmt = veritabaniServisi.getBaglanti().prepareStatement(sql)) {
            pstmt.setInt(1, pc.isMusait() ? 1 : 0);
            pstmt.setInt(2, pc.getAktifMusteriId());
            pstmt.setInt(3, pc.getBilgisayarId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("D2 bilgisayar güncelleme hatası: " + e.getMessage());
        }
    }

    private void oturumDBKaydet(OturumKaydi oturum) {
        String sql = "INSERT INTO oturum_kayitlari (oturum_id, musteri_id, bilgisayar_id, baslangic_zamani, bitis_zamani, aktif) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = veritabaniServisi.getBaglanti().prepareStatement(sql)) {
            pstmt.setInt(1, oturum.getOturumId());
            pstmt.setInt(2, oturum.getMusteriId());
            pstmt.setInt(3, oturum.getBilgisayarId());
            pstmt.setString(4, oturum.getBaslangicZamani().format(FORMATTER));
            pstmt.setString(5, oturum.getBitisZamani() != null ? oturum.getBitisZamani().format(FORMATTER) : null);
            pstmt.setInt(6, oturum.isAktif() ? 1 : 0);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("D2 oturum kayıt hatası: " + e.getMessage());
        }
    }

    private void oturumDurumGuncelle(OturumKaydi oturum) {
        String sql = "UPDATE oturum_kayitlari SET bitis_zamani = ?, aktif = ? WHERE oturum_id = ?";
        try (PreparedStatement pstmt = veritabaniServisi.getBaglanti().prepareStatement(sql)) {
            pstmt.setString(1, oturum.getBitisZamani() != null ? oturum.getBitisZamani().format(FORMATTER) : null);
            pstmt.setInt(2, oturum.isAktif() ? 1 : 0);
            pstmt.setInt(3, oturum.getOturumId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("D2 oturum güncelleme hatası: " + e.getMessage());
        }
    }
}
