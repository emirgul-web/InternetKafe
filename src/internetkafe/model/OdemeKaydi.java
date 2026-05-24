package internetkafe.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OdemeKaydi {

    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static final String TIP_NAKIT    = "Nakit";
    public static final String TIP_KART     = "Kart";
    public static final String TIP_BAKIYE   = "Bakiye";

    private int           odemeId;
    private int           oturumId;
    private int           musteriId;
    private double        tutar;
    private String        odemeTipi;
    private LocalDateTime odemeZamani;
    private boolean       odendi;

    public OdemeKaydi(int odemeId, int oturumId, int musteriId,
                      double tutar, String odemeTipi) {
        this.odemeId     = odemeId;
        this.oturumId    = oturumId;
        this.musteriId   = musteriId;
        this.tutar       = tutar;
        this.odemeTipi   = odemeTipi;
        this.odemeZamani = LocalDateTime.now();
        this.odendi      = true;
    }

    public int           getOdemeId()     { return odemeId; }
    public int           getOturumId()    { return oturumId; }
    public int           getMusteriId()   { return musteriId; }
    public double        getTutar()       { return tutar; }
    public String        getOdemeTipi()   { return odemeTipi; }
    public LocalDateTime getOdemeZamani() { return odemeZamani; }
    public boolean       isOdendi()       { return odendi; }

    @Override
    public String toString() {
        return String.format(
            "[Ödeme #%d] Müşteri: #%d | Oturum: #%d | %.2f TL (%s) | %s",
            odemeId, musteriId, oturumId,
            tutar, odemeTipi, odemeZamani.format(FORMATTER)
        );
    }
}
