package internetkafe.gui;

import internetkafe.servis.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class AdminFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private final MusteriServisi    musteriServisi;
    private final BilgisayarServisi bilgisayarServisi;
    private final OdemeServisi      odemeServisi;
    private final OrtamServisi      ortamServisi;
    private final RaporServisi      raporServisi;

    private JPanel      contentPanel;
    private CardLayout  cardLayout;
    private JButton     aktifButon;

    private MusteriPanel    musteriPanel;
    private BilgisayarPanel bilgisayarPanel;
    private OdemePanel      odemePanel;
    private RaporPanel      raporPanel;

    private static final String P_MUSTERI    = "MUSTERİ";
    private static final String P_BILGISAYAR = "BİLGİSAYAR";
    private static final String P_ODEME      = "ÖDEME";
    private static final String P_ORTAM      = "ORTAM";
    private static final String P_RAPOR      = "RAPOR";

    public AdminFrame(MusteriServisi ms, BilgisayarServisi bs,
                     OdemeServisi os, OrtamServisi orts, RaporServisi rs) {
        this.musteriServisi    = ms;
        this.bilgisayarServisi = bs;
        this.odemeServisi      = os;
        this.ortamServisi      = orts;
        this.raporServisi      = rs;

        pencereAyarla();
        arayuzu_olustur();
    }

    private void pencereAyarla() {
        setTitle("🖥️  İnternet Kafe — Admin Paneli");
        setSize(UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));
    }

    private void arayuzu_olustur() {
        setLayout(new BorderLayout());

        JPanel sidebar = sidebarOlustur();
        add(sidebar, BorderLayout.WEST);

        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UIConstants.CONTENT_BG);

        musteriPanel    = new MusteriPanel(musteriServisi, bilgisayarServisi);
        bilgisayarPanel = new BilgisayarPanel(bilgisayarServisi, musteriServisi);
        odemePanel      = new OdemePanel(odemeServisi, musteriServisi, bilgisayarServisi);
        OrtamPanel ortamPanel = new OrtamPanel(ortamServisi);
        raporPanel      = new RaporPanel(raporServisi);

        contentPanel.add(musteriPanel,    P_MUSTERI);
        contentPanel.add(bilgisayarPanel, P_BILGISAYAR);
        contentPanel.add(odemePanel,      P_ODEME);
        contentPanel.add(ortamPanel,      P_ORTAM);
        contentPanel.add(raporPanel,      P_RAPOR);

        add(contentPanel, BorderLayout.CENTER);

        cardLayout.show(contentPanel, P_MUSTERI);
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
            { "👤  Müşteri İşlemleri",    P_MUSTERI    },
            { "🖥️  Bilgisayar İşlemleri", P_BILGISAYAR },
            { "💳  Ödeme İşlemleri",       P_ODEME      },
            { "🌡️  Ortam Kontrolü",        P_ORTAM      },
            { "📊  Raporlama",             P_RAPOR      },
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

        JLabel ikon = new JLabel("🖥️", SwingConstants.CENTER);
        ikon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        ikon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel baslik = new JLabel("İnternet Kafe", SwingConstants.CENTER);
        baslik.setFont(UIConstants.FONT_SUBTITLE);
        baslik.setForeground(Color.WHITE);
        baslik.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel alt = new JLabel("Admin Paneli", SwingConstants.CENTER);
        alt.setFont(UIConstants.FONT_SMALL);
        alt.setForeground(new Color(150, 165, 210));
        alt.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(ikon);
        panel.add(Box.createVerticalStrut(6));
        panel.add(baslik);
        panel.add(alt);
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
            panelYenile(panelAdi);
        });

        return btn;
    }

    private void panelYenile(String panelAdi) {
        switch (panelAdi) {
            case P_MUSTERI    -> musteriPanel.tabloyiYenile();
            case P_BILGISAYAR -> bilgisayarPanel.repaint();
            case P_ODEME      -> odemePanel.tabloyiYenile();
            case P_RAPOR      -> raporPanel.verileriGuncelle();
        }
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
                "Giriş ekranına dönmek istiyor musunuz?",
                "Çıkış Onayı",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            if (onay == JOptionPane.YES_OPTION) {
                dispose();
                new GirisEkrani(musteriServisi, bilgisayarServisi,
                    odemeServisi, ortamServisi, raporServisi).setVisible(true);
            }
        });
        return btn;
    }
}
