import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class DrawingCanvas extends JPanel {
    private ArrayList<Point> points = new ArrayList<>();
    private Color penColor = Color.BLACK;
    private int strokeSize = 2;

    public DrawingCanvas() {
        setBackground(Color.WHITE);
        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                points.add(e.getPoint());
                repaint();
            }
        });
    }

    public void clearCanvas() {
        points.clear();
        repaint();
    }

    public void setPenColor(Color color) {
        this.penColor = color;
    }

    public void setStrokeSize(int size) {
        this.strokeSize = size;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(penColor);
        ((Graphics2D) g).setStroke(new BasicStroke(strokeSize));
        for (int i = 1; i < points.size(); i++) {
            Point p1 = points.get(i - 1);
            Point p2 = points.get(i);
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
    }
}
