import java.awt.Graphics2D;

public interface DrawableItem {
    void draw(Graphics2D g2d);
    void rotate(double angle);
    void flip();
    void scale(double factor);
    void transpose(int dx, int dy);
    // boolean contains(java.awt.Point p); // Consider adding if needed at interface level
}
