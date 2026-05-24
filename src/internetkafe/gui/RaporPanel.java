package internetkafe.gui;

import internetkafe.model.Bilgisayar;
import internetkafe.servis.RaporServisi;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RaporPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final RaporServisi raporServisi;

    private JLabel gelirDegerLabel;
    private JLabel oturumDegerLabel;
    private JLabel dolulukDegerLabel;
    private JLabel musteriDegerLabel;

    private BarChart grafik;

    public RaporPanel(RaporServisi raporServisi) {
        this.raporServisi = raporServisi;
        setLayout(new BorderLayout(0, 15));
        setBackground(UIConstants.CONTENT_BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(headerOlustur(), BorderLayout.NORTH);
        add(ortaBolum(),     BorderLayout.CENTER);
        add(butonlar(),      BorderLayout.SOUTH);

        verileriGuncelle();
    }

    private JPanel headerOlustur() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UIConstants.CONTENT_BG);
        p.setBorder(new EmptyBorder(0, 0, 5, 0));

        JLabel baslik = new JLabel("📊  Raporlama");
        baslik.setFont(UIConstants.FONT_TITLE);
        baslik.setForeground(UIConstants.TEXT_PRIMARY);

        String tarih = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy",
                new java.util.Locale("tr", "TR")));
        JLabel tarihLabel = new JLabel(tarih);
        tarihLabel.setFont(UIConstants.FONT_BODY);
        tarihLabel.setForeground(UIConstants.TEXT_SECONDARY);

        p.add(baslik,    BorderLayout.WEST);
        p.add(tarihLabel, BorderLayout.EAST);
        return p;
    }

    private JPanel ortaBolum() {
        JPanel p = new JPanel(new BorderLayout(0, 15));
        p.setBackground(UIConstants.CONTENT_BG);
        p.add(ozetKartlari(), BorderLayout.NORTH);
        p.add(grafikAlani(),  BorderLayout.CENTER);
        return p;
    }

    private JPanel ozetKartlari() {
        JPanel p = new JPanel(new GridLayout(1, 4, 15, 0));
        p.setBackground(UIConstants.CONTENT_BG);

        gelirDegerLabel   = ozetKarti(p, "💰  Günlük Gelir",  "0,00 TL", UIConstants.SUCCESS_COLOR);
        oturumDegerLabel  = ozetKarti(p, "⏱  Toplam Oturum", "0",        UIConstants.HEADER_BG);
        dolulukDegerLabel = ozetKarti(p, "🖥  Doluluk",        "0%",       UIConstants.WARNING_COLOR);
        musteriDegerLabel = ozetKarti(p, "👤  Kayıtlı Müşteri","0",        UIConstants.INFO_COLOR);
        return p;
    }

    private JLabel ozetKarti(JPanel konteyner, String baslik, String deger, Color renk) {
        JPanel kart = new JPanel();
        kart.setLayout(new BoxLayout(kart, BoxLayout.Y_AXIS));
        kart.setBackground(Color.WHITE);
        kart.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, renk),
                new EmptyBorder(14, 14, 14, 14)));

        JLabel baslikLabel = new JLabel(baslik);
        baslikLabel.setFont(UIConstants.FONT_SMALL);
        baslikLabel.setForeground(UIConstants.TEXT_SECONDARY);

        JLabel degerLabel = new JLabel(deger);
        degerLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        degerLabel.setForeground(renk);

        kart.add(baslikLabel);
        kart.add(Box.createVerticalStrut(6));
        kart.add(degerLabel);
        konteyner.add(kart);
        return degerLabel;
    }

    private JPanel grafikAlani() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 245)),
                new EmptyBorder(15, 15, 15, 15)));

        JLabel title = new JLabel("  Bilgisayar Kullanım Dağılımı");
        title.setFont(UIConstants.FONT_SUBTITLE);
        title.setForeground(UIConstants.TEXT_PRIMARY);
        p.add(title, BorderLayout.NORTH);

        grafik = new BarChart(raporServisi);
        p.add(grafik, BorderLayout.CENTER);
        return p;
    }

    private JPanel butonlar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        p.setBackground(UIConstants.CONTENT_BG);

        JButton btnYenile = UIConstants.butonOlustur("🔄 Yenile", UIConstants.HEADER_BG);
        btnYenile.addActionListener(e -> verileriGuncelle());
        p.add(btnYenile);
        return p;
    }

    public void verileriGuncelle() {
        gelirDegerLabel.setText(String.format("%.2f TL", raporServisi.getGunlukGelir()));
        oturumDegerLabel.setText(String.valueOf(raporServisi.getToplamOturumSayisi()));
        dolulukDegerLabel.setText(String.format("%.0f%%", raporServisi.getDolulukOrani()));
        musteriDegerLabel.setText(String.valueOf(raporServisi.getKayitliMusteriSayisi()));

        raporServisi.gunlukRaporKaydet();

        grafik.repaint();
    }

    private static class BarChart extends JPanel {
        private final RaporServisi raporServisi;

        BarChart(RaporServisi raporServisi) {
            this.raporServisi = raporServisi;
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            List<Bilgisayar> pcler = raporServisi.tumBilgisayarlar();
            if (pcler.isEmpty()) return;

            int w = getWidth(), h = getHeight();
            int margin = 30;
            int barW   = (w - 2 * margin) / pcler.size() - 6;
            int maxH   = h - margin - 30;

            g2.setColor(new Color(220, 225, 245));
            g2.drawLine(margin, margin, margin, h - 30);
            g2.drawLine(margin, h - 30, w - margin, h - 30);

            for (int i = 0; i < pcler.size(); i++) {
                Bilgisayar pc = pcler.get(i);
                int x = margin + i * (barW + 6) + 3;

                Color renk = pc.isMusait() ? UIConstants.SUCCESS_COLOR : UIConstants.DANGER_COLOR;
                int barH = pc.isMusait() ? (int)(maxH * 0.3) : (int)(maxH * 0.8);
                int y    = h - 30 - barH;

                g2.setColor(renk);
                g2.fillRoundRect(x, y, barW, barH, 6, 6);

                g2.setColor(UIConstants.TEXT_SECONDARY);
                g2.setFont(UIConstants.FONT_SMALL);
                String label = "#" + pc.getBilgisayarId();
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(label, x + (barW - fm.stringWidth(label)) / 2, h - 12);

                String dur = pc.isMusait() ? "Boş" : "Dolu";
                g2.setColor(renk);
                g2.drawString(dur, x + (barW - fm.stringWidth(dur)) / 2, y - 4);
            }

            g2.setColor(UIConstants.SUCCESS_COLOR);
            g2.fillRoundRect(w - 140, 10, 12, 12, 3, 3);
            g2.setColor(UIConstants.TEXT_SECONDARY);
            g2.setFont(UIConstants.FONT_SMALL);
            g2.drawString("Boş", w - 124, 21);

            g2.setColor(UIConstants.DANGER_COLOR);
            g2.fillRoundRect(w - 90, 10, 12, 12, 3, 3);
            g2.drawString("Dolu", w - 74, 21);

            g2.dispose();
        }
    }
}