import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class DrawingStudioPro extends JFrame implements PropertyChangeListener {
    private CanvasPanel leftCanvas;
    private CanvasPanel rightCanvas;
    private AppToolbar toolbar;

    public DrawingStudioPro() {
        setTitle("Drawing Studio Pro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Define dimensions for the canvases
        int canvasWidth = 500;
        int canvasHeight = 500;

        // Initialize canvases
        leftCanvas = new CanvasPanel(canvasWidth, canvasHeight, false); // Composition canvas
        rightCanvas = new CanvasPanel(canvasWidth, canvasHeight, true); // Freehand drawing canvas
        leftCanvas.addPropertyChangeListener(this); // Listen for selected item changes
        // Setup toolbar using the dedicated AppToolbar class
        toolbar = new AppToolbar(this, leftCanvas, rightCanvas);

        // Layout using JSplitPane for resizable canvas areas
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftCanvas, rightCanvas);
        splitPane.setResizeWeight(0.5);
        splitPane.setOneTouchExpandable(true);

        add(toolbar, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == leftCanvas && "selectedItem".equals(evt.getPropertyName())) {
            DrawableItem newItem = (DrawableItem) evt.getNewValue();
            JSlider itemRotationSlider = toolbar.getItemRotationSlider();

            if (newItem instanceof CreationItem) {
                itemRotationSlider.setEnabled(true);
                itemRotationSlider.setValue((int) Math.round(((CreationItem) newItem).getRotationAngle()));
            } else {
                itemRotationSlider.setEnabled(false);
                itemRotationSlider.setValue(0); // Reset slider
            }
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DrawingStudioPro().setVisible(true));
    }
}
