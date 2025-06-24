import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class SaveDrawingAction extends AbstractAction {
    private JFrame owner;
    private CanvasPanel drawingCanvas;
    private List<BufferedImage> library;

    public SaveDrawingAction(String text, ImageIcon icon, JFrame owner, CanvasPanel drawingCanvas, List<BufferedImage> library) {
        super(text, icon);
        this.owner = owner;
        this.drawingCanvas = drawingCanvas;
        this.library = library;
        putValue(SHORT_DESCRIPTION, "Save custom drawing to a file and add to library");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Drawing As");
        fileChooser.setSelectedFile(new File("my_drawing.png"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("PNG Image", "png"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("JPEG Image", "jpg", "jpeg"));

        if (fileChooser.showSaveDialog(owner) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                String filePath = file.getAbsolutePath();
                String ext = "";
                String description = fileChooser.getFileFilter().getDescription();
                if (description.contains("PNG")) ext = "png";
                else if (description.contains("JPEG")) ext = "jpg";
                else if (!filePath.toLowerCase().endsWith(".png") && !filePath.toLowerCase().endsWith(".jpg")) {
                    ext = "png"; // Default to PNG
                    file = new File(filePath + "." + ext);
                }

                BufferedImage drawing = drawingCanvas.getFreehandImage();
                if (drawing == null) {
                    JOptionPane.showMessageDialog(owner, "Nothing to save.", "Save Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                ImageIO.write(drawing, ext, file);

                library.add(drawing);
                JOptionPane.showMessageDialog(owner, "Drawing saved to " + file.getName() + " and added to library.", "Save Successful", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(owner, "Error saving file: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}