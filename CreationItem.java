import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.Shape;
import java.awt.Point;


public abstract class CreationItem implements DrawableItem {
    protected int x, y; // Top-left position
    protected int width, height; // Base dimensions of the item
    protected double angle; // Rotation angle in degrees
    protected double scaleFactor; // Uniform scaling factor
    protected boolean flippedX;
    protected boolean flippedY;

    public CreationItem(int x, int y) {
        this.x = x;
        this.y = y;
        this.angle = 0;
        this.scaleFactor = 1.0;
        this.flippedX = false;
        this.flippedY = false;
        // width and height should be set by subclasses, typically from image dimensions
        // or user input. Initialize to a small default to avoid issues if not set.
        this.width = 1;
        this.height = 1;
    }

    // Abstract method for subclasses to implement their specific drawing logic
    // The Graphics2D context passed here will be relative to the item's (0,0)
    // Subclasses should draw their content using this.width and this.height as their bounds.
    protected abstract void drawContent(Graphics2D g2d);


    @Override
    public void draw(Graphics2D g2d) {
        if (width <= 0 || height <= 0) return; // Nothing to draw if dimensions are invalid

        Graphics2D g = (Graphics2D) g2d.create(); // Create a copy to not affect other drawings

        // Prepare transformation
        AffineTransform tx = new AffineTransform();

        // 1. Translate to the item's top-left corner on the canvas
        tx.translate(x, y);

        // 2. Translate to the center of the item (using base width/height) for rotation and scaling
        tx.translate(width / 2.0, height / 2.0);

        // 3. Apply rotation
        tx.rotate(Math.toRadians(angle));

        // 4. Apply scaling and flipping
        tx.scale(scaleFactor * (flippedX ? -1 : 1), scaleFactor * (flippedY ? -1 : 1));

        // 5. Translate back from the center so drawing happens from the item's effective top-left
        //    relative to its own coordinate system (0,0) to (width,height).
        tx.translate(-width / 2.0, -height / 2.0);

        g.transform(tx);

        // Subclasses draw their content within a bounding box of (0,0) to (this.width, this.height)
        drawContent(g);

        g.dispose(); // Dispose of the copy
    }


    @Override
    public void rotate(double angleDelta) {
        this.angle += angleDelta;
        while (this.angle >= 360.0) {
            this.angle -= 360.0;
        }
        while (this.angle < 0.0) {
            this.angle += 360.0;
        }
    }

    @Override
    public void flip() { // Default flip: horizontal
        this.flippedX = !this.flippedX;
    }

    public void flipHorizontal() {
        this.flippedX = !this.flippedX;
    }

    public void flipVertical() {
        this.flippedY = !this.flippedY;
    }

    @Override
    public void scale(double factor) { // factor is multiplicative e.g. 1.1 or 0.9
        this.scaleFactor *= factor;
        if (this.scaleFactor < 0.05) this.scaleFactor = 0.05; // Min scale
        if (this.scaleFactor > 10.0) this.scaleFactor = 10.0; // Max scale
    }

    public void setScale(double absoluteScale) {
        this.scaleFactor = absoluteScale;
         if (this.scaleFactor < 0.05) this.scaleFactor = 0.05;
        if (this.scaleFactor > 10.0) this.scaleFactor = 10.0;
    }

    public void setRotationAngle(double angle) {
        this.angle = angle;
        while (this.angle >= 360.0) {
            this.angle -= 360.0;
        }
        while (this.angle < 0.0) {
            this.angle += 360.0;
        }
    }

    public void setDimensions(int w, int h) {
        // These are base dimensions. Subclasses (like image items)
        // will use these for drawing their content.
        this.width = w > 0 ? w : 1;
        this.height = h > 0 ? h : 1;
    }

    @Override
    public void transpose(int dx, int dy) { // Move the item
        this.x += dx;
        this.y += dy;
    }

    public Rectangle getBounds() {
        Rectangle localBounds = new Rectangle(0, 0, width, height);
        AffineTransform tx = new AffineTransform();
        tx.translate(x, y);
        tx.translate(width / 2.0, height / 2.0);
        tx.rotate(Math.toRadians(angle));
        tx.scale(scaleFactor * (flippedX ? -1 : 1), scaleFactor * (flippedY ? -1 : 1));
        tx.translate(-width / 2.0, -height / 2.0);
        Shape transformedShape = tx.createTransformedShape(localBounds);
        return transformedShape.getBounds();
    }

    public boolean contains(Point p) {
        Rectangle localBounds = new Rectangle(0, 0, width, height);
        AffineTransform tx = new AffineTransform();
        tx.translate(x, y);
        tx.translate(width / 2.0, height / 2.0);
        tx.rotate(Math.toRadians(angle));
        tx.scale(scaleFactor * (flippedX ? -1 : 1), scaleFactor * (flippedY ? -1 : 1));
        tx.translate(-width / 2.0, -height / 2.0);
        Shape transformedShape = tx.createTransformedShape(localBounds);
        return transformedShape.contains(p);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getEffectiveWidth() { return (int)(width * scaleFactor); }
    public int getEffectiveHeight() { return (int)(height * scaleFactor); }
    public double getRotationAngle() { return angle; }

    public double getScaleFactor() { // Added for mouse-driven scaling
        return this.scaleFactor;
    }
}