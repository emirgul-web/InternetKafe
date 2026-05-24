package internetkafe.model;

public class Musteri {

    private int    musteriId;
    private String ad;
    private String soyad;
    private String telefon;
    private String sifre;
    private double bakiye;
    private boolean aktif;

    public Musteri(int musteriId, String ad, String soyad, String telefon, String sifre) {
        this.musteriId = musteriId;
        this.ad        = ad;
        this.soyad     = soyad;
        this.telefon   = telefon;
        this.sifre     = sifre;
        this.bakiye    = 0.0;
        this.aktif     = false;
    }

    public int    getMusteriId() { return musteriId; }
    public String getAd()        { return ad; }
    public String getSoyad()     { return soyad; }
    public String getTelefon()   { return telefon; }
    public String getSifre()     { return sifre; }
    public double getBakiye()    { return bakiye; }
    public boolean isAktif()     { return aktif; }

    public void setAd(String ad)           { this.ad = ad; }
    public void setSoyad(String soyad)     { this.soyad = soyad; }
    public void setTelefon(String telefon) { this.telefon = telefon; }
    public void setSifre(String sifre)     { this.sifre = sifre; }
    public void setAktif(boolean aktif)    { this.aktif = aktif; }

    public void bakiyeYukle(double miktar) {
        if (miktar <= 0) {
            System.out.println("⚠️  Yükleme miktarı 0'dan büyük olmalıdır.");
            return;
        }
        this.bakiye += miktar;
    }

    public boolean bakiyeDus(double miktar) {
        if (this.bakiye < miktar) {
            return false;
        }
        this.bakiye -= miktar;
        return true;
    }

    @Override
    public String toString() {
        return String.format(
            "[Müşteri #%d] %s %s | Tel: %s | Bakiye: %.2f TL | Durum: %s",
            musteriId, ad, soyad, telefon, bakiye,
            aktif ? "İÇERİDE" : "DIŞARIDA"
        );
    }
}
