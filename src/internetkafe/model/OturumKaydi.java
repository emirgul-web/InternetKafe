package internetkafe.model;

import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

public class OturumKaydi {

    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private int           oturumId;
    private int           musteriId;
    private int           bilgisayarId;
    private int           paketDk;
    private LocalDateTime baslangicZamani;
    private LocalDateTime bitisZamani;
    private boolean       aktif;

    public OturumKaydi(int oturumId, int musteriId, int bilgisayarId) {
        this(oturumId, musteriId, bilgisayarId, 0);
    }

    public OturumKaydi(int oturumId, int musteriId, int bilgisayarId, int paketDk) {
        this.oturumId        = oturumId;
        this.musteriId       = musteriId;
        this.bilgisayarId    = bilgisayarId;
        this.paketDk         = paketDk;
        this.baslangicZamani = LocalDateTime.now();
        this.bitisZamani     = null;
        this.aktif           = true;
    }

    public int           getOturumId()        { return oturumId; }
    public int           getMusteriId()       { return musteriId; }
    public int           getBilgisayarId()    { return bilgisayarId; }
    public int           getPaketDk()         { return paketDk; }
    public LocalDateTime getBaslangicZamani() { return baslangicZamani; }
    public LocalDateTime getBitisZamani()     { return bitisZamani; }
    public boolean       isAktif()            { return aktif; }

    public void oturumKapat() {
        this.bitisZamani = LocalDateTime.now();
        this.aktif       = false;
    }

    public long getSureDakika() {
        LocalDateTime bitis = (bitisZamani != null) ? bitisZamani : LocalDateTime.now();
        Duration sure = Duration.between(baslangicZamani, bitis);
        return sure.toMinutes();
    }

    public double getSureSaat() {
        return getSureDakika() / 60.0;
    }

    @Override
    public String toString() {
        String bitis = (bitisZamani != null)
            ? bitisZamani.format(FORMATTER)
            : "Devam ediyor...";

        return String.format(
            "[Oturum #%d] Müşteri: #%d | PC: #%d | Başlangıç: %s | Bitiş: %s | Süre: %d dk",
            oturumId, musteriId, bilgisayarId,
            baslangicZamani.format(FORMATTER), bitis, getSureDakika()
        );
    }
}
