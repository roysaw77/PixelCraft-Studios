import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AppToolbar extends JToolBar {
    private JSlider itemRotationSlider;

    private static final int ICON_WIDTH = 24;
    private static final int ICON_HEIGHT = 24;

    public AppToolbar(JFrame owner, CanvasPanel leftCanvas, CanvasPanel rightCanvas) {
        super("Drawing Tools");
        List<BufferedImage> library = new ArrayList<>();

        // Actions
        add(new CanvasActions.ClearCanvasAction("Clear Comp", loadIcon("new_canvas.png"), leftCanvas));
        add(new CanvasActions.ClearCanvasAction("Clear Draw", loadIcon("new_drawing.png"), rightCanvas));
        addSeparator();

        // Asset Creation using a single Factory
        ItemFactory itemFactory = (x, y, path, w, h) -> new ImageCreationItem(x, y, path, w, h);
        String[] animalAssets = {"Lion", "Tiger", "Elephant", "Bear"};
        add(new CanvasActions.AddAssetAction("Animal", loadIcon("add_animal.png"), owner, leftCanvas, animalAssets, "/assets/animals/", itemFactory));
        String[] flowerAssets = {"Rose", "Tulip", "Sunflower", "Daisy"};
        add(new CanvasActions.AddAssetAction("Flower", loadIcon("add_flower.png"), owner, leftCanvas, flowerAssets, "/assets/flowers/", itemFactory));
        addSeparator();

        // Save and Library
        add(new CanvasActions.SaveDrawingAction("Save Draw", loadIcon("save.png"), owner, rightCanvas, library));
        add(new CanvasActions.SaveCompositionAction("Save Comp", loadIcon("save_composition.png"), owner, leftCanvas));
        add(new CanvasActions.AddFromLibraryAction("From Lib", loadIcon("add_custom_library.png"), owner, leftCanvas, library));
        addSeparator();

        // Pen Tools
        add(new CanvasActions.ChangePenColorAction("Pen Color", loadIcon("color_picker.png"), owner, rightCanvas));
        add(new CanvasActions.ChangePenSizeAction("Pen Size", loadIcon("stroke_size.png"), owner, rightCanvas));
        addSeparator();

        // Sliders and Manipulation
        add(new JLabel("Canvas Rot:"));
        JSlider canvasRotationSlider = new JSlider(0, 359, 0);
        canvasRotationSlider.addChangeListener(e -> leftCanvas.setCanvasRotationAngle(canvasRotationSlider.getValue()));
        add(canvasRotationSlider);

        add(new JLabel("Item Rot:"));
        itemRotationSlider = new JSlider(0, 359, 0);
        itemRotationSlider.setEnabled(false);
        itemRotationSlider.addChangeListener(e -> {
            DrawableItem selected = leftCanvas.getSelectedItem();
            if (selected instanceof CreationItem) {
                ((CreationItem) selected).setRotationAngle(itemRotationSlider.getValue());
                leftCanvas.repaint();
            }
        });
        add(itemRotationSlider);
        addSeparator();

        add(new CanvasActions.ManipulateItemAction("Rot", loadIcon("rotate.png"), "Rotate Item", leftCanvas, item -> item.rotate(15)));
        add(new CanvasActions.ManipulateItemAction("Flip", loadIcon("flip.png"), "Flip Item", leftCanvas, DrawableItem::flip));
        addSeparator();

        // Merge Action
        add(new CanvasActions.MergeCanvasAction("Merge", loadIcon("merge.png"), leftCanvas, canvasRotationSlider));
    }

    public JSlider getItemRotationSlider() {
        return itemRotationSlider;
    }

    private ImageIcon loadIcon(String name) {
        String path = "/icons/" + name;
        URL iconUrl = getClass().getResource(path);
        if (iconUrl != null) {
            ImageIcon originalIcon = new ImageIcon(iconUrl);
            Image scaledImage = originalIcon.getImage().getScaledInstance(ICON_WIDTH, ICON_HEIGHT, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        }
        System.err.println("Icon not found: " + path);
        return null;
    }
}