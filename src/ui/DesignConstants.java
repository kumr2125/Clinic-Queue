package ui;

import javax.swing.BorderFactory;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Font;

/** One place for every color, font and padding used by the screens. */
public final class DesignConstants {
    private DesignConstants() {}

    public static final Color BACKGROUND = new Color(0xF4F8FB);
    public static final Color PRIMARY = new Color(0x1F7A8C);
    public static final Color PRIMARY_TEXT = Color.WHITE;
    public static final Color SUCCESS = new Color(0x2E7D32);
    public static final Color ERROR = new Color(0xC62828);
    public static final Color STATUS_TEXT = Color.WHITE;
    public static final Color INFO = new Color(0x37474F);

    public static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 20);
    public static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, 14);
    public static final Font STATUS_FONT = new Font("SansSerif", Font.BOLD, 13);

    public static final Font REPORT_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 15);

    public static final int PAD = 12;

    /** Empty padded border for panels and boxes. */
    public static Border padded() { return BorderFactory.createEmptyBorder(PAD, PAD, PAD, PAD); }

    /** Titled box with padding inside. */
    public static Border box(String title) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(PRIMARY), title),
                BorderFactory.createEmptyBorder(PAD / 2, PAD, PAD, PAD));
    }
}
