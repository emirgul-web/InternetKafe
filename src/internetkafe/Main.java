package internetkafe;

import internetkafe.gui.GirisEkrani;
import internetkafe.servis.*;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VeritabaniServisi veritabaniS = new VeritabaniServisi();

            MusteriServisi    musteriS    = new MusteriServisi(veritabaniS);
            BilgisayarServisi bilgisayarS = new BilgisayarServisi(musteriS, veritabaniS);
            OdemeServisi      odemeS      = new OdemeServisi(musteriS, bilgisayarS, veritabaniS);
            OrtamServisi      ortamS      = new OrtamServisi(veritabaniS);

            RaporServisi      raporS  = new RaporServisi(musteriS, bilgisayarS, odemeS, ortamS, veritabaniS);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                veritabaniS.kapat();
            }));

            GirisEkrani giris = new GirisEkrani(musteriS, bilgisayarS, odemeS, ortamS, raporS);
            giris.setVisible(true);
        });
    }
}