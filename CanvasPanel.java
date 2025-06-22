import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

public class CanvasPanel extends JPanel {
    private List<DrawableItem> items = new ArrayList<>();
    private BufferedImage freehandCanvas;
    private Graphics2D freehandGraphics;
    private Color penColor = Color.BLACK;
    private int strokeSize = 2;
    private Point lastPoint;
    private DrawableItem selectedItem = null;
    private Point dragStartPoint = null;
    private boolean isCompositionCanvas;
    private double canvasRotationAngle = 0.0;
    private PropertyChangeSupport support;

    public CanvasPanel(int width, int height, boolean isFreehand) {
        setPreferredSize(new Dimension(width, height));
        setMinimumSize(new Dimension(200, 200)); // prevent collapse
        this.isCompositionCanvas = !isFreehand;
        support = new PropertyChangeSupport(this);

        if (!isCompositionCanvas) {
            freehandCanvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            freehandGraphics = freehandCanvas.createGraphics();
            freehandGraphics.setColor(Color.WHITE);
            freehandGraphics.fillRect(0, 0, width, height);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    lastPoint = e.getPoint();
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    Point currentPoint = e.getPoint();
                    freehandGraphics.setColor(penColor);
                    freehandGraphics.setStroke(new BasicStroke(strokeSize));
                    freehandGraphics.drawLine(lastPoint.x, lastPoint.y, currentPoint.x, currentPoint.y);
                    lastPoint = currentPoint;
                    repaint();
                }
            });

            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    int newWidth = getWidth();
                    int newHeight = getHeight();
                    if (newWidth <= 0 || newHeight <= 0) return;

                    BufferedImage newDrawingSurface = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2 = newDrawingSurface.createGraphics();
                    g2.setColor(Color.WHITE);
                    g2.fillRect(0, 0, newWidth, newHeight);
                    if (freehandCanvas != null) {
                        g2.drawImage(freehandCanvas, 0, 0, null);
                    }
                    g2.dispose();
                    freehandCanvas = newDrawingSurface;
                    freehandGraphics = freehandCanvas.createGraphics();
                    freehandGraphics.setColor(penColor);
                    freehandGraphics.setStroke(new BasicStroke(strokeSize));
                }
            });

        } else {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    DrawableItem oldSelectedItem = selectedItem;
                    selectedItem = null;
                    for (int i = items.size() - 1; i >= 0; i--) {
                        DrawableItem item = items.get(i);
                        if (item instanceof CreationItem && ((CreationItem) item).contains(e.getPoint())) {
                            selectedItem = item;
                            dragStartPoint = e.getPoint();
                            // Move to top for rendering and selection priority
                            items.remove(item);
                            items.add(item);
                            repaint();
                            break; // Found an item
                        }
                    }
                    // Fire property change regardless of whether an item was found or not (could be deselection)
                    support.firePropertyChange("selectedItem", oldSelectedItem, selectedItem);
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    dragStartPoint = null;
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (selectedItem != null && dragStartPoint != null && selectedItem instanceof CreationItem) {
                        selectedItem.transpose(e.getX() - dragStartPoint.x, e.getY() - dragStartPoint.y);
                        dragStartPoint = e.getPoint();
                        repaint();
                    }
                }
            });
        }
    }

    public void addItem(DrawableItem item) {
        items.add(item);
        repaint();
    }

    public void clear() {
        items.clear();
        if (freehandCanvas != null) {
            freehandGraphics.setColor(Color.WHITE);
            freehandGraphics.setComposite(AlphaComposite.Src);
            freehandGraphics.fillRect(0, 0, freehandCanvas.getWidth(), freehandCanvas.getHeight());
        }
        repaint();
    }

    public List<DrawableItem> getItems() {
        return items;
    }

    public void setPenColor(Color color) {
        penColor = color;
    }

    public void setStrokeSize(int size) {
        strokeSize = size;
    }

    public BufferedImage getFreehandImage() {
        return freehandCanvas;
    }

    public BufferedImage getComposedImage() {
        BufferedImage composedImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = composedImage.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        if (canvasRotationAngle != 0) {
            AffineTransform tx = new AffineTransform();
            tx.rotate(Math.toRadians(canvasRotationAngle), getWidth() / 2.0, getHeight() / 2.0);
            g2d.transform(tx);
        }

        for (DrawableItem item : items) {
            item.draw(g2d);
        }
        g2d.dispose();
        return composedImage;
    }

    public void setCanvasRotationAngle(double angle) {
        this.canvasRotationAngle = angle;
        repaint();
    }

    public DrawableItem getSelectedItem() {
        return selectedItem;
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        AffineTransform oldTransform = g2d.getTransform();

        if (isCompositionCanvas && canvasRotationAngle != 0) {
            g2d.rotate(Math.toRadians(canvasRotationAngle), getWidth() / 2.0, getHeight() / 2.0);
        }

        if (freehandCanvas != null) {
            g2d.drawImage(freehandCanvas, 0, 0, null);
        }

        for (DrawableItem item : items) {
            item.draw(g2d);
        }

        if (isCompositionCanvas && selectedItem != null && selectedItem instanceof CreationItem) {
            Graphics2D gCopy = (Graphics2D) g2d.create();
            gCopy.setColor(Color.CYAN);
            gCopy.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{5.0f}, 0.0f));
            Rectangle bounds = ((CreationItem) selectedItem).getBounds();
            gCopy.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
            gCopy.dispose();
        }

        g2d.setTransform(oldTransform);
    }
}
