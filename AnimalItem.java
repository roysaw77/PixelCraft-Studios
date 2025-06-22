import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class AnimalItem extends CreationItem {
    private BufferedImage image;

    public AnimalItem(int x, int y, String resourcePath) {
        super(x, y);
        loadImage(resourcePath);
    }

    // Constructor to set initial dimensions (Req 2)
    public AnimalItem(int x, int y, String resourcePath, int desiredWidth, int desiredHeight) {
        super(x, y);
        loadImage(resourcePath);
        if (this.image != null) {
            // Calculate scale factor to fit desiredWidth/Height, maintaining aspect ratio
            double originalWidth = this.width; // Original image width set by loadImage
            double originalHeight = this.height; // Original image height set by loadImage

            if (originalWidth > 0 && originalHeight > 0 && desiredWidth > 0 && desiredHeight > 0) {
                double scaleX = (double) desiredWidth / originalWidth;
                double scaleY = (double) desiredHeight / originalHeight;
                this.scaleFactor = Math.min(scaleX, scaleY); // Maintain aspect ratio
            }
        } else {
            // If image loading failed, use desired dimensions directly with scale 1
            this.width = desiredWidth > 0 ? desiredWidth : 50;
            this.height = desiredHeight > 0 ? desiredHeight : 50;
            this.scaleFactor = 1.0;
        }
    }

    private void loadImage(String resourcePath) {
        try {
            java.net.URL imageUrl = getClass().getResource(resourcePath);
            if (imageUrl == null) {
                System.err.println("Animal resource not found: " + resourcePath);
                setDefaultSizeOnError();
                return;
            }
            image = ImageIO.read(imageUrl);
            if (image != null) {
                this.width = image.getWidth();   // Set base width from image
                this.height = image.getHeight(); // Set base height from image
            } else {
                System.err.println("Failed to load animal image (ImageIO.read returned null): " + resourcePath);
                setDefaultSizeOnError();
            }
        } catch (IOException e) {
            System.err.println("IOException loading animal image: " + resourcePath);
            e.printStackTrace(); // Print stack trace for better debugging
            setDefaultSizeOnError();
        }
    }

    private void setDefaultSizeOnError() {
        this.image = null;
        this.width = 50; // Default base width on error
        this.height = 50; // Default base height on error
    }

    @Override
    protected void drawContent(Graphics2D g2d) {
        if (image != null) {
            // Draw the image from (0,0) up to (this.width, this.height)
            // The transformations in CreationItem.draw() handle the rest.
            g2d.drawImage(image, 0, 0, this.width, this.height, null);
        } else {
            // Draw a placeholder if image failed to load
            g2d.setColor(java.awt.Color.LIGHT_GRAY);
            g2d.fillRect(0, 0, this.width, this.height);
            g2d.setColor(java.awt.Color.BLACK);
            g2d.drawString("Animal", 5, this.height / 2);
        }
    }
}