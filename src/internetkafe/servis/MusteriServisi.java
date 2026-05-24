package internetkafe.servis;

import internetkafe.model.Musteri;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusteriServisi {

    private Map<Integer, Musteri> musteriler;
    private VeritabaniServisi veritabaniServisi;

    public MusteriServisi(VeritabaniServisi veritabaniServisi) {
        this.veritabaniServisi = veritabaniServisi;
        this.musteriler = new HashMap<>();
        veritabanindanYukle();
        tumAktifDurumuSifirla();
        if (musteriler.isEmpty()) {
            ornek_verileriYukle();
        }
    }

    private void veritabanindanYukle() {
        String sql = "SELECT musteri_id, ad, soyad, telefon, sifre, bakiye, aktif FROM musteriler";
        try (Statement stmt = veritabaniServisi.getBaglanti().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("musteri_id");
                Musteri m = new Musteri(id, rs.getString("ad"),
                    rs.getString("soyad"), rs.getString("telefon"), rs.getString("sifre"));
                double bakiye = rs.getDouble("bakiye");
                if (bakiye > 0) m.bakiyeYukle(bakiye);
                m.setAktif(rs.getInt("aktif") == 1);
                musteriler.put(id, m);
            }
            System.out.println("✅ D1: " + musteriler.size() + " müşteri veritabanından yüklendi.");
        } catch (SQLException e) {
            System.err.println("D1 yükleme hatası: " + e.getMessage());
        }
    }

    private void tumAktifDurumuSifirla() {
        List<Integer> aktifOturumMusteriIdleri = new ArrayList<>();
        String sql = "SELECT musteri_id FROM oturum_kayitlari WHERE aktif = 1";
        try (Statement stmt = veritabaniServisi.getBaglanti().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                aktifOturumMusteriIdleri.add(rs.getInt("musteri_id"));
            }
        } catch (SQLException e) {
            System.err.println("Aktif oturumları kontrol ederken hata: " + e.getMessage());
        }

        for (Musteri m : musteriler.values()) {
            if (m.isAktif() && !aktifOturumMusteriIdleri.contains(m.getMusteriId())) {
                m.setAktif(false);
                musteriDurumGuncelle(m);
            } else if (!m.isAktif() && aktifOturumMusteriIdleri.contains(m.getMusteriId())) {
                m.setAktif(true);
                musteriDurumGuncelle(m);
            }
        }
    }

    private void ornek_verileriYukle() {
        musteriOlustur("Ahmet", "Yılmaz", "05301234567", "123");
        musteriOlustur("Fatma", "Kaya",   "05361234567", "123");
        musteriOlustur("Mehmet", "Demir", "05421234567", "123");

        Musteri ahmet = adIleAra("Ahmet");
        if (ahmet != null) {
            bakiyeYukle(ahmet.getMusteriId(), 50.0);
        }
    }

    public Musteri musteriOlustur(String ad, String soyad, String telefon, String sifre) {
        for (Musteri m : musteriler.values()) {
            if (m.getTelefon().equals(telefon)) {
                System.out.println("⚠️  Bu telefon numarası zaten kayıtlı!");
                return null;
            }
        }

        String sql = "INSERT INTO musteriler (ad, soyad, telefon, sifre, bakiye, aktif) VALUES (?, ?, ?, ?, 0.0, 0)";
        try (PreparedStatement pstmt = veritabaniServisi.getBaglanti().prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, ad);
            pstmt.setString(2, soyad);
            pstmt.setString(3, telefon);
            pstmt.setString(4, sifre);
            pstmt.executeUpdate();

            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) {
                int yeniId = keys.getInt(1);
                Musteri yeniMusteri = new Musteri(yeniId, ad, soyad, telefon, sifre);
                musteriler.put(yeniId, yeniMusteri);
                System.out.println("✅ Müşteri oluşturuldu: " + yeniMusteri);
                return yeniMusteri;
            }
        } catch (SQLException e) {
            System.err.println("D1 ekleme hatası: " + e.getMessage());
        }
        return null;
    }

    public Musteri musteriGiris(String telefon, String sifre) {
        Musteri musteri = telefonIleAra(telefon);

        if (musteri == null) {
            System.out.println("❌ Bu telefon numarasına kayıtlı müşteri bulunamadı.");
            return null;
        }

        if (!musteri.getSifre().equals(sifre)) {
            System.out.println("❌ Hatalı şifre!");
            return null;
        }
        
        if (!musteri.isAktif()) {
            musteri.setAktif(true);
            musteriDurumGuncelle(musteri);
        }
        
        System.out.println("✅ Giriş başarılı: " + musteri.getAd() + " " + musteri.getSoyad());
        return musteri;
    }

    public Musteri adIleGiris(String ad) {
        Musteri musteri = adIleAra(ad);

        if (musteri == null) {
            System.err.println("❌ Bu isme kayıtlı müşteri bulunamadı.");
            return null;
        }

        if (!musteri.isAktif()) {
            musteri.setAktif(true);
            musteriDurumGuncelle(musteri);
        }

        System.out.println("✅ Giriş başarılı: " + musteri.getAd() + " " + musteri.getSoyad());
        return musteri;
    }

    private Musteri adIleAra(String ad) {
        for (Musteri m : musteriler.values()) {
            if (m.getAd().equalsIgnoreCase(ad)) {
                return m;
            }
        }
        return null;
    }

    public boolean musteriCikis(int musteriId) {
        Musteri musteri = idIleAra(musteriId);

        if (musteri == null) {
            System.out.println("❌ Müşteri bulunamadı (ID: " + musteriId + ")");
            return false;
        }

        musteri.setAktif(false);
        musteriDurumGuncelle(musteri);
        System.out.println("👋 Çıkış yapıldı: " + musteri.getAd() + " " + musteri.getSoyad());
        return true;
    }

    public void bakiyeYukle(int musteriId, double miktar) {
        Musteri musteri = idIleAra(musteriId);
        if (musteri != null) {
            musteri.bakiyeYukle(miktar);
            musteriDurumGuncelle(musteri);
            System.out.printf("💰 %.2f TL yüklendi. Yeni bakiye: %.2f TL%n",
                miktar, musteri.getBakiye());
        }
    }

    public Musteri idIleAra(int musteriId) {
        return musteriler.get(musteriId);
    }

    private Musteri telefonIleAra(String telefon) {
        for (Musteri m : musteriler.values()) {
            if (m.getTelefon().equals(telefon)) {
                return m;
            }
        }
        return null;
    }

    public List<Musteri> tumMusterileriListele() {
        return new ArrayList<>(musteriler.values());
    }

    public List<Musteri> aktifMusterileriListele() {
        List<Musteri> aktifler = new ArrayList<>();
        for (Musteri m : musteriler.values()) {
            if (m.isAktif()) {
                aktifler.add(m);
            }
        }
        return aktifler;
    }

    private void musteriDurumGuncelle(Musteri m) {
        String sql = "UPDATE musteriler SET bakiye = ?, aktif = ? WHERE musteri_id = ?";
        try (PreparedStatement pstmt = veritabaniServisi.getBaglanti().prepareStatement(sql)) {
            pstmt.setDouble(1, m.getBakiye());
            pstmt.setInt(2, m.isAktif() ? 1 : 0);
            pstmt.setInt(3, m.getMusteriId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("D1 güncelleme hatası: " + e.getMessage());
        }
    }
}
