import javax.swing.*;
import java.awt.event.ActionEvent;

public class ClearCanvasAction extends AbstractAction {
    private CanvasPanel canvas;

    public ClearCanvasAction(String text, ImageIcon icon, CanvasPanel canvas) {
        super(text, icon);
        this.canvas = canvas;
        putValue(SHORT_DESCRIPTION, "Clear the " + (canvas.isCompositionCanvas() ? "composition" : "drawing") + " canvas");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        canvas.clear();
    }
}