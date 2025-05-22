import javax.swing.*;
import java.awt.*;

public class AnimalItem implements InteractiveItem {
    private Image image;
    private int x, y;
    private double rotation;

    public AnimalItem(String path, int x, int y) {
        this.image = new ImageIcon(path).getImage();
        this.x = x;
        this.y = y;
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        int w = image.getWidth(null);
        int h = image.getHeight(null);
        g2d.rotate(Math.toRadians(rotation), x + w / 2, y + h / 2);
        g2d.drawImage(image, x, y, null);
        g2d.dispose();
    }

    @Override
    public void rotate(double angle) {
        this.rotation += angle;
    }

    @Override
    public void flip() {
        // Implement horizontal flip if needed
    }

    @Override
    public void scale(double factor) {
        // Implement image scaling if needed
    }

    @Override
    public void move(int dx, int dy) {
        this.x += dx;
        this.y += dy;
    }

    @Override
    public boolean contains(Point p) {
        Rectangle bounds = new Rectangle(x, y, image.getWidth(null), image.getHeight(null));
        return bounds.contains(p);
    }
}
