import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

public class MergeCanvasAction extends AbstractAction {
    private JFrame owner;
    private CanvasPanel canvas;
    private JSlider canvasRotationSlider;

    public MergeCanvasAction(String text, ImageIcon icon, JFrame owner, CanvasPanel canvas, JSlider canvasRotationSlider) {
        super(text, icon);
        this.owner = owner;
        this.canvas = canvas;
        this.canvasRotationSlider = canvasRotationSlider;
        putValue(SHORT_DESCRIPTION, "Merge all items on the left canvas into a single new image item");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (canvas.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(owner, "Left canvas is empty. Nothing to merge.", "Merge Canvas", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        BufferedImage mergedImage = canvas.getComposedImage();

        if (mergedImage != null) {
            CustomImageItem mergedItem = new CustomImageItem(0, 0, mergedImage);
            canvas.clear();
            canvas.setCanvasRotationAngle(0);
            canvasRotationSlider.setValue(0);
            canvas.addItem(mergedItem);
        } else {
            JOptionPane.showMessageDialog(owner, "Failed to create merged image.", "Merge Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}