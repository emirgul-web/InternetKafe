package internetkafe.gui;

import java.awt.*;

public class UIConstants {

    public static final Color SIDEBAR_BG      = new Color(30, 39, 73);
    public static final Color SIDEBAR_HOVER   = new Color(44, 56, 100);
    public static final Color SIDEBAR_ACTIVE  = new Color(74, 108, 247);

    public static final Color CONTENT_BG      = new Color(245, 246, 250);
    public static final Color CARD_BG         = Color.WHITE;
    public static final Color HEADER_BG       = new Color(74, 108, 247);

    public static final Color SUCCESS_COLOR   = new Color(46, 213, 115);
    public static final Color DANGER_COLOR    = new Color(255, 71, 87);
    public static final Color WARNING_COLOR   = new Color(255, 165, 2);
    public static final Color INFO_COLOR      = new Color(83, 167, 255);

    public static final Color TABLE_ROW_EVEN  = Color.WHITE;
    public static final Color TABLE_ROW_ODD   = new Color(245, 247, 255);
    public static final Color TABLE_HEADER_BG = new Color(30, 39, 73);
    public static final Color TABLE_HEADER_FG = Color.WHITE;
    public static final Color TABLE_SELECT_BG = new Color(74, 108, 247, 60);

    public static final Color TEXT_PRIMARY    = new Color(30, 39, 73);
    public static final Color TEXT_SECONDARY  = new Color(120, 130, 160);
    public static final Color TEXT_LIGHT      = Color.WHITE;

    public static final Font FONT_TITLE       = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE    = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_BODY        = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL       = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BUTTON      = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_SIDEBAR     = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_TABLE       = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_TABLE_HDR   = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_MONO        = new Font("Consolas", Font.PLAIN, 13);

    public static final int  SIDEBAR_WIDTH    = 220;
    public static final int  WINDOW_WIDTH     = 1100;
    public static final int  WINDOW_HEIGHT    = 700;
    public static final int  PADDING          = 20;
    public static final int  CARD_ARC         = 12;

    private UIConstants() { }

    public static javax.swing.JButton butonOlustur(String metin, Color renk) {
        javax.swing.JButton btn = new javax.swing.JButton(metin);
        btn.setFont(FONT_BUTTON);
        btn.setBackground(renk);
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 36));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(renk.brighter());
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(renk);
            }
        });
        return btn;
    }
}
