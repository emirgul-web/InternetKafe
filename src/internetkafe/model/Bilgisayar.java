package internetkafe.model;

public class Bilgisayar {

    public static final double VARSAYILAN_SAATLIK_UCRET = 10.0;

    private int     bilgisayarId;
    private String  tip;
    private boolean musait;
    private double  saatlikUcret;
    private int     aktifMusteriId;

    public Bilgisayar(int bilgisayarId, String tip, double saatlikUcret) {
        this.bilgisayarId   = bilgisayarId;
        this.tip            = tip;
        this.saatlikUcret   = saatlikUcret;
        this.musait         = true;
        this.aktifMusteriId = -1;
    }

    public Bilgisayar(int bilgisayarId, String tip) {
        this(bilgisayarId, tip, VARSAYILAN_SAATLIK_UCRET);
    }

    public int    getBilgisayarId()   { return bilgisayarId; }
    public String getTip()            { return tip; }
    public boolean isMusait()         { return musait; }
    public double getSaatlikUcret()   { return saatlikUcret; }
    public int    getAktifMusteriId() { return aktifMusteriId; }

    public boolean musteriAta(int musteriId) {
        if (!musait) {
            System.out.println("⚠️  Bilgisayar " + bilgisayarId + " zaten dolu!");
            return false;
        }
        this.aktifMusteriId = musteriId;
        this.musait         = false;
        return true;
    }

    public void serbestBirak() {
        this.aktifMusteriId = -1;
        this.musait         = true;
    }

    @Override
    public String toString() {
        return String.format(
            "[PC #%d] Tip: %-10s | Ücret: %.0f TL/saat | Durum: %s",
            bilgisayarId, tip, saatlikUcret,
            musait ? "✅ BOŞ" : "🔴 DOLU (Müşteri #" + aktifMusteriId + ")"
        );
    }
}
