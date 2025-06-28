import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public interface DrawableItem {
    void draw(Graphics2D g2d);
    void rotate(double angle);
    void flip();
    void scale(double factor);
    void transpose(int dx, int dy);
}

abstract class CreationItem implements DrawableItem {
    protected int x, y;
    protected int width, height;
    protected double angle;
    protected double scaleFactor;
    protected boolean flippedX, flippedY;

    public CreationItem(int x, int y) {
        this.x = x; this.y = y;
        this.angle = 0; this.scaleFactor = 1.0;
        this.flippedX = false; this.flippedY = false;
        this.width = 1; this.height = 1;
    }

    protected abstract void drawContent(Graphics2D g2d);

    @Override
    public void draw(Graphics2D g2d) {
        if (width <= 0 || height <= 0) return;
        Graphics2D g = (Graphics2D) g2d.create();
        AffineTransform tx = new AffineTransform();
        tx.translate(x, y);
        tx.translate(width / 2.0, height / 2.0);
        tx.rotate(Math.toRadians(angle));
        tx.scale(scaleFactor * (flippedX ? -1 : 1), scaleFactor * (flippedY ? -1 : 1));
        tx.translate(-width / 2.0, -height / 2.0);
        g.transform(tx);
        drawContent(g);
        g.dispose();
    }

    @Override public void rotate(double angleDelta) { this.angle = (this.angle + angleDelta) % 360; }
    @Override public void flip() { this.flippedX = !this.flippedX; }
    @Override public void scale(double factor) { this.scaleFactor *= factor; }
    @Override public void transpose(int dx, int dy) { this.x += dx; this.y += dy; }
    public void setRotationAngle(double angle) { this.angle = angle % 360; }
    public double getRotationAngle() { return angle; }

    public Rectangle getBounds() {
        Shape transformedShape = createTransformedShape(new Rectangle(0, 0, width, height));
        return transformedShape.getBounds();
    }

    public boolean contains(Point p) {
        return createTransformedShape(new Rectangle(0, 0, width, height)).contains(p);
    }

    private Shape createTransformedShape(Shape localShape) {
        AffineTransform tx = new AffineTransform();
        tx.translate(x, y);
        tx.translate(width / 2.0, height / 2.0);
        tx.rotate(Math.toRadians(angle));
        tx.scale(scaleFactor * (flippedX ? -1 : 1), scaleFactor * (flippedY ? -1 : 1));
        tx.translate(-width / 2.0, -height / 2.0);
        return tx.createTransformedShape(localShape);
    }
}

class CustomImageItem extends CreationItem {
    private BufferedImage image;
    public CustomImageItem(int x, int y, BufferedImage image) {
        super(x, y);
        this.image = image;
        if (image != null) {
            this.width = image.getWidth();
            this.height = image.getHeight();
        } else {
            this.width = 50; this.height = 50;
        }
    }

    @Override
    protected void drawContent(Graphics2D g2d) {
        if (image != null) {
            g2d.drawImage(image, 0, 0, this.width, this.height, null);
        } else {
            g2d.setColor(java.awt.Color.GRAY);
            g2d.fillRect(0, 0, this.width, this.height);
        }
    }
}

class AnimalItem extends CreationItem {
    private BufferedImage image;
    public AnimalItem(int x, int y, String resourcePath, int width, int height) {
        super(x, y);
        this.width = width;
        this.height = height;
        try {
            java.net.URL imageUrl = getClass().getResource(resourcePath);
            if (imageUrl == null) throw new IOException("Resource not found: " + resourcePath);
            this.image = ImageIO.read(imageUrl);
        } catch (IOException e) {
            this.image = null;
            System.err.println("Error loading image: " + e.getMessage());
        }
    }
    @Override
    protected void drawContent(Graphics2D g2d) {
        if (image != null) {
            g2d.drawImage(image, 0, 0, this.width, this.height, null);
        } else {
            g2d.setColor(java.awt.Color.RED);
            g2d.fillRect(0, 0, this.width, this.height);
            g2d.setColor(java.awt.Color.WHITE);
            g2d.drawString("ERR", 5, 20);
        }
    }
    @Override
    public void flip() { this.flippedX = !this.flippedX; }
    @Override
    public void scale(double factor) { }
}

class FlowerItem extends CreationItem {
    private BufferedImage image;
    public FlowerItem(int x, int y, String resourcePath, int width, int height) {
        super(x, y);
        this.width = width;
        this.height = height;
        try {
            java.net.URL imageUrl = getClass().getResource(resourcePath);
            if (imageUrl == null) throw new IOException("Resource not found: " + resourcePath);
            this.image = ImageIO.read(imageUrl);
        } catch (IOException e) {
            this.image = null;
            System.err.println("Error loading image: " + e.getMessage());
        }
    }
    @Override
    protected void drawContent(Graphics2D g2d) {
        if (image != null) {
            g2d.drawImage(image, 0, 0, this.width, this.height, null);
        } else {
            g2d.setColor(java.awt.Color.RED);
            g2d.fillRect(0, 0, this.width, this.height);
            g2d.setColor(java.awt.Color.WHITE);
            g2d.drawString("ERR", 5, 20);
        }
    }
    @Override
    public void scale(double factor) { this.scaleFactor *= factor; }
    @Override
    public void flip() {}
}