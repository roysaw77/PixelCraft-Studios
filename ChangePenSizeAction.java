import javax.swing.*;
import java.awt.event.ActionEvent;

public class ChangePenSizeAction extends AbstractAction {
    private JFrame owner;
    private CanvasPanel drawingCanvas;

    public ChangePenSizeAction(String text, ImageIcon icon, JFrame owner, CanvasPanel drawingCanvas) {
        super(text, icon);
        this.owner = owner;
        this.drawingCanvas = drawingCanvas;
        putValue(SHORT_DESCRIPTION, "Adjust pen stroke size for free style drawing");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String sizeStr = JOptionPane.showInputDialog(owner, "Enter stroke size (e.g., 2):", "2");
        if (sizeStr != null) {
            try {
                int size = Integer.parseInt(sizeStr);
                if (size > 0 && size < 50) { // Basic validation
                    drawingCanvas.setStrokeSize(size);
                } else {
                    JOptionPane.showMessageDialog(owner, "Stroke size must be between 1 and 49.", "Invalid Size", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(owner, "Invalid number entered for stroke size.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}