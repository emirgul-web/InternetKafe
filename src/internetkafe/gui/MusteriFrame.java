package internetkafe.gui;

import internetkafe.model.Bilgisayar;
import internetkafe.model.Musteri;
import internetkafe.model.OturumKaydi;
import internetkafe.servis.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class MusteriFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private final Musteri            girisYapanMusteri;
    private final MusteriServisi     musteriServisi;
    private final BilgisayarServisi  bilgisayarServisi;
    private final OdemeServisi       odemeServisi;
    private final OrtamServisi       ortamServisi;
    private final RaporServisi       raporServisi;

    private JPanel     contentPanel;
    private CardLayout cardLayout;
    private JButton    aktifButon;

    private static final String P_BILGISAYAR = "BİLGİSAYAR";
    private static final String P_OTURUM     = "OTURUM";
    private static final String P_BAKIYE     = "BAKİYE";

    private OturumBilgiPanel oturumPanel;
    private BakiyePanel      bakiyePanel;

    public MusteriFrame(Musteri musteri,
                        MusteriServisi ms, BilgisayarServisi bs,
                        OdemeServisi os, OrtamServisi orts, RaporServisi rs) {
        this.girisYapanMusteri = musteri;
        this.musteriServisi    = ms;
        this.bilgisayarServisi = bs;
        this.odemeServisi      = os;
        this.ortamServisi      = orts;
        this.raporServisi      = rs;

        pencereAyarla();
        arayuzOlustur();
    }

    private void pencereAyarla() {
        setTitle("🖥️  İnternet Kafe — " + girisYapanMusteri.getAd() + " " + girisYapanMusteri.getSoyad());
        setSize(UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                musteriServisi.musteriCikis(girisYapanMusteri.getMusteriId());
                dispose();
                new GirisEkrani(musteriServisi, bilgisayarServisi,
                    odemeServisi, ortamServisi, raporServisi).setVisible(true);
            }
        });
    }

    private void arayuzOlustur() {
        setLayout(new BorderLayout());

        JPanel sidebar = sidebarOlustur();
        add(sidebar, BorderLayout.WEST);

        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UIConstants.CONTENT_BG);

        BilgisayarPanel bilgisayarPanel = new BilgisayarPanel(bilgisayarServisi, musteriServisi, girisYapanMusteri.getMusteriId());
        oturumPanel  = new OturumBilgiPanel();
        bakiyePanel  = new BakiyePanel();

        contentPanel.add(bilgisayarPanel, P_BILGISAYAR);
        contentPanel.add(oturumPanel,     P_OTURUM);
        contentPanel.add(bakiyePanel,     P_BAKIYE);

        add(contentPanel, BorderLayout.CENTER);
        cardLayout.show(contentPanel, P_BILGISAYAR);
    }

    private JPanel sidebarOlustur() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(UIConstants.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(UIConstants.SIDEBAR_WIDTH, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        sidebar.add(logoAlani());
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(ayirici());

        String[][] navItems = {
            { "🖥️  Bilgisayar Seç",  P_BILGISAYAR },
            { "⏱️  Oturumum",         P_OTURUM     },
            { "💰  Bakiyem",           P_BAKIYE     },
        };

        JButton ilkButon = null;
        for (String[] item : navItems) {
            JButton btn = navButonOlustur(item[0], item[1]);
            if (ilkButon == null) ilkButon = btn;
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(4));
        }

        if (ilkButon != null) {
            aktifButon = ilkButon;
            aktifButon.setBackground(UIConstants.SIDEBAR_ACTIVE);
        }

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(ayirici());
        sidebar.add(cikisButonu());
        sidebar.add(Box.createVerticalStrut(15));

        return sidebar;
    }

    private JPanel logoAlani() {
        JPanel panel = new JPanel();
        panel.setBackground(UIConstants.SIDEBAR_BG);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(25, 15, 15, 15));

        JLabel ikon = new JLabel("👤", SwingConstants.CENTER);
        ikon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        ikon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel ad = new JLabel(girisYapanMusteri.getAd() + " " + girisYapanMusteri.getSoyad(),
            SwingConstants.CENTER);
        ad.setFont(UIConstants.FONT_SUBTITLE);
        ad.setForeground(Color.WHITE);
        ad.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel idLabel = new JLabel("Müşteri #" + girisYapanMusteri.getMusteriId(),
            SwingConstants.CENTER);
        idLabel.setFont(UIConstants.FONT_SMALL);
        idLabel.setForeground(new Color(150, 165, 210));
        idLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(ikon);
        panel.add(Box.createVerticalStrut(6));
        panel.add(ad);
        panel.add(idLabel);
        return panel;
    }

    private JSeparator ayirici() {
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(60, 75, 120));
        sep.setMaximumSize(new Dimension(UIConstants.SIDEBAR_WIDTH, 1));
        return sep;
    }

    private JButton navButonOlustur(String metin, String panelAdi) {
        JButton btn = new JButton(metin);
        btn.setFont(UIConstants.FONT_SIDEBAR);
        btn.setForeground(new Color(190, 200, 230));
        btn.setBackground(UIConstants.SIDEBAR_BG);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(UIConstants.SIDEBAR_WIDTH, 44));
        btn.setPreferredSize(new Dimension(UIConstants.SIDEBAR_WIDTH, 44));
        btn.setBorder(new EmptyBorder(0, 18, 0, 0));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn != aktifButon)
                    btn.setBackground(UIConstants.SIDEBAR_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (btn != aktifButon)
                    btn.setBackground(UIConstants.SIDEBAR_BG);
            }
        });

        btn.addActionListener(e -> {
            if (aktifButon != null) {
                aktifButon.setBackground(UIConstants.SIDEBAR_BG);
                aktifButon.setForeground(new Color(190, 200, 230));
            }
            aktifButon = btn;
            btn.setBackground(UIConstants.SIDEBAR_ACTIVE);
            btn.setForeground(Color.WHITE);
            cardLayout.show(contentPanel, panelAdi);

            if (P_OTURUM.equals(panelAdi)) oturumPanel.yenile();
            if (P_BAKIYE.equals(panelAdi)) bakiyePanel.yenile();
        });

        return btn;
    }

    private JButton cikisButonu() {
        JButton btn = new JButton("🚪  Çıkış Yap");
        btn.setFont(UIConstants.FONT_SIDEBAR);
        btn.setForeground(new Color(255, 100, 100));
        btn.setBackground(UIConstants.SIDEBAR_BG);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(UIConstants.SIDEBAR_WIDTH, 44));
        btn.setBorder(new EmptyBorder(0, 18, 0, 0));

        btn.addActionListener(e -> {
            int onay = JOptionPane.showConfirmDialog(this,
                "Çıkış yapmak istiyor musunuz?",
                "Çıkış Onayı",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            if (onay == JOptionPane.YES_OPTION) {
                musteriServisi.musteriCikis(girisYapanMusteri.getMusteriId());
                dispose();
                new GirisEkrani(musteriServisi, bilgisayarServisi,
                    odemeServisi, ortamServisi, raporServisi).setVisible(true);
            }
        });
        return btn;
    }

    class OturumBilgiPanel extends JPanel {
        private JLabel durumLabel, pcLabel, sureLabelVal, ucretLabelVal;
        private javax.swing.Timer yenileTimer;

        OturumBilgiPanel() {
            setLayout(new BorderLayout());
            setBackground(UIConstants.CONTENT_BG);
            setBorder(new EmptyBorder(20, 20, 20, 20));

            JPanel header = new JPanel(new BorderLayout());
            header.setBackground(UIConstants.CONTENT_BG);
            header.setBorder(new EmptyBorder(0, 0, 15, 0));
            JLabel baslik = new JLabel("⏱️  Oturum Bilgilerim");
            baslik.setFont(UIConstants.FONT_TITLE);
            baslik.setForeground(UIConstants.TEXT_PRIMARY);
            header.add(baslik, BorderLayout.WEST);
            add(header, BorderLayout.NORTH);

            JPanel merkez = new JPanel(new GridLayout(2, 2, 20, 20));
            merkez.setBackground(UIConstants.CONTENT_BG);

            durumLabel   = kartOlustur(merkez, "📊 Oturum Durumu", "Kontrol ediliyor...");
            pcLabel      = kartOlustur(merkez, "🖥️ Bilgisayar", "—");
            sureLabelVal = kartOlustur(merkez, "⏱️ Geçen Süre", "—");
            ucretLabelVal = kartOlustur(merkez, "💰 Tahmini Ücret", "—");

            add(merkez, BorderLayout.CENTER);

            yenileTimer = new javax.swing.Timer(3000, ev -> yenile());
            yenileTimer.start();

            yenile();
        }

        private JLabel kartOlustur(JPanel parent, String baslik, String deger) {
            JPanel kart = new JPanel();
            kart.setLayout(new BoxLayout(kart, BoxLayout.Y_AXIS));
            kart.setBackground(Color.WHITE);
            kart.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 245)),
                new EmptyBorder(20, 20, 20, 20)));

            JLabel baslikLabel = new JLabel(baslik);
            baslikLabel.setFont(UIConstants.FONT_SMALL);
            baslikLabel.setForeground(UIConstants.TEXT_SECONDARY);
            baslikLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel degerLabel = new JLabel(deger);
            degerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            degerLabel.setForeground(UIConstants.TEXT_PRIMARY);
            degerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            kart.add(baslikLabel);
            kart.add(Box.createVerticalStrut(10));
            kart.add(degerLabel);
            kart.add(Box.createVerticalGlue());

            parent.add(kart);
            return degerLabel;
        }

        void yenile() {
            OturumKaydi oturum = bilgisayarServisi.aktifOturumGetir(
                girisYapanMusteri.getMusteriId());

            if (oturum != null && oturum.isAktif()) {
                durumLabel.setText("AKTİF");
                durumLabel.setForeground(UIConstants.SUCCESS_COLOR);

                pcLabel.setText("PC #" + oturum.getBilgisayarId());

                long dakika = oturum.getSureDakika();
                long saat = dakika / 60;
                long dk = dakika % 60;
                sureLabelVal.setText(String.format("%d sa %d dk", saat, dk));

                double sureSaat = oturum.getSureSaat();
                double saatlikUcret = 10.0;
                for (Bilgisayar pc : bilgisayarServisi.tumBilgisayarlariListele()) {
                    if (pc.getBilgisayarId() == oturum.getBilgisayarId()) {
                        saatlikUcret = pc.getSaatlikUcret();
                        break;
                    }
                }
                double ucret = sureSaat * saatlikUcret;
                if (ucret < 1.0 && dakika > 0) ucret = 1.0;
                ucretLabelVal.setText(String.format("%.2f TL", ucret));
            } else {
                durumLabel.setText("AKTİF OTURUM YOK");
                durumLabel.setForeground(UIConstants.TEXT_SECONDARY);
                pcLabel.setText("—");
                sureLabelVal.setText("—");
                ucretLabelVal.setText("—");
            }
        }
    }

    class BakiyePanel extends JPanel {
        private JLabel bakiyeLabel;
        private JLabel adLabel;
        private JLabel telefonLabel;

        BakiyePanel() {
            setLayout(new BorderLayout());
            setBackground(UIConstants.CONTENT_BG);
            setBorder(new EmptyBorder(20, 20, 20, 20));

            JPanel header = new JPanel(new BorderLayout());
            header.setBackground(UIConstants.CONTENT_BG);
            header.setBorder(new EmptyBorder(0, 0, 15, 0));
            JLabel baslik = new JLabel("💰  Bakiye Bilgilerim");
            baslik.setFont(UIConstants.FONT_TITLE);
            baslik.setForeground(UIConstants.TEXT_PRIMARY);
            header.add(baslik, BorderLayout.WEST);
            add(header, BorderLayout.NORTH);

            JPanel merkez = new JPanel();
            merkez.setBackground(UIConstants.CONTENT_BG);
            merkez.setLayout(new BoxLayout(merkez, BoxLayout.Y_AXIS));

            JPanel kart = new JPanel();
            kart.setLayout(new BoxLayout(kart, BoxLayout.Y_AXIS));
            kart.setBackground(Color.WHITE);
            kart.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 245)),
                new EmptyBorder(30, 30, 30, 30)));
            kart.setMaximumSize(new Dimension(500, 300));
            kart.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel ikon = new JLabel("👤", SwingConstants.CENTER);
            ikon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
            ikon.setAlignmentX(Component.CENTER_ALIGNMENT);

            adLabel = new JLabel("", SwingConstants.CENTER);
            adLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
            adLabel.setForeground(UIConstants.TEXT_PRIMARY);
            adLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            telefonLabel = new JLabel("", SwingConstants.CENTER);
            telefonLabel.setFont(UIConstants.FONT_BODY);
            telefonLabel.setForeground(UIConstants.TEXT_SECONDARY);
            telefonLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JSeparator sep = new JSeparator();
            sep.setMaximumSize(new Dimension(300, 1));
            sep.setForeground(new Color(230, 235, 245));

            JLabel bakiyeBaslik = new JLabel("Mevcut Bakiye", SwingConstants.CENTER);
            bakiyeBaslik.setFont(UIConstants.FONT_SMALL);
            bakiyeBaslik.setForeground(UIConstants.TEXT_SECONDARY);
            bakiyeBaslik.setAlignmentX(Component.CENTER_ALIGNMENT);

            bakiyeLabel = new JLabel("", SwingConstants.CENTER);
            bakiyeLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
            bakiyeLabel.setForeground(UIConstants.SUCCESS_COLOR);
            bakiyeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            kart.add(ikon);
            kart.add(Box.createVerticalStrut(10));
            kart.add(adLabel);
            kart.add(Box.createVerticalStrut(4));
            kart.add(telefonLabel);
            kart.add(Box.createVerticalStrut(20));
            kart.add(sep);
            kart.add(Box.createVerticalStrut(20));
            kart.add(bakiyeBaslik);
            kart.add(Box.createVerticalStrut(6));
            kart.add(bakiyeLabel);

            JButton btnYukle = UIConstants.butonOlustur("💳 Bakiye Yükle", UIConstants.HEADER_BG);
            btnYukle.setMaximumSize(new Dimension(200, 40));
            btnYukle.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnYukle.addActionListener(e -> bakiyeYukleDialog());

            kart.add(Box.createVerticalStrut(20));
            kart.add(btnYukle);

            merkez.add(Box.createVerticalStrut(20));
            merkez.add(kart);
            merkez.add(Box.createVerticalGlue());

            add(merkez, BorderLayout.CENTER);
            yenile();
        }

        void yenile() {
            Musteri m = musteriServisi.idIleAra(girisYapanMusteri.getMusteriId());
            if (m != null) {
                adLabel.setText(m.getAd() + " " + m.getSoyad());
                telefonLabel.setText("Tel: " + m.getTelefon());
                bakiyeLabel.setText(String.format("%.2f TL", m.getBakiye()));
            }
        }

        private void bakiyeYukleDialog() {
            JTextField miktarField = new JTextField(12);
            miktarField.setFont(UIConstants.FONT_BODY);
            miktarField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 240)),
                new EmptyBorder(5, 8, 5, 8)));

            JPanel form = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(6, 6, 6, 6);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;

            JLabel title = new JLabel("💳 Bakiye Yükleme");
            title.setFont(UIConstants.FONT_SUBTITLE);
            gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
            form.add(title, gbc);
            gbc.gridwidth = 1;

            JLabel lbl = new JLabel("Miktar (TL):");
            lbl.setFont(UIConstants.FONT_BODY);
            gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
            form.add(lbl, gbc);

            gbc.gridx = 1; gbc.weightx = 0.7;
            form.add(miktarField, gbc);

            form.setPreferredSize(new Dimension(280, 90));

            int sonuc = JOptionPane.showConfirmDialog(MusteriFrame.this, form,
                "Bakiye Yükle", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

            if (sonuc == JOptionPane.OK_OPTION) {
                try {
                    double miktar = Double.parseDouble(
                        miktarField.getText().trim().replace(",", "."));
                    if (miktar <= 0) {
                        JOptionPane.showMessageDialog(MusteriFrame.this,
                            "Miktar 0'dan büyük olmalıdır!",
                            "Hata", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    musteriServisi.bakiyeYukle(girisYapanMusteri.getMusteriId(), miktar);
                    yenile();
                    JOptionPane.showMessageDialog(MusteriFrame.this,
                        String.format("%.2f TL başarıyla yüklendi!", miktar),
                        "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(MusteriFrame.this,
                        "Geçerli bir miktar girin!",
                        "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
