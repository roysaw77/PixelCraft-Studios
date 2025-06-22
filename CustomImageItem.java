import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class CustomImageItem extends CreationItem {
    private BufferedImage image;

    public CustomImageItem(int x, int y, BufferedImage image) {
        super(x, y);
        this.image = image;
        if (image != null) {
            this.width = image.getWidth();
            this.height = image.getHeight();
        } else {
            // Handle null image case, e.g., set default size
            this.width = 50;
            this.height = 50;
            System.err.println("CustomImageItem created with null image.");
        }
    }

    @Override
    protected void drawContent(Graphics2D g2d) {
        if (image != null) {
            g2d.drawImage(image, 0, 0, this.width, this.height, null);
        } else {
            // Draw a placeholder if image is null
            g2d.setColor(java.awt.Color.GRAY);
            g2d.fillRect(0, 0, this.width, this.height);
            g2d.setColor(java.awt.Color.BLACK);
            g2d.drawString("Custom", 5, this.height / 2);
        }
    }
}