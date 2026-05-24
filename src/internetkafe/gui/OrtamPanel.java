package internetkafe.gui;

import internetkafe.servis.OrtamServisi;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Arc2D;

public class OrtamPanel extends JPanel {
	private static final long serialVersionUID = 1L;
    private final OrtamServisi ortamServisi;
    private static final int REFRESH_MS = 5000;

    private JLabel   sicaklikLabel;
    private JLabel   klimaDurumLabel;
    private JLabel   degisimLabel;
    private JLabel   eşikLabel;
    private TermometerPanel termometre;
    private javax.swing.Timer otomatikTimer;

    public OrtamPanel(OrtamServisi ortamServisi) {
        this.ortamServisi = ortamServisi;
        setLayout(new BorderLayout(20, 20));
        setBackground(UIConstants.CONTENT_BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(headerOlustur(),  BorderLayout.NORTH);
        add(merkez(),         BorderLayout.CENTER);
        add(butonlar(),       BorderLayout.SOUTH);

        ekraniGuncelle();
        otomatikTimerKur();
    }

    private JPanel headerOlustur() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UIConstants.CONTENT_BG);
        p.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel baslik = new JLabel("🌡️  Ortam Kontrolü");
        baslik.setFont(UIConstants.FONT_TITLE);
        baslik.setForeground(UIConstants.TEXT_PRIMARY);

        JLabel alt = new JLabel("Sıcaklık sensörü 5 saniyede bir otomatik güncellenir");
        alt.setFont(UIConstants.FONT_SMALL);
        alt.setForeground(UIConstants.TEXT_SECONDARY);

        p.add(baslik, BorderLayout.WEST);
        p.add(alt,    BorderLayout.EAST);
        return p;
    }

    private JPanel merkez() {
        JPanel p = new JPanel(new GridLayout(1, 3, 20, 0));
        p.setBackground(UIConstants.CONTENT_BG);

        p.add(sicaklikKarti());
        p.add(klimaKarti());
        p.add(bilgiKarti());
        return p;
    }

    private JPanel sicaklikKarti() {
        JPanel kart = beyazKart();

        termometre = new TermometerPanel();
        termometre.setPreferredSize(new Dimension(160, 160));
        termometre.setAlignmentX(Component.CENTER_ALIGNMENT);

        sicaklikLabel = new JLabel("-- °C", SwingConstants.CENTER);
        sicaklikLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        sicaklikLabel.setForeground(UIConstants.TEXT_PRIMARY);
        sicaklikLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel alt = new JLabel("Anlık Sıcaklık", SwingConstants.CENTER);
        alt.setFont(UIConstants.FONT_SMALL);
        alt.setForeground(UIConstants.TEXT_SECONDARY);
        alt.setAlignmentX(Component.CENTER_ALIGNMENT);

        kart.add(Box.createVerticalStrut(15));
        kart.add(termometre);
        kart.add(Box.createVerticalStrut(10));
        kart.add(sicaklikLabel);
        kart.add(Box.createVerticalStrut(4));
        kart.add(alt);
        kart.add(Box.createVerticalGlue());
        return kart;
    }

    private JPanel klimaKarti() {
        JPanel kart = beyazKart();

        JLabel ikon = new JLabel("❄️", SwingConstants.CENTER);
        ikon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        ikon.setAlignmentX(Component.CENTER_ALIGNMENT);

        klimaDurumLabel = new JLabel("KAPALI", SwingConstants.CENTER);
        klimaDurumLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        klimaDurumLabel.setForeground(UIConstants.TEXT_SECONDARY);
        klimaDurumLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel alt = new JLabel("Klima Durumu", SwingConstants.CENTER);
        alt.setFont(UIConstants.FONT_SMALL);
        alt.setForeground(UIConstants.TEXT_SECONDARY);
        alt.setAlignmentX(Component.CENTER_ALIGNMENT);

        kart.add(Box.createVerticalStrut(20));
        kart.add(ikon);
        kart.add(Box.createVerticalStrut(10));
        kart.add(klimaDurumLabel);
        kart.add(Box.createVerticalStrut(4));
        kart.add(alt);
        kart.add(Box.createVerticalGlue());
        return kart;
    }

    private JPanel bilgiKarti() {
        JPanel kart = beyazKart();

        degisimLabel = new JLabel("0", SwingConstants.CENTER);
        degisimLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        degisimLabel.setForeground(UIConstants.HEADER_BG);
        degisimLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lb1 = new JLabel("Klima Değişim Sayısı", SwingConstants.CENTER);
        lb1.setFont(UIConstants.FONT_SMALL);
        lb1.setForeground(UIConstants.TEXT_SECONDARY);
        lb1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(new Color(230, 235, 245));

        eşikLabel = new JLabel("Eşik: 24.0 °C | Histerez: 2.0°", SwingConstants.CENTER);
        eşikLabel.setFont(UIConstants.FONT_SMALL);
        eşikLabel.setForeground(UIConstants.TEXT_SECONDARY);
        eşikLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        kart.add(Box.createVerticalStrut(20));
        kart.add(degisimLabel);
        kart.add(Box.createVerticalStrut(6));
        kart.add(lb1);
        kart.add(Box.createVerticalStrut(15));
        kart.add(sep);
        kart.add(Box.createVerticalStrut(12));
        kart.add(eşikLabel);
        kart.add(Box.createVerticalGlue());
        return kart;
    }

    private JPanel butonlar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        p.setBackground(UIConstants.CONTENT_BG);
        p.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton btnOlc    = UIConstants.butonOlustur("🌡️ Sıcaklık Ölç",    UIConstants.INFO_COLOR);
        JButton btnOtom   = UIConstants.butonOlustur("🤖 Otomatik Kontrol", UIConstants.HEADER_BG);
        JButton btnAc     = UIConstants.butonOlustur("✅ Klimayı Aç",       UIConstants.SUCCESS_COLOR);
        JButton btnKapat  = UIConstants.butonOlustur("⬜ Klimayı Kapat",    UIConstants.DANGER_COLOR);

        btnOlc.addActionListener(e -> {
            ortamServisi.sicaklikOku();
            ekraniGuncelle();
        });
        btnOtom.addActionListener(e -> {
            ortamServisi.otomatikKlimaKontrol();
            ekraniGuncelle();
        });
        btnAc.addActionListener(e -> {
            ortamServisi.klimaManuelKontrol(true);
            ekraniGuncelle();
        });
        btnKapat.addActionListener(e -> {
            ortamServisi.klimaManuelKontrol(false);
            ekraniGuncelle();
        });

        p.add(btnOlc);
        p.add(btnOtom);
        p.add(btnAc);
        p.add(btnKapat);
        return p;
    }

    public void ekraniGuncelle() {
        double s = ortamServisi.getAnlikSicaklik();
        sicaklikLabel.setText(String.format("%.1f °C", s));
        termometre.setSicaklik(s);

        boolean acik = ortamServisi.isKlimaAcik();
        klimaDurumLabel.setText(acik ? "AÇIK" : "KAPALI");
        klimaDurumLabel.setForeground(acik ? UIConstants.SUCCESS_COLOR : UIConstants.TEXT_SECONDARY);

        degisimLabel.setText(String.valueOf(ortamServisi.getKlimaDegisimSayisi()));
        repaint();
    }

    private void otomatikTimerKur() {
        otomatikTimer = new javax.swing.Timer(REFRESH_MS, e -> {
            ortamServisi.otomatikKlimaKontrol();
            ekraniGuncelle();
        });

        otomatikTimer.setRepeats(true);
    }

    private JPanel beyazKart() {
        JPanel k = new JPanel();
        k.setLayout(new BoxLayout(k, BoxLayout.Y_AXIS));
        k.setBackground(Color.WHITE);
        k.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 225, 245)),
            new EmptyBorder(10, 15, 15, 15)));
        return k;
    }

    static class TermometerPanel extends JPanel {
    	private static final long serialVersionUID = 1L;
        private double sicaklik = 22.0;
        static final double MIN = 18.0, MAX = 35.0;

        public void setSicaklik(double s) { this.sicaklik = s; repaint(); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            int cx = w / 2, cy = h / 2;
            int r  = Math.min(w, h) / 2 - 10;

            g2.setStroke(new BasicStroke(12, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(235, 238, 248));
            g2.drawOval(cx - r, cy - r, r * 2, r * 2);

            double oran  = (sicaklik - MIN) / (MAX - MIN);
            oran = Math.max(0, Math.min(1, oran));
            double derece = oran * 270.0;
            Color renk = oran < 0.4 ? UIConstants.INFO_COLOR
                       : oran < 0.7 ? UIConstants.SUCCESS_COLOR
                       : oran < 0.85 ? UIConstants.WARNING_COLOR
                       : UIConstants.DANGER_COLOR;
            g2.setColor(renk);
            g2.draw(new Arc2D.Double(cx - r, cy - r, r * 2, r * 2,
                135, -derece, Arc2D.OPEN));
            g2.dispose();
        }

        @Override public Dimension getPreferredSize() { return new Dimension(140, 140); }
    }
}
