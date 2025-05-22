import java.awt.*;

public class CustomImageItem implements InteractiveItem {
    private Image image;
    private int x, y;
    private double rotation;

    public CustomImageItem(Image image, int x, int y) {
        this.image = image;
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
    public void flip() {}

    @Override
    public void scale(double factor) {}

    @Override
    public void move(int dx, int dy) {
        this.x += dx;
        this.y += dy;
    }

    @Override
    public boolean contains(Point p) {
        return new Rectangle(x, y, image.getWidth(null), image.getHeight(null)).contains(p);
    }
}