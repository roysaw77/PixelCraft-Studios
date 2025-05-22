public interface CreationItem {
    void draw(java.awt.Graphics g);
    void rotate(double angle);
    void flip();
    void scale(double factor);
    void move(int dx, int dy);
}
