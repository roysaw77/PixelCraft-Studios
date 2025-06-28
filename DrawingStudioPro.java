import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class DrawingStudioPro extends JFrame implements PropertyChangeListener {
    private CanvasPanel leftCanvas;
    private AppToolbar toolbar;

    public DrawingStudioPro() {
        setTitle("Drawing Studio Pro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize canvases
        leftCanvas = new CanvasPanel(500, 500, false); // Composition canvas
        CanvasPanel rightCanvas = new CanvasPanel(500, 500, true); // Freehand drawing canvas
        leftCanvas.addPropertyChangeListener(this); // Listen for selected item changes

        // Setup toolbar, which wires up all actions
        toolbar = new AppToolbar(this, leftCanvas, rightCanvas);

        // Layout using JSplitPane for resizable canvas areas
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftCanvas, rightCanvas);
        splitPane.setResizeWeight(0.5);

        add(toolbar, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // This logic correctly updates the UI based on events from the CanvasPanel.
        if (evt.getSource() == leftCanvas && "selectedItem".equals(evt.getPropertyName())) {
            DrawableItem newItem = (DrawableItem) evt.getNewValue();
            JSlider itemRotationSlider = toolbar.getItemRotationSlider();

            if (newItem instanceof CreationItem) {
                itemRotationSlider.setEnabled(true);
                itemRotationSlider.setValue((int) Math.round(((CreationItem) newItem).getRotationAngle()));
            } else {
                itemRotationSlider.setEnabled(false);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DrawingStudioPro().setVisible(true));
    }
}