package main.ui;

import main.config.SoundManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BunkerButton extends JButton {
    private Color normalBg = new Color(30, 30, 36);
    private Color hoverBg = new Color(199, 0, 57);
    private Color borderColor = new Color(255, 87, 51);
    private Color textColor = new Color(240, 240, 240);
    private boolean isHovered = false;

    public BunkerButton(String text) {
        super(text);
        setFont(new Font("SansSerif", Font.BOLD, 18));
        setForeground(textColor);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(320, 50));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                SoundManager.playSoundEffect("assets/sounds/click.wav");
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Фон з градієнтом та ефектом наведення
        if (isHovered) {
            g2.setPaint(new GradientPaint(0, 0, hoverBg, width, height, normalBg));
        } else {
            g2.setPaint(new GradientPaint(0, 0, normalBg, 0, height, new Color(20, 20, 24)));
        }
        g2.fillRoundRect(0, 0, width, height, 12, 12);

        // Рамка кнопки
        g2.setColor(isHovered ? Color.WHITE : borderColor);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(1, 1, width - 2, height - 2, 12, 12);

        // Текст кнопки
        FontMetrics fm = g2.getFontMetrics();
        int textX = (width - fm.stringWidth(getText())) / 2;
        int textY = (height + fm.getAscent() - fm.getDescent()) / 2;

        g2.setColor(isHovered ? Color.WHITE : textColor);
        g2.drawString(getText(), textX, textY);

        g2.dispose();
    }
}
