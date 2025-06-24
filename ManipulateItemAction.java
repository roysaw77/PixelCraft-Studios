import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

public class ManipulateItemAction extends AbstractAction {
    private CanvasPanel canvas;
    private Consumer<DrawableItem> operation;

    public ManipulateItemAction(String text, String tooltip, CanvasPanel canvas, Consumer<DrawableItem> operation) {
        super(text);
        this.canvas = canvas;
        this.operation = operation;
        putValue(SHORT_DESCRIPTION, tooltip);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DrawableItem selected = canvas.getSelectedItem();
        if (selected != null) {
            operation.accept(selected);
            canvas.repaint();
        }
    }
}