package internetkafe.gui;

import internetkafe.model.Bilgisayar;
import internetkafe.model.Musteri;
import internetkafe.model.OturumKaydi;
import internetkafe.servis.BilgisayarServisi;
import internetkafe.servis.MusteriServisi;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.List;

public class BilgisayarPanel extends JPanel {

    private final BilgisayarServisi bilgisayarServisi;
    private final MusteriServisi    musteriServisi;
    private int                     kullananMusteriId = -1;

    private KafeHaritasi harita;
    private JLabel       dolulukLabel;
    private JLabel       bilgiLabel;

    public BilgisayarPanel(BilgisayarServisi bs, MusteriServisi ms) {
        this(bs, ms, -1);
    }

    public BilgisayarPanel(BilgisayarServisi bs, MusteriServisi ms, int musteriId) {
        this.bilgisayarServisi = bs;
        this.musteriServisi    = ms;
        this.kullananMusteriId = musteriId;
        setLayout(new BorderLayout());
        setBackground(UIConstants.CONTENT_BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(headerOlustur(),  BorderLayout.NORTH);
        add(haritaOlustur(),  BorderLayout.CENTER);
        add(altBarOlustur(),  BorderLayout.SOUTH);
    }

    private JPanel headerOlustur() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UIConstants.CONTENT_BG);
        p.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel baslik = new JLabel("🖥️  Bilgisayar Seçimi — Kuşbakışı Görünüm");
        baslik.setFont(UIConstants.FONT_TITLE);
        baslik.setForeground(UIConstants.TEXT_PRIMARY);

        dolulukLabel = new JLabel("Doluluk: —");
        dolulukLabel.setFont(UIConstants.FONT_SUBTITLE);
        dolulukLabel.setForeground(UIConstants.TEXT_SECONDARY);

        p.add(baslik,      BorderLayout.WEST);
        p.add(dolulukLabel, BorderLayout.EAST);
        return p;
    }

    private JPanel haritaOlustur() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UIConstants.CONTENT_BG);

        harita = new KafeHaritasi();
        wrapper.add(harita, BorderLayout.CENTER);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(Color.WHITE);
        info.setPreferredSize(new Dimension(200, 0));
        info.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 225, 245)),
            new EmptyBorder(15, 15, 15, 15)));

        JLabel infoTitle = new JLabel("📋 Bilgi");
        infoTitle.setFont(UIConstants.FONT_SUBTITLE);
        infoTitle.setForeground(UIConstants.TEXT_PRIMARY);
        infoTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        bilgiLabel = new JLabel("<html><p style='color:#888;'>Bir bilgisayara<br>tıklayın</p></html>");
        bilgiLabel.setFont(UIConstants.FONT_BODY);
        bilgiLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel legend = new JPanel();
        legend.setLayout(new BoxLayout(legend, BoxLayout.Y_AXIS));
        legend.setBackground(Color.WHITE);
        legend.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel legendTitle = new JLabel("Renk Kodları:");
        legendTitle.setFont(UIConstants.FONT_SMALL);
        legendTitle.setForeground(UIConstants.TEXT_SECONDARY);
        legendTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        legend.add(legendTitle);
        legend.add(Box.createVerticalStrut(6));
        legend.add(legendItem("🟢 Boş — Seçilebilir"));
        legend.add(Box.createVerticalStrut(3));
        legend.add(legendItem("🔴 Dolu — Kullanımda"));
        legend.add(Box.createVerticalStrut(3));
        legend.add(legendItem("🟣 Gaming PC"));
        legend.add(Box.createVerticalStrut(3));
        legend.add(legendItem("🔵 Normal PC"));

        info.add(infoTitle);
        info.add(Box.createVerticalStrut(12));
        info.add(bilgiLabel);
        info.add(Box.createVerticalStrut(20));
        info.add(new JSeparator());
        info.add(Box.createVerticalStrut(12));
        info.add(legend);
        info.add(Box.createVerticalGlue());

        JPanel infoWrapper = new JPanel(new BorderLayout());
        infoWrapper.setBackground(UIConstants.CONTENT_BG);
        infoWrapper.setBorder(new EmptyBorder(0, 12, 0, 0));
        infoWrapper.add(info);

        wrapper.add(infoWrapper, BorderLayout.EAST);
        return wrapper;
    }

    private JLabel legendItem(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UIConstants.FONT_SMALL);
        l.setForeground(UIConstants.TEXT_PRIMARY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JPanel altBarOlustur() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        p.setBackground(UIConstants.CONTENT_BG);
        p.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton btnYenile = UIConstants.butonOlustur("🔄 Yenile", UIConstants.TEXT_SECONDARY);
        btnYenile.addActionListener(e -> harita.repaint());
        p.add(btnYenile);

        return p;
    }

    class KafeHaritasi extends JPanel {

        private int hoverPcId = -1;
        private Rectangle2D[] masaAlanlari;

        KafeHaritasi() {
            setBackground(new Color(55, 62, 82));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createLineBorder(new Color(40, 48, 70), 2));

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    int onceki = hoverPcId;
                    hoverPcId = masaBul(e.getX(), e.getY());
                    if (hoverPcId != onceki) repaint();
                    bilgiGuncelle(hoverPcId);
                }
            });

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int pcId = masaBul(e.getX(), e.getY());
                    if (pcId > 0) masaTiklandi(pcId);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    hoverPcId = -1;
                    repaint();
                }
            });
        }

        private int masaBul(int mx, int my) {
            if (masaAlanlari == null) return -1;
            List<Bilgisayar> pcler = bilgisayarServisi.tumBilgisayarlariListele();
            for (int i = 0; i < pcler.size() && i < masaAlanlari.length; i++) {
                if (masaAlanlari[i] != null && masaAlanlari[i].contains(mx, my)) {
                    return pcler.get(i).getBilgisayarId();
                }
            }
            return -1;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();

            g2.setColor(new Color(62, 70, 92));
            for (int x = 0; x < w; x += 40) {
                for (int y = 0; y < h; y += 40) {
                    g2.drawRect(x, y, 40, 40);
                }
            }

            g2.setColor(new Color(90, 100, 130));
            g2.setStroke(new BasicStroke(4));
            g2.drawRoundRect(20, 20, w - 40, h - 40, 12, 12);

            g2.setColor(new Color(55, 62, 82));
            int kapiW = 60, kapiX = w / 2 - kapiW / 2;
            g2.fillRect(kapiX, h - 22, kapiW, 6);
            g2.setColor(new Color(255, 200, 60));
            g2.setFont(UIConstants.FONT_SMALL);
            FontMetrics fmKapi = g2.getFontMetrics();
            String kapiYazi = "🚪 GİRİŞ";
            g2.drawString(kapiYazi, w / 2 - fmKapi.stringWidth(kapiYazi) / 2, h - 28);

            g2.setColor(new Color(200, 210, 240, 80));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            FontMetrics fmT = g2.getFontMetrics();
            String kafeAdi = "İNTERNET KAFE — KUŞBAKIŞI";
            g2.drawString(kafeAdi, w / 2 - fmT.stringWidth(kafeAdi) / 2, 40);

            List<Bilgisayar> pcler = bilgisayarServisi.tumBilgisayarlariListele();
            masaAlanlari = new Rectangle2D[pcler.size()];

            int masaW = 80, masaH = 70;
            int basX = 60, basY = 70;
            int aralikX = 30, aralikY = 20;

            int normalIdx = 0;
            int gamingIdx = 0;

            int solBlokX = 60;
            int solBlokY = 80;

            int sagBlokX = w - 60 - masaW;

            for (int i = 0; i < pcler.size(); i++) {
                Bilgisayar pc = pcler.get(i);
                int mx, my;

                if (pc.getTip().equals("Normal")) {
                    int col = normalIdx % 2;
                    int row = normalIdx / 2;
                    mx = solBlokX + col * (masaW + aralikX);
                    my = solBlokY + row * (masaH + aralikY);
                    normalIdx++;
                } else {
                    mx = sagBlokX;
                    my = solBlokY + gamingIdx * (masaH + aralikY);
                    gamingIdx++;
                }

                masaAlanlari[i] = new Rectangle2D.Double(mx, my, masaW, masaH);
                masaCiz(g2, pc, mx, my, masaW, masaH, pc.getBilgisayarId() == hoverPcId);
            }

            g2.setColor(new Color(180, 190, 220));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            g2.drawString("NORMAL ALAN", solBlokX, solBlokY - 10);

            g2.drawString("GAMİNG ALAN", sagBlokX - 10, solBlokY - 10);

            g2.setColor(new Color(100, 110, 140));
            float[] dash = {8f, 6f};
            g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, dash, 0));
            int ayX = w / 2;
            g2.drawLine(ayX, 55, ayX, h - 45);

            int bos = 0;
            for (Bilgisayar pc : pcler) if (pc.isMusait()) bos++;
            dolulukLabel.setText(String.format("Boş: %d / %d", bos, pcler.size()));

            g2.dispose();
        }

        private void masaCiz(Graphics2D g2, Bilgisayar pc, int x, int y, int w, int h, boolean hover) {
            boolean bos = pc.isMusait();
            boolean gaming = pc.getTip().equals("Gaming");

            g2.setColor(new Color(0, 0, 0, 40));
            g2.fillRoundRect(x + 3, y + 3, w, h, 10, 10);

            Color masaRenk;
            if (hover) {
                masaRenk = bos ? new Color(60, 180, 100) : new Color(180, 60, 60);
            } else if (bos) {
                masaRenk = gaming ? new Color(70, 50, 130) : new Color(50, 90, 60);
            } else {
                masaRenk = gaming ? new Color(120, 30, 60) : new Color(130, 40, 40);
            }

            g2.setColor(masaRenk);
            g2.fillRoundRect(x, y, w, h, 10, 10);

            g2.setStroke(new BasicStroke(hover ? 3 : 1.5f));
            Color borderRenk = hover ? Color.WHITE
                : bos ? new Color(80, 200, 120) : new Color(255, 80, 80);
            g2.setColor(borderRenk);
            g2.drawRoundRect(x, y, w, h, 10, 10);

            int monW = 24, monH = 16;
            int monX = x + w / 2 - monW / 2, monY = y + 8;
            g2.setColor(bos ? new Color(100, 220, 150) : new Color(255, 100, 100));
            g2.fillRoundRect(monX, monY, monW, monH, 4, 4);
            g2.fillRect(monX + monW / 2 - 2, monY + monH, 4, 4);
            g2.fillRect(monX + monW / 2 - 6, monY + monH + 3, 12, 2);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            FontMetrics fm = g2.getFontMetrics();
            String pcNo = "#" + pc.getBilgisayarId();
            g2.drawString(pcNo, x + w / 2 - fm.stringWidth(pcNo) / 2, y + h / 2 + 8);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
            fm = g2.getFontMetrics();
            String tip = gaming ? "GAMING" : "NORMAL";
            g2.setColor(new Color(200, 210, 230));
            g2.drawString(tip, x + w / 2 - fm.stringWidth(tip) / 2, y + h - 10);

            int ledR = 5;
            g2.setColor(bos ? new Color(50, 255, 100) : new Color(255, 50, 50));
            g2.fillOval(x + w - 14, y + 6, ledR * 2, ledR * 2);

            g2.setColor(new Color(100, 110, 140));
            g2.fillArc(x + w / 2 - 10, y + h - 4, 20, 16, 0, 180);
        }

        private void bilgiGuncelle(int pcId) {
            if (pcId < 0) {
                bilgiLabel.setText("<html><p style='color:#888;'>Bir bilgisayara<br>tıklayın</p></html>");
                return;
            }
            List<Bilgisayar> pcler = bilgisayarServisi.tumBilgisayarlariListele();
            for (Bilgisayar pc : pcler) {
                if (pc.getBilgisayarId() == pcId) {
                    StringBuilder sb = new StringBuilder("<html>");
                    sb.append("<b>PC #").append(pc.getBilgisayarId()).append("</b><br>");
                    sb.append("Tip: ").append(pc.getTip()).append("<br>");
                    sb.append("Ücret: ").append(String.format("%.0f", pc.getSaatlikUcret())).append(" TL/saat<br>");
                    sb.append("Durum: ");
                    if (pc.isMusait()) {
                        sb.append("<font color='green'>✅ BOŞ</font>");
                    } else {
                        sb.append("<font color='red'>🔴 DOLU</font><br>");
                        sb.append("Müşteri: #").append(pc.getAktifMusteriId());
                    }
                    sb.append("</html>");
                    bilgiLabel.setText(sb.toString());
                    return;
                }
            }
        }
    }

    private void masaTiklandi(int pcId) {
        List<Bilgisayar> pcler = bilgisayarServisi.tumBilgisayarlariListele();
        Bilgisayar secilen = null;
        for (Bilgisayar pc : pcler) {
            if (pc.getBilgisayarId() == pcId) { secilen = pc; break; }
        }
        if (secilen == null) return;

        if (!secilen.isMusait()) {
            JOptionPane.showMessageDialog(this,
                "Bu bilgisayar şu an kullanımda!\nMüşteri #" + secilen.getAktifMusteriId(),
                "Dolu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Musteri> aktifler = musteriServisi.aktifMusterileriListele();
        if (aktifler.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Giriş yapmış müşteri yok!\nÖnce Müşteri İşlemleri'nden giriş yapın.",
                "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JComboBox<String> combo = null;
        if (kullananMusteriId == -1) {
            String[] secenekler = new String[aktifler.size()];
            for (int i = 0; i < aktifler.size(); i++) {
                Musteri m = aktifler.get(i);
                secenekler[i] = "#" + m.getMusteriId() + " — " + m.getAd() + " " + m.getSoyad()
                    + " (Bakiye: " + String.format("%.2f", m.getBakiye()) + " TL)";
            }
            combo = new JComboBox<>(secenekler);
            combo.setFont(UIConstants.FONT_BODY);
        }

        String[] paketler = {"Sınırsız", "30 Dakika", "1 Saat", "2 Saat"};
        int[] paketSureleri = {0, 30, 60, 120};
        JComboBox<String> paketCombo = new JComboBox<>(paketler);
        paketCombo.setFont(UIConstants.FONT_BODY);

        JPanel form = new JPanel(new GridLayout(0, 1, 5, 5));
        form.setBorder(new EmptyBorder(10, 10, 10, 10));
        form.add(new JLabel("PC #" + pcId + " (" + secilen.getTip()
            + " — " + String.format("%.0f", secilen.getSaatlikUcret()) + " TL/saat)"));
        
        if (kullananMusteriId != -1) {
            Musteri m = musteriServisi.idIleAra(kullananMusteriId);
            form.add(new JLabel("Müşteri: " + (m != null ? m.getAd() + " " + m.getSoyad() : "Bilinmiyor")));
        } else {
            form.add(new JLabel("Müşteri seçin:"));
            form.add(combo);
        }

        form.add(new JLabel("Paket seçin:"));
        form.add(paketCombo);

        int res = JOptionPane.showConfirmDialog(this, form,
            "Oturum Başlat", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (res == JOptionPane.OK_OPTION) {
            Musteri secilenMusteri;
            if (kullananMusteriId != -1) {
                secilenMusteri = musteriServisi.idIleAra(kullananMusteriId);
            } else {
                secilenMusteri = aktifler.get(combo.getSelectedIndex());
            }

            if (secilenMusteri == null) {
                hataGoster("Müşteri bulunamadı!");
                return;
            }

            int secilenPaketDk = paketSureleri[paketCombo.getSelectedIndex()];
            OturumKaydi o = bilgisayarServisi.oturumBaslat(
                secilenMusteri.getMusteriId(), pcId, secilenPaketDk);
            if (o != null) {
                JOptionPane.showMessageDialog(this,
                    "✅ Oturum açıldı!\n" + secilenMusteri.getAd() + " → PC #" + pcId,
                    "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                harita.repaint();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Oturum başlatılamadı.\nMüşteri zaten bir bilgisayar kullanıyor olabilir.",
                    "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void hataGoster(String mesaj) {
        JOptionPane.showMessageDialog(this, mesaj, "Hata", JOptionPane.ERROR_MESSAGE);
    }
}
