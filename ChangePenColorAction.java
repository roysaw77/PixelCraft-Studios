import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ChangePenColorAction extends AbstractAction {
    private JFrame owner;
    private CanvasPanel drawingCanvas;

    public ChangePenColorAction(String text, ImageIcon icon, JFrame owner, CanvasPanel drawingCanvas) {
        super(text, icon);
        this.owner = owner;
        this.drawingCanvas = drawingCanvas;
        putValue(SHORT_DESCRIPTION, "Change pen color for free style drawing");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Color newColor = JColorChooser.showDialog(owner, "Choose Pen Color", drawingCanvas.getForeground());
        if (newColor != null) {
            drawingCanvas.setPenColor(newColor);
        }
    }
}