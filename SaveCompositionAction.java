import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SaveCompositionAction extends AbstractAction {
    private JFrame owner;
    private CanvasPanel compositionCanvas;

    public SaveCompositionAction(String text, ImageIcon icon, JFrame owner, CanvasPanel compositionCanvas) {
        super(text, icon);
        this.owner = owner;
        this.compositionCanvas = compositionCanvas;
        putValue(SHORT_DESCRIPTION, "Save the current composition to a file");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Composition As");
        fileChooser.setSelectedFile(new File("my_composition.png"));
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

                BufferedImage composedImage = compositionCanvas.getComposedImage();
                if (composedImage == null) {
                    JOptionPane.showMessageDialog(owner, "Nothing to save in composition.", "Save Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                ImageIO.write(composedImage, ext, file);
                JOptionPane.showMessageDialog(owner, "Composition saved to " + file.getName(), "Save Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(owner, "Error saving composition: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}