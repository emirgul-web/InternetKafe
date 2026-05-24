package internetkafe.gui;

import internetkafe.model.Musteri;
import internetkafe.servis.BilgisayarServisi;
import internetkafe.servis.MusteriServisi;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class MusteriPanel extends JPanel {

    private final MusteriServisi    musteriServisi;
    private final BilgisayarServisi bilgisayarServisi;

    private DefaultTableModel tableModel;
    private JTable            tablo;
    private JLabel            sayacLabel;

    private static final String[] SUTUNLAR = {
        "ID", "Ad", "Soyad", "Telefon", "Bakiye (TL)", "Durum"
    };

    public MusteriPanel(MusteriServisi musteriServisi, BilgisayarServisi bilgisayarServisi) {
        this.musteriServisi    = musteriServisi;
        this.bilgisayarServisi = bilgisayarServisi;
        setLayout(new BorderLayout());
        setBackground(UIConstants.CONTENT_BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(headerOlustur(),  BorderLayout.NORTH);
        add(tabloOlustur(),   BorderLayout.CENTER);
        add(butonlarOlustur(), BorderLayout.SOUTH);

        tabloyiYenile();

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                tabloyiYenile();
            }
        });
    }

    private JPanel headerOlustur() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIConstants.CONTENT_BG);
        panel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel baslik = new JLabel("👤  Müşteri Yönetimi");
        baslik.setFont(UIConstants.FONT_TITLE);
        baslik.setForeground(UIConstants.TEXT_PRIMARY);

        sayacLabel = new JLabel("Toplam: 0 müşteri");
        sayacLabel.setFont(UIConstants.FONT_SMALL);
        sayacLabel.setForeground(UIConstants.TEXT_SECONDARY);

        panel.add(baslik,    BorderLayout.WEST);
        panel.add(sayacLabel, BorderLayout.EAST);
        return panel;
    }

    private JScrollPane tabloOlustur() {
        tableModel = new DefaultTableModel(SUTUNLAR, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tablo = new JTable(tableModel);
        tablo.setFont(UIConstants.FONT_TABLE);
        tablo.setRowHeight(32);
        tablo.setGridColor(new Color(230, 232, 240));
        tablo.setShowVerticalLines(false);
        tablo.setSelectionBackground(new Color(74, 108, 247, 40));
        tablo.setSelectionForeground(UIConstants.TEXT_PRIMARY);
        tablo.setBackground(Color.WHITE);

        tablo.getTableHeader().setFont(UIConstants.FONT_TABLE_HDR);
        tablo.getTableHeader().setBackground(UIConstants.TABLE_HEADER_BG);
        tablo.getTableHeader().setForeground(Color.WHITE);
        tablo.getTableHeader().setPreferredSize(new Dimension(0, 38));
        tablo.getTableHeader().setReorderingAllowed(false);

        int[] genislikler = {40, 100, 100, 130, 100, 90};
        for (int i = 0; i < genislikler.length; i++) {
            tablo.getColumnModel().getColumn(i).setPreferredWidth(genislikler[i]);
        }

        tablo.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setHorizontalAlignment(col == 4 || col == 0 ? CENTER : LEFT);
                setBorder(new EmptyBorder(0, 10, 0, 10));

                if (!sel) {
                    setBackground(row % 2 == 0 ? UIConstants.TABLE_ROW_EVEN : UIConstants.TABLE_ROW_ODD);
                }
                if (col == 5) {
                    String deger = val != null ? val.toString() : "";
                    setForeground(deger.equals("İÇERİDE")
                        ? UIConstants.SUCCESS_COLOR : UIConstants.TEXT_SECONDARY);
                    setFont(UIConstants.FONT_TABLE.deriveFont(Font.BOLD));
                } else {
                    setForeground(UIConstants.TEXT_PRIMARY);
                    setFont(UIConstants.FONT_TABLE);
                }
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(tablo);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 240)));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    private JPanel butonlarOlustur() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setBackground(UIConstants.CONTENT_BG);
        panel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JButton btnYeni     = UIConstants.butonOlustur("➕ Yeni Müşteri",   UIConstants.HEADER_BG);
        JButton btnGiris    = UIConstants.butonOlustur("🔑 Giriş Yap",      UIConstants.SUCCESS_COLOR);
        JButton btnBakiye   = UIConstants.butonOlustur("💰 Bakiye Yükle",   UIConstants.WARNING_COLOR);
        JButton btnYenile   = UIConstants.butonOlustur("🔄 Yenile",         UIConstants.TEXT_SECONDARY);

        btnYeni.addActionListener(e   -> yeniMusteriDialog());
        btnGiris.addActionListener(e  -> girisDialog());
        btnBakiye.addActionListener(e -> bakiyeDialog());
        btnYenile.addActionListener(e -> tabloyiYenile());

        panel.add(btnYeni);
        panel.add(btnGiris);
        panel.add(btnBakiye);
        panel.add(btnYenile);
        return panel;
    }

    private void yeniMusteriDialog() {
        JTextField adField      = stilliField("Ad");
        JTextField soyadField   = stilliField("Soyad");
        JTextField telefonField = stilliField("05xx...");
        JTextField sifreField   = stilliField("");
        sifreField.setText("123");

        JPanel form = formPanel("Yeni Müşteri Kaydı",
            new String[]{"Ad:", "Soyad:", "Telefon:", "Şifre:"},
            new JTextField[]{adField, soyadField, telefonField, sifreField}
        );

        int sonuc = JOptionPane.showConfirmDialog(this, form,
            "Yeni Müşteri Ekle", JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);

        if (sonuc == JOptionPane.OK_OPTION) {
            String ad      = adField.getText().trim();
            String soyad   = soyadField.getText().trim();
            String telefon = telefonField.getText().trim();
            String sifre   = sifreField.getText().trim();

            if (ad.isEmpty() || soyad.isEmpty() || telefon.isEmpty() || sifre.isEmpty()) {
                hataGoster("Lütfen tüm alanları doldurun.");
                return;
            }
            Musteri m = musteriServisi.musteriOlustur(ad, soyad, telefon, sifre);
            if (m != null) {
                basariGoster("Müşteri başarıyla eklendi!\nID: " + m.getMusteriId());
                tabloyiYenile();
            } else {
                hataGoster("Bu telefon numarası zaten kayıtlı.");
            }
        }
    }

    private void girisDialog() {
        JTextField telField = stilliField("05xx...");
        JPasswordField sifreField = new JPasswordField(18);
        sifreField.setFont(UIConstants.FONT_BODY);
        sifreField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 210, 240)),
            new EmptyBorder(5, 8, 5, 8)
        ));

        JPanel form = formPanel("Müşteri Girişi",
            new String[]{"Telefon:", "Şifre:"},
            new JTextField[]{telField, sifreField}
        );

        int sonuc = JOptionPane.showConfirmDialog(this, form,
            "Müşteri Girişi", JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);

        if (sonuc == JOptionPane.OK_OPTION) {
            String tel = telField.getText().trim();
            String sifre = new String(sifreField.getPassword()).trim();

            if (tel.isEmpty() || sifre.isEmpty()) {
                hataGoster("Telefon ve şifre boş olamaz!");
                return;
            }

            Musteri m = musteriServisi.musteriGiris(tel, sifre);
            if (m != null) {
                basariGoster("Giriş başarılı!\n" + m.getAd() + " " + m.getSoyad()
                    + "\nMüşteri ID: " + m.getMusteriId());
                tabloyiYenile();
            } else {
                hataGoster("Giriş başarısız! Bilgileri kontrol edin.");
            }
        }
    }

    private void bakiyeDialog() {
        int seciliSatir = tablo.getSelectedRow();
        String baslangicId = "";
        if (seciliSatir >= 0) {
            baslangicId = tableModel.getValueAt(seciliSatir, 0).toString();
        }

        JTextField idField     = stilliField(baslangicId.isEmpty() ? "Müşteri ID" : baslangicId);
        if (!baslangicId.isEmpty()) idField.setText(baslangicId);
        JTextField miktarField = stilliField("Örn: 50");

        JPanel form = formPanel("Bakiye Yükleme",
            new String[]{"Müşteri ID:", "Miktar (TL):"},
            new JTextField[]{idField, miktarField}
        );

        int sonuc = JOptionPane.showConfirmDialog(this, form,
            "Bakiye Yükle", JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);

        if (sonuc == JOptionPane.OK_OPTION) {
            try {
                int    id     = Integer.parseInt(idField.getText().trim());
                double miktar = Double.parseDouble(miktarField.getText().trim().replace(",", "."));
                musteriServisi.bakiyeYukle(id, miktar);
                tabloyiYenile();
                basariGoster(String.format("%.2f TL başarıyla yüklendi.", miktar));
            } catch (NumberFormatException ex) {
                hataGoster("Geçerli bir ID ve miktar girin.");
            }
        }
    }

    public void tabloyiYenile() {
        tableModel.setRowCount(0);
        List<Musteri> liste = musteriServisi.tumMusterileriListele();

        for (Musteri m : liste) {
            boolean icerde = m.isAktif();
            if (!icerde && bilgisayarServisi != null
                    && bilgisayarServisi.aktifOturumGetir(m.getMusteriId()) != null) {
                icerde = true;
                m.setAktif(true);
            }

            tableModel.addRow(new Object[]{
                m.getMusteriId(),
                m.getAd(),
                m.getSoyad(),
                m.getTelefon(),
                String.format("%.2f", m.getBakiye()),
                icerde ? "İÇERİDE" : "DIŞARIDA"
            });
        }
        sayacLabel.setText("Toplam: " + liste.size() + " müşteri");
    }

    private JTextField stilliField(String placeholder) {
        JTextField f = new JTextField(18);
        f.setFont(UIConstants.FONT_BODY);
        f.setToolTipText(placeholder);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 210, 240)),
            new EmptyBorder(5, 8, 5, 8)
        ));
        return f;
    }

    private JPanel formPanel(String baslik, String[] etiketler, JTextField[] alanlar) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setPreferredSize(new Dimension(320, 30 + etiketler.length * 48));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(6, 6, 6, 6);
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.anchor  = GridBagConstraints.WEST;

        JLabel baslikLabel = new JLabel(baslik);
        baslikLabel.setFont(UIConstants.FONT_SUBTITLE);
        baslikLabel.setForeground(UIConstants.TEXT_PRIMARY);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(baslikLabel, gbc);
        gbc.gridwidth = 1;

        for (int i = 0; i < etiketler.length; i++) {
            gbc.gridx = 0; gbc.gridy = i + 1; gbc.weightx = 0.3;
            JLabel lbl = new JLabel(etiketler[i]);
            lbl.setFont(UIConstants.FONT_BODY);
            panel.add(lbl, gbc);

            gbc.gridx = 1; gbc.weightx = 0.7;
            panel.add(alanlar[i], gbc);
        }
        return panel;
    }

    private void basariGoster(String mesaj) {
        JOptionPane.showMessageDialog(this, mesaj, "Başarılı",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void hataGoster(String mesaj) {
        JOptionPane.showMessageDialog(this, mesaj, "Hata",
            JOptionPane.ERROR_MESSAGE);
    }
}
