import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class LeftCanvas extends JPanel {
    private java.util.List<CreationItem> items = new ArrayList<>();
    private CreationItem selectedItem = null;
    private Point prevMousePoint;

    public LeftCanvas() {
        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                for (CreationItem item : items) {
                    if (item instanceof InteractiveItem && ((InteractiveItem) item).contains(e.getPoint())) {
                        selectedItem = item;
                        prevMousePoint = e.getPoint();
                        break;
                    }
                }
            }

            public void mouseReleased(MouseEvent e) {
                selectedItem = null;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (selectedItem != null && prevMousePoint != null) {
                    int dx = e.getX() - prevMousePoint.x;
                    int dy = e.getY() - prevMousePoint.y;
                    selectedItem.move(dx, dy);
                    prevMousePoint = e.getPoint();
                    repaint();
                }
            }
        });
    }

    public void addItem(CreationItem item) {
        items.add(item);
        repaint();
    }

    public void clearCanvas() {
        items.clear();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (CreationItem item : items) {
            item.draw(g);
        }
    }
}