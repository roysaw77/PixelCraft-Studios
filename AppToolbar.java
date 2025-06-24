import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class AppToolbar extends JToolBar {
    private JFrame owner;
    private CanvasPanel leftCanvas;
    private CanvasPanel rightCanvas;
    private List<BufferedImage> library = new ArrayList<>();

    private JSlider canvasRotationSlider;
    private JSlider itemRotationSlider;
    public AppToolbar(JFrame owner, CanvasPanel leftCanvas, CanvasPanel rightCanvas) {
        super("Drawing Tools");
        this.owner = owner;
        this.leftCanvas = leftCanvas;
        this.rightCanvas = rightCanvas;
        createToolbarComponents();
}
    public JSlider getItemRotationSlider() {
        return itemRotationSlider;
    }

    private void createToolbarComponents() {
        int iconSize = 16;
        Dimension shortButtonSize = new Dimension(110, 26);
        Dimension smallButtonSize = new Dimension(90, 26);
        Dimension tinyButtonSize = new Dimension(80, 26);

        // Canvas Actions
        add(createButtonFromAction(new ClearCanvasAction("Clear Comp", createIcon("/icons/new_canvas.png", iconSize), leftCanvas), shortButtonSize));
        add(createButtonFromAction(new ClearCanvasAction("Clear Draw", createIcon("/icons/new_drawing.png", iconSize), rightCanvas), shortButtonSize));
        addSeparator();

        // Drawing Actions
        add(createButtonFromAction(new ChangePenColorAction("Pen Color", createIcon("/icons/color_picker.png", iconSize), owner, rightCanvas), shortButtonSize));
        add(createButtonFromAction(new ChangePenSizeAction("Pen Size", createIcon("/icons/stroke_size.png", iconSize), owner, rightCanvas), shortButtonSize));
        addSeparator();

        // Add Asset Actions
        String[] animalAssets = {"Lion", "Tiger", "Elephant", "Bear"};
        add(createButtonFromAction(new AddAssetAction("Animal", createIcon("/icons/add_animal.png", iconSize), owner, leftCanvas, animalAssets, "/assets/animals/", (x, y, path, w, h) -> new AnimalItem(x, y, path, w, h)), smallButtonSize));

        String[] flowerAssets = {"Rose", "Tulip", "Sunflower", "Daisy"};
        add(createButtonFromAction(new AddAssetAction("Flower", createIcon("/icons/add_flower.png", iconSize), owner, leftCanvas, flowerAssets, "/assets/flowers/", (x, y, path, w, h) -> new FlowerItem(x, y, path, w, h)), smallButtonSize));
        addSeparator();

        // Save and Library Actions
        add(createButtonFromAction(new SaveDrawingAction("Save Draw", createIcon("/icons/save.png", iconSize), owner, rightCanvas, library), shortButtonSize));
        add(createButtonFromAction(new SaveCompositionAction("Save Comp", createIcon("/icons/save_composition.png", iconSize), owner, leftCanvas), shortButtonSize));
        add(createButtonFromAction(new AddFromLibraryAction("Lib Custom", createIcon("/icons/add_custom_library.png", iconSize), owner, leftCanvas, library), shortButtonSize));
        addSeparator();

        // Canvas Rotation Slider
        add(new JLabel("Canvas Rot:"));
        canvasRotationSlider = new JSlider(0, 359, 0);
        canvasRotationSlider.setToolTipText("Rotate the entire left canvas");
        canvasRotationSlider.setPreferredSize(new Dimension(200, canvasRotationSlider.getPreferredSize().height));
        canvasRotationSlider.addChangeListener(e -> {
            if (leftCanvas != null) {
                leftCanvas.setCanvasRotationAngle(canvasRotationSlider.getValue());
            }
        });
        add(canvasRotationSlider);

        // Selected Item Rotation Slider
        add(new JLabel("Item Rot:"));
        itemRotationSlider = new JSlider(0, 359, 0);
        itemRotationSlider.setToolTipText("Rotate the selected item on the left canvas");
        itemRotationSlider.setPreferredSize(new Dimension(200, itemRotationSlider.getPreferredSize().height));
        itemRotationSlider.setEnabled(false);
        itemRotationSlider.addChangeListener(e -> {
            DrawableItem selected = leftCanvas.getSelectedItem();
            if (selected instanceof CreationItem && itemRotationSlider.isEnabled()) {
                ((CreationItem) selected).setRotationAngle(itemRotationSlider.getValue());
                leftCanvas.repaint();
            }
        });
        add(itemRotationSlider);

        // Item Manipulation Actions
        add(createButtonFromAction(new ManipulateItemAction("Rot Item", "Rotate selected item by 15 degrees", leftCanvas, item -> item.rotate(15)), tinyButtonSize));
        add(createButtonFromAction(new ManipulateItemAction("Flip Item", "Flip selected item horizontally", leftCanvas, DrawableItem::flip), tinyButtonSize));
        add(createButtonFromAction(new ManipulateItemAction("Scale Up", "Increase size of selected item", leftCanvas, item -> item.scale(1.1)), tinyButtonSize));
        add(createButtonFromAction(new ManipulateItemAction("Scale Down", "Decrease size of selected item", leftCanvas, item -> item.scale(0.9)), tinyButtonSize));
        addSeparator(new Dimension(10, 0));

        // Merge Action
        add(createButtonFromAction(new MergeCanvasAction("Merge", createIcon("/icons/merge.png", iconSize), owner, leftCanvas, canvasRotationSlider), tinyButtonSize));
    }

    private JButton createButtonFromAction(Action action, Dimension size) {
        JButton button = new JButton(action);
        button.setPreferredSize(size);
        button.setToolTipText((String) action.getValue(Action.SHORT_DESCRIPTION));
        return button;
    }

    private ImageIcon createIcon(String path, int size) {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(path));
            return new ImageIcon(icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH));
        } catch (Exception e) {
            System.err.println("Icon not found at: " + path);
            return null;
        }
    }
}


