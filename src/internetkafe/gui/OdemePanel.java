package internetkafe.gui;

import internetkafe.model.OdemeKaydi;
import internetkafe.servis.BilgisayarServisi;
import internetkafe.servis.MusteriServisi;
import internetkafe.servis.OdemeServisi;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OdemePanel extends JPanel {

    private final OdemeServisi      odemeServisi;
    private final MusteriServisi    musteriServisi;
    private final BilgisayarServisi bilgisayarServisi;

    private DefaultTableModel tableModel;
    private JLabel            gelirLabel;

    private static final String[] SUTUNLAR = {
        "Ödeme ID", "Müşteri ID", "Tutar (TL)", "Tip", "Zaman"
    };

    public OdemePanel(OdemeServisi os, MusteriServisi ms, BilgisayarServisi bs) {
        this.odemeServisi      = os;
        this.musteriServisi    = ms;
        this.bilgisayarServisi = bs;
        setLayout(new BorderLayout());
        setBackground(UIConstants.CONTENT_BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(headerOlustur(), BorderLayout.NORTH);
        add(odemeFormu(),    BorderLayout.WEST);
        add(tabloOlustur(),  BorderLayout.CENTER);
        tabloyiYenile();
    }

    private JPanel headerOlustur() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UIConstants.CONTENT_BG);
        p.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel baslik = new JLabel("💳  Ödeme İşlemleri");
        baslik.setFont(UIConstants.FONT_TITLE);
        baslik.setForeground(UIConstants.TEXT_PRIMARY);

        gelirLabel = new JLabel("Günlük Gelir: 0,00 TL");
        gelirLabel.setFont(UIConstants.FONT_SUBTITLE);
        gelirLabel.setForeground(UIConstants.SUCCESS_COLOR);

        p.add(baslik,    BorderLayout.WEST);
        p.add(gelirLabel, BorderLayout.EAST);
        return p;
    }

    private JPanel odemeFormu() {
        JPanel kart = new JPanel();
        kart.setLayout(new BoxLayout(kart, BoxLayout.Y_AXIS));
        kart.setBackground(Color.WHITE);
        kart.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 225, 245)),
            new EmptyBorder(20, 20, 20, 20)
        ));
        kart.setPreferredSize(new Dimension(240, 0));

        JLabel title = new JLabel("Ödeme Al");
        title.setFont(UIConstants.FONT_SUBTITLE);
        title.setForeground(UIConstants.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbId = new JLabel("Müşteri ID:");
        lbId.setFont(UIConstants.FONT_BODY);
        lbId.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextField idField = new JTextField();
        idField.setFont(UIConstants.FONT_BODY);
        idField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        idField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 210, 240)),
            new EmptyBorder(4, 8, 4, 8)));

        JLabel lbTip = new JLabel("Ödeme Tipi:");
        lbTip.setFont(UIConstants.FONT_BODY);
        lbTip.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] tipler = { OdemeKaydi.TIP_BAKIYE, OdemeKaydi.TIP_NAKIT, OdemeKaydi.TIP_KART };
        JComboBox<String> tipBox = new JComboBox<>(tipler);
        tipBox.setFont(UIConstants.FONT_BODY);
        tipBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        JButton btnOde = UIConstants.butonOlustur("💳 Ödemeyi Al", UIConstants.HEADER_BG);
        btnOde.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnOde.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnOde.addActionListener(e -> {
            try {
                int    id  = Integer.parseInt(idField.getText().trim());
                String tip = (String) tipBox.getSelectedItem();
                boolean ok = odemeServisi.odemeAl(id, tip);
                if (ok) {
                    JOptionPane.showMessageDialog(this,
                        "✅ Ödeme başarıyla alındı.", "Başarılı",
                        JOptionPane.INFORMATION_MESSAGE);
                    tabloyiYenile();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "❌ Ödeme alınamadı.\nAktif oturum yok veya yetersiz bakiye.",
                        "Hata", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                    "Geçerli bir Müşteri ID girin.", "Hata",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnSifirla = UIConstants.butonOlustur("🔄 Günü Sıfırla", UIConstants.DANGER_COLOR);
        btnSifirla.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnSifirla.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSifirla.addActionListener(e -> {
            odemeServisi.gunuSifirla();
            tabloyiYenile();
        });

        kart.add(title);
        kart.add(Box.createVerticalStrut(16));
        kart.add(lbId);
        kart.add(Box.createVerticalStrut(4));
        kart.add(idField);
        kart.add(Box.createVerticalStrut(12));
        kart.add(lbTip);
        kart.add(Box.createVerticalStrut(4));
        kart.add(tipBox);
        kart.add(Box.createVerticalStrut(16));
        kart.add(btnOde);
        kart.add(Box.createVerticalStrut(8));
        kart.add(btnSifirla);
        kart.add(Box.createVerticalGlue());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UIConstants.CONTENT_BG);
        wrapper.setBorder(new EmptyBorder(0, 0, 0, 15));
        wrapper.add(kart, BorderLayout.CENTER);
        return wrapper;
    }

    private JScrollPane tabloOlustur() {
        tableModel = new DefaultTableModel(SUTUNLAR, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tablo = new JTable(tableModel);
        tablo.setFont(UIConstants.FONT_TABLE);
        tablo.setRowHeight(32);
        tablo.setGridColor(new Color(230, 235, 245));
        tablo.setShowVerticalLines(false);
        tablo.setSelectionBackground(new Color(74, 108, 247, 40));
        tablo.getTableHeader().setFont(UIConstants.FONT_TABLE_HDR);
        tablo.getTableHeader().setBackground(UIConstants.TABLE_HEADER_BG);
        tablo.getTableHeader().setForeground(Color.WHITE);
        tablo.getTableHeader().setPreferredSize(new Dimension(0, 36));
        tablo.getTableHeader().setReorderingAllowed(false);

        tablo.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setHorizontalAlignment(CENTER);
                setBorder(new EmptyBorder(0, 5, 0, 5));
                if (!sel) {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 247, 255));
                }
                String v = val != null ? val.toString() : "";
                setForeground(switch (v) {
                    case "Bakiye" -> UIConstants.INFO_COLOR;
                    case "Nakit"  -> UIConstants.SUCCESS_COLOR;
                    case "Kart"   -> UIConstants.HEADER_BG;
                    default       -> UIConstants.TEXT_PRIMARY;
                });
                setFont(UIConstants.FONT_TABLE.deriveFont(Font.BOLD));
                return this;
            }
        });

        tablo.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setBorder(new EmptyBorder(0, 8, 0, 8));
                setHorizontalAlignment(col == 2 ? CENTER : LEFT);
                if (!sel) {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 247, 255));
                    setForeground(UIConstants.TEXT_PRIMARY);
                }
                setFont(UIConstants.FONT_TABLE);
                return this;
            }
        });

        JScrollPane sp = new JScrollPane(tablo);
        sp.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(210, 220, 240)),
            "  Ödeme Geçmişi"));
        return sp;
    }

    public void tabloyiYenile() {
        tableModel.setRowCount(0);
        List<OdemeKaydi> liste = odemeServisi.tumOdemeler();
        for (OdemeKaydi o : liste) {
            tableModel.addRow(new Object[]{
                o.getOdemeId(),
                "#" + o.getMusteriId(),
                String.format("%.2f", o.getTutar()),
                o.getOdemeTipi(),
                o.getOdemeZamani().toLocalTime().toString().substring(0, 5)
            });
        }
        gelirLabel.setText(String.format("Günlük Gelir: %.2f TL",
            odemeServisi.getGunlukGelir()));
    }
}
