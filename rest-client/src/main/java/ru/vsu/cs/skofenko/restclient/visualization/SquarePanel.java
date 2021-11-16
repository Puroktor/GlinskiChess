package ru.vsu.cs.skofenko.restclient.visualization;

import javax.swing.*;
import java.awt.*;

class SquarePanel extends JPanel {
    @Override
    public Dimension getPreferredSize() {
        Dimension d;
        Container c = getParent();
        if (c != null) {
            d = c.getSize();
        } else {
            return new Dimension(10, 10);
        }
        int w = (int) d.getWidth();
        int h = (int) d.getHeight();
        int s = Math.min(w, h);
        return new Dimension(s, s);
    }
}
