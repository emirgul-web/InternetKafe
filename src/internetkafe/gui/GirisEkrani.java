package internetkafe.gui;

import internetkafe.model.Musteri;
import internetkafe.servis.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class GirisEkrani extends JFrame {
    private static final long serialVersionUID = 1L;

    private static final String ADMIN_SIFRE = "admin123";

    private final MusteriServisi    musteriServisi;
    private final BilgisayarServisi bilgisayarServisi;
    private final OdemeServisi      odemeServisi;
    private final OrtamServisi      ortamServisi;
    private final RaporServisi      raporServisi;

    public GirisEkrani(MusteriServisi ms, BilgisayarServisi bs,
                       OdemeServisi os, OrtamServisi orts, RaporServisi rs) {
        this.musteriServisi    = ms;
        this.bilgisayarServisi = bs;
        this.odemeServisi      = os;
        this.ortamServisi      = orts;
        this.raporServisi      = rs;

        pencereAyarla();
        arayuzOlustur();
    }

    private void pencereAyarla() {
        setTitle("İnternet Kafe Yönetim Sistemi");
        setSize(600, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void arayuzOlustur() {
        JPanel anaPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(30, 39, 73),
                    0, getHeight(), new Color(15, 20, 45));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        anaPanel.setLayout(new BoxLayout(anaPanel, BoxLayout.Y_AXIS));
        anaPanel.setBorder(new EmptyBorder(40, 50, 40, 50));

        JLabel ikon = new JLabel("🖥️", SwingConstants.CENTER);
        ikon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 52));
        ikon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel baslik = new JLabel("İnternet Kafe", SwingConstants.CENTER);
        baslik.setFont(new Font("Segoe UI", Font.BOLD, 30));
        baslik.setForeground(Color.WHITE);
        baslik.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel altBaslik = new JLabel("Yönetim Sistemi", SwingConstants.CENTER);
        altBaslik.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        altBaslik.setForeground(new Color(150, 165, 210));
        altBaslik.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(60, 75, 120));
        sep.setMaximumSize(new Dimension(400, 1));

        JLabel girisLabel = new JLabel("Giriş Türünüzü Seçin", SwingConstants.CENTER);
        girisLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        girisLabel.setForeground(new Color(190, 200, 230));
        girisLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel butonPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        butonPanel.setOpaque(false);
        butonPanel.setMaximumSize(new Dimension(440, 180));
        butonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel adminKart = girisKartiOlustur(
            "🔒", "Admin Girişi",
            "Tüm sistemi yönet",
            new Color(74, 108, 247),
            e -> adminGiris()
        );

        JPanel musteriKart = girisKartiOlustur(
            "👤", "Müşteri Girişi",
            "Bilgisayar kullan",
            new Color(46, 213, 115),
            e -> musteriGiris()
        );

        butonPanel.add(adminKart);
        butonPanel.add(musteriKart);

        JLabel footer = new JLabel("Sistem Analizi ve Tasarımı Dersi Projesi", SwingConstants.CENTER);
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footer.setForeground(new Color(100, 110, 140));
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);

        anaPanel.add(ikon);
        anaPanel.add(Box.createVerticalStrut(8));
        anaPanel.add(baslik);
        anaPanel.add(Box.createVerticalStrut(4));
        anaPanel.add(altBaslik);
        anaPanel.add(Box.createVerticalStrut(20));
        anaPanel.add(sep);
        anaPanel.add(Box.createVerticalStrut(20));
        anaPanel.add(girisLabel);
        anaPanel.add(Box.createVerticalStrut(20));
        anaPanel.add(butonPanel);
        anaPanel.add(Box.createVerticalGlue());
        anaPanel.add(footer);

        setContentPane(anaPanel);
    }

    private JPanel girisKartiOlustur(String emoji, String baslik,
                                      String aciklama, Color vurguRenk,
                                      ActionListener action) {
        JPanel kart = new JPanel() {
            private boolean hover = false;

            {
                setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(60, 75, 120), 2, true),
                    new EmptyBorder(20, 15, 20, 15)
                ));
                setBackground(new Color(40, 50, 85));
                setCursor(new Cursor(Cursor.HAND_CURSOR));

                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hover = true;
                        setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(vurguRenk, 2, true),
                            new EmptyBorder(20, 15, 20, 15)
                        ));
                        setBackground(new Color(50, 62, 105));
                        repaint();
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        hover = false;
                        setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(60, 75, 120), 2, true),
                            new EmptyBorder(20, 15, 20, 15)
                        ));
                        setBackground(new Color(40, 50, 85));
                        repaint();
                    }
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        action.actionPerformed(new ActionEvent(e.getSource(),
                            ActionEvent.ACTION_PERFORMED, "click"));
                    }
                });
            }
        };

        JLabel emojiLabel = new JLabel(emoji, SwingConstants.CENTER);
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        emojiLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel baslikLabel = new JLabel(baslik, SwingConstants.CENTER);
        baslikLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        baslikLabel.setForeground(Color.WHITE);
        baslikLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel aciklamaLabel = new JLabel(aciklama, SwingConstants.CENTER);
        aciklamaLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        aciklamaLabel.setForeground(new Color(150, 165, 210));
        aciklamaLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel renkCizgi = new JPanel();
        renkCizgi.setBackground(vurguRenk);
        renkCizgi.setMaximumSize(new Dimension(Integer.MAX_VALUE, 3));
        renkCizgi.setPreferredSize(new Dimension(0, 3));

        kart.add(Box.createVerticalStrut(8));
        kart.add(emojiLabel);
        kart.add(Box.createVerticalStrut(12));
        kart.add(baslikLabel);
        kart.add(Box.createVerticalStrut(4));
        kart.add(aciklamaLabel);
        kart.add(Box.createVerticalStrut(12));
        kart.add(renkCizgi);

        return kart;
    }

    private void adminGiris() {
        JPasswordField sifreField = new JPasswordField(18);
        sifreField.setFont(UIConstants.FONT_BODY);
        sifreField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 210, 240)),
            new EmptyBorder(6, 8, 6, 8)));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel title = new JLabel("🔒 Admin Girişi");
        title.setFont(UIConstants.FONT_SUBTITLE);
        title.setForeground(UIConstants.TEXT_PRIMARY);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        form.add(title, gbc);
        gbc.gridwidth = 1;

        JLabel lbl = new JLabel("Şifre:");
        lbl.setFont(UIConstants.FONT_BODY);
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        form.add(lbl, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        form.add(sifreField, gbc);

        form.setPreferredSize(new Dimension(300, 100));

        int sonuc = JOptionPane.showConfirmDialog(this, form,
            "Admin Girişi", JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);

        if (sonuc == JOptionPane.OK_OPTION) {
            String sifre = new String(sifreField.getPassword()).trim();
            if (ADMIN_SIFRE.equals(sifre)) {
                dispose();
                AdminFrame adminFrame = new AdminFrame(
                    musteriServisi, bilgisayarServisi,
                    odemeServisi, ortamServisi, raporServisi);
                adminFrame.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Yanlış şifre! Tekrar deneyin.",
                    "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void musteriGiris() {
        JTextField telefonField = new JTextField(18);
        telefonField.setFont(UIConstants.FONT_BODY);
        telefonField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 210, 240)),
            new EmptyBorder(6, 8, 6, 8)));

        JPasswordField sifreField = new JPasswordField(18);
        sifreField.setFont(UIConstants.FONT_BODY);
        sifreField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 210, 240)),
            new EmptyBorder(6, 8, 6, 8)));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel title = new JLabel("👤 Müşteri Girişi");
        title.setFont(UIConstants.FONT_SUBTITLE);
        title.setForeground(UIConstants.TEXT_PRIMARY);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        form.add(title, gbc);
        gbc.gridwidth = 1;

        JLabel lblTel = new JLabel("Telefon No:");
        lblTel.setFont(UIConstants.FONT_BODY);
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        form.add(lblTel, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        form.add(telefonField, gbc);

        JLabel lblSifre = new JLabel("Şifre:");
        lblSifre.setFont(UIConstants.FONT_BODY);
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        form.add(lblSifre, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        form.add(sifreField, gbc);

        JLabel info = new JLabel("<html><i style='color:gray;'>Telefon ve şifrenizi girin.<br>Kayıtlı değilseniz Admin'e başvurun.</i></html>");
        info.setFont(UIConstants.FONT_SMALL);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        form.add(info, gbc);

        form.setPreferredSize(new Dimension(340, 160));

        int sonuc = JOptionPane.showConfirmDialog(this, form,
            "Müşteri Girişi", JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);

        if (sonuc == JOptionPane.OK_OPTION) {
            String telefon = telefonField.getText().trim();
            String sifre = new String(sifreField.getPassword()).trim();

            if (telefon.isEmpty() || sifre.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Telefon ve şifre boş olamaz!",
                    "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Musteri musteri = musteriServisi.musteriGiris(telefon, sifre);
            if (musteri != null) {
                dispose();
                MusteriFrame musteriFrame = new MusteriFrame(
                    musteri, musteriServisi, bilgisayarServisi,
                    odemeServisi, ortamServisi, raporServisi);
                musteriFrame.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Giriş başarısız! Bilgilerinizi kontrol edin.",
                    "Giriş Başarısız", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}
