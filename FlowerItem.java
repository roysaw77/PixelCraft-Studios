import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class FlowerItem extends CreationItem {
    private BufferedImage image;

    public FlowerItem(int x, int y, String resourcePath) {
        super(x, y);
        loadImage(resourcePath);
    }

    // Constructor to set initial dimensions (Req 2)
    public FlowerItem(int x, int y, String resourcePath, int desiredWidth, int desiredHeight) {
        super(x, y);
        loadImage(resourcePath);
        if (this.image != null) {
            double originalWidth = this.width;
            double originalHeight = this.height;
            if (originalWidth > 0 && originalHeight > 0 && desiredWidth > 0 && desiredHeight > 0) {
                double scaleX = (double) desiredWidth / originalWidth;
                double scaleY = (double) desiredHeight / originalHeight;
                this.scaleFactor = Math.min(scaleX, scaleY);
            }
        } else {
            this.width = desiredWidth > 0 ? desiredWidth : 50;
            this.height = desiredHeight > 0 ? desiredHeight : 50;
            this.scaleFactor = 1.0;
        }
    }

    private void loadImage(String resourcePath) {
        try {
            java.net.URL imageUrl = getClass().getResource(resourcePath);
            if (imageUrl == null) {
                System.err.println("Flower resource not found: " + resourcePath);
                setDefaultSizeOnError();
                return;
            }
            image = ImageIO.read(imageUrl);
            if (image != null) {
                this.width = image.getWidth();
                this.height = image.getHeight();
            } else {
                System.err.println("Failed to load flower image (ImageIO.read returned null): " + resourcePath);
                setDefaultSizeOnError();
            }
        } catch (IOException e) {
            System.err.println("IOException loading flower image: " + resourcePath);
            e.printStackTrace(); // Print stack trace for better debugging
            setDefaultSizeOnError();
        }
    }

    private void setDefaultSizeOnError() {
        this.image = null;
        this.width = 50;
        this.height = 50;
    }

    @Override
    protected void drawContent(Graphics2D g2d) {
        if (image != null) {
            g2d.drawImage(image, 0, 0, this.width, this.height, null);
        } else {
            g2d.setColor(java.awt.Color.PINK);
            g2d.fillRect(0, 0, this.width, this.height);
            g2d.setColor(java.awt.Color.BLACK);
            g2d.drawString("Flower", 5, this.height / 2);
        }
    }
}