import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

public class DrawingStudioPro extends JFrame implements PropertyChangeListener {
    private CanvasPanel leftCanvas;
    private CanvasPanel rightCanvas;
    private JToolBar toolbar;
    private List<BufferedImage> library = new ArrayList<>();

    private JSlider canvasRotationSlider;
    private JSlider itemRotationSlider;

    public DrawingStudioPro() {
        setTitle("Drawing Studio Pro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Define "big" dimensions for the canvases
        int canvasWidth = 500; // Increased width for each canvas's preferred size
        int canvasHeight = 500; // Increased height

        // Initialize canvases
        leftCanvas = new CanvasPanel(canvasWidth, canvasHeight, false); // Composition canvas
        rightCanvas = new CanvasPanel(canvasWidth, canvasHeight, true); // Freehand drawing canvas
        leftCanvas.addPropertyChangeListener(this); // Listen for selected item changes

        // Setup toolbar
        toolbar = new JToolBar();
        addToolbarButtons();

        // Layout using JSplitPane for resizable canvas areas
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftCanvas, rightCanvas);
        splitPane.setDividerLocation(0.5); // Set divider proportionally to the middle
        splitPane.setResizeWeight(0.5); // How space is distributed when window is resized

        add(toolbar, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    private void addToolbarButtons() {
        // Helper to create buttons with icons (icons are placeholders)
        // You'll need to create an 'icons' folder in your project root (or adjust path)
        // and add small PNG images (e.g., 16x16 or 24x24)
        // Example: icons/new_canvas.png, icons/color_picker.png etc.
        JButton clearLeftCanvasButton = new JButton("Clear Comp"); // Fulfills "Create left canvas" by clearing
        int iconSize = 16; // Or 16 for smaller icons. Adjust as needed.
        Dimension shortButtonSize = new Dimension(110, 26); // Standard size for "short" buttons with icons/text
        clearLeftCanvasButton.setToolTipText("Clear the left composition canvas");
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/icons/new_canvas.png"));
            clearLeftCanvasButton.setIcon(new ImageIcon(icon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH)));
        }
        catch (Exception e) { System.err.println("Icon not found for New Comp"); }
        clearLeftCanvasButton.addActionListener(e -> {
            leftCanvas.clear();
        });
        clearLeftCanvasButton.setPreferredSize(shortButtonSize);
        toolbar.add(clearLeftCanvasButton);

        JButton clearRightCanvasButton = new JButton("Clear Draw"); // Fulfills "Refresh/create new right canvas"
        clearRightCanvasButton.setToolTipText("Clear the right drawing canvas for a new free style drawing");
        clearRightCanvasButton.setPreferredSize(shortButtonSize);
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/icons/new_drawing.png"));
            clearRightCanvasButton.setIcon(new ImageIcon(icon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH)));
        }
        catch (Exception e) { System.err.println("Icon not found for New Draw"); }
        clearRightCanvasButton.addActionListener(e -> {
            rightCanvas.clear();
        });
        toolbar.add(clearRightCanvasButton);
        toolbar.addSeparator();
        JButton colorButton = new JButton("Pen Color");
        colorButton.setToolTipText("Change pen color for free style drawing");
        colorButton.setPreferredSize(shortButtonSize);
        try { ImageIcon icon = new ImageIcon(getClass().getResource("/icons/color_picker.png"));
            colorButton.setIcon(new ImageIcon(icon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH)));
        }
        catch (Exception e) { System.err.println("Icon not found for Pen Color"); }
        colorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Choose Pen Color", rightCanvas.getForeground()); // Or a default like Color.BLACK
            if (newColor != null) {
                rightCanvas.setPenColor(newColor);
            }
        });
        toolbar.add(colorButton);

        JButton strokeButton = new JButton("Pen Size");
        strokeButton.setToolTipText("Adjust pen stroke size for free style drawing");
        strokeButton.setPreferredSize(shortButtonSize);
        try { ImageIcon icon = new ImageIcon(getClass().getResource("/icons/stroke_size.png"));
             strokeButton.setIcon(new ImageIcon(icon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH)));}
        catch (Exception e) { System.err.println("Icon not found for Pen Size"); }
        strokeButton.addActionListener(e -> {
            String sizeStr = JOptionPane.showInputDialog(this, "Enter stroke size (e.g., 2):", "2");
            if (sizeStr != null) {
                try {
                    int size = Integer.parseInt(sizeStr);
                    if (size > 0 && size < 50) { // Basic validation
                        rightCanvas.setStrokeSize(size);
                    } else {
                        JOptionPane.showMessageDialog(this, "Stroke size must be between 1 and 49.", "Invalid Size", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid number entered for stroke size.", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        toolbar.add(strokeButton);

        toolbar.addSeparator();

        JButton addAnimal = new JButton("Animal");
        addAnimal.setToolTipText("Insert animal image from library collection");
        addAnimal.setPreferredSize(new Dimension(90, 26)); // Shorter for single word
        try { ImageIcon icon = new ImageIcon(getClass().getResource("/icons/add_animal.png"));
             addAnimal.setIcon(new ImageIcon(icon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH)));}
        catch (Exception e) { System.err.println("Icon not found for Animal"); }
        addAnimal.addActionListener(e -> {
            // Simulate a library of animals. In a real app, these might be filenames in an assets folder.
            String[] animalAssets = {"Lion", "Tiger", "Elephant", "Bear"}; // Placeholder names
            // For actual loading, you'd map these names to resource paths like "/assets/animals/lion.png"
            String selectedAnimalName = (String) JOptionPane.showInputDialog(
                    this,
                    "Select an animal from the library:",
                    "Insert Animal",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    animalAssets,
                    animalAssets[0]);

            if (selectedAnimalName != null) {
                // Construct the path to the asset. User needs to create these files.
                // Example: "/assets/animals/Lion.png" (ensure case sensitivity matches file names)
                String assetPath = "/assets/animals/" + selectedAnimalName + ".png";
                // Check if resource exists before trying to load
                if (getClass().getResource(assetPath) == null) {
                    JOptionPane.showMessageDialog(this, "Asset not found: " + assetPath + "\nPlease ensure the image exists in your resources.", "Asset Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                JTextField widthField = new JTextField("100", 5);
                JTextField heightField = new JTextField("100", 5);
                JPanel sizePanel = new JPanel();
                sizePanel.add(new JLabel("Width:"));
                sizePanel.add(widthField);
                sizePanel.add(Box.createHorizontalStrut(15));
                sizePanel.add(new JLabel("Height:"));
                sizePanel.add(heightField);
                int result = JOptionPane.showConfirmDialog(this, sizePanel, "Specify " + selectedAnimalName + " Size (px)", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    try {
                        int w = Integer.parseInt(widthField.getText());
                        int h = Integer.parseInt(heightField.getText());
                        // Pass the resource path string directly
                        leftCanvas.addItem(new AnimalItem(50, 50, assetPath, w, h));
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Invalid dimensions.", "Error", JOptionPane.ERROR_MESSAGE);
                        leftCanvas.addItem(new AnimalItem(50, 50, assetPath)); // Pass the resource path string directly
                    } catch (NullPointerException npe){
                        JOptionPane.showMessageDialog(this, "Error loading asset: " + assetPath + "\nCheck if the path is correct and the file exists.", "Asset Load Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        toolbar.add(addAnimal);

        JButton addFlower = new JButton("Flower");
        addFlower.setToolTipText("Insert flower image from library collection");
        addFlower.setPreferredSize(new Dimension(90, 26)); // Shorter for single word
        try { ImageIcon icon = new ImageIcon(getClass().getResource("/icons/add_flower.png"));
             addFlower.setIcon(new ImageIcon(icon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH)));}
        catch (Exception e) { System.err.println("Icon not found for Flower"); }
        addFlower.addActionListener(e -> {
            String[] flowerAssets = {"Rose", "Tulip", "Sunflower", "Daisy"}; // Placeholder names
            String selectedFlowerName = (String) JOptionPane.showInputDialog(
                    this,
                    "Select a flower from the library:",
                    "Insert Flower",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    flowerAssets,
                    flowerAssets[0]);

            if (selectedFlowerName != null) {
                String assetPath = "/assets/flowers/" + selectedFlowerName + ".png";
                 if (getClass().getResource(assetPath) == null) {
                    JOptionPane.showMessageDialog(this, "Asset not found: " + assetPath + "\nPlease ensure the image exists in your resources.", "Asset Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                JTextField widthField = new JTextField("80", 5);
                JTextField heightField = new JTextField("80", 5);
                JPanel sizePanel = new JPanel();
                sizePanel.add(new JLabel("Width:"));
                sizePanel.add(widthField);
                sizePanel.add(Box.createHorizontalStrut(15));
                sizePanel.add(new JLabel("Height:"));
                sizePanel.add(heightField);
                int result = JOptionPane.showConfirmDialog(this, sizePanel, "Specify " + selectedFlowerName + " Size (px)", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    try {
                        int w = Integer.parseInt(widthField.getText());
                        int h = Integer.parseInt(heightField.getText());
                        leftCanvas.addItem(new FlowerItem(70, 70, assetPath, w, h)); // Pass the resource path string directly
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Invalid dimensions.", "Error", JOptionPane.ERROR_MESSAGE);
                        leftCanvas.addItem(new FlowerItem(70, 70, assetPath)); // Pass the resource path string directly
                    } catch (NullPointerException npe){
                        JOptionPane.showMessageDialog(this, "Error loading asset: " + assetPath + "\nCheck if the path is correct and the file exists.", "Asset Load Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        toolbar.add(addFlower);

        toolbar.addSeparator();

        JButton saveDrawingButton = new JButton("Save Draw"); // Saves right canvas drawing
        saveDrawingButton.setToolTipText("Save custom drawing from right canvas to a file and add to library"); // Fulfills "Save the custom image into a collection library"
        saveDrawingButton.setPreferredSize(shortButtonSize);
        try { ImageIcon icon = new ImageIcon(getClass().getResource("/icons/save.png"));
             saveDrawingButton.setIcon(new ImageIcon(icon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH)));}
        catch (Exception e) { System.err.println("Icon not found for Save Draw"); }
        saveDrawingButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Drawing As");
            fileChooser.setSelectedFile(new File("my_drawing.png"));
            fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PNG Image", "png"));
            fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JPEG Image", "jpg", "jpeg"));

            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
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

                    BufferedImage drawing = rightCanvas.getFreehandImage();
                    if (drawing == null) {
                        JOptionPane.showMessageDialog(this, "Nothing to save.", "Save Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if ("jpg".equalsIgnoreCase(ext)) {
                        // For JPG, create an image with a white background
                        BufferedImage jpgImage = new BufferedImage(drawing.getWidth(), drawing.getHeight(), BufferedImage.TYPE_INT_RGB);
                        Graphics2D g = jpgImage.createGraphics();
                        g.setColor(Color.WHITE);
                        g.fillRect(0, 0, jpgImage.getWidth(), jpgImage.getHeight());
                        g.drawImage(drawing, 0, 0, null);
                        g.dispose();
                        ImageIO.write(jpgImage, "jpg", file);
                    } else { // PNG
                        ImageIO.write(drawing, "png", file);
                    }
                    library.add(drawing); // Add original drawing (with transparency if PNG) to library
                    JOptionPane.showMessageDialog(this, "Drawing saved to " + file.getName() + " and added to library.", "Save Successful", JOptionPane.INFORMATION_MESSAGE);

                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });
        toolbar.add(saveDrawingButton);

        JButton saveCompositionButton = new JButton("Save Comp");
        saveCompositionButton.setToolTipText("Save the current composition from left canvas to a file");
        saveCompositionButton.setPreferredSize(shortButtonSize);
        try { ImageIcon icon = new ImageIcon(getClass().getResource("/icons/save_composition.png"));
             saveCompositionButton.setIcon(new ImageIcon(icon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH)));}
        catch (Exception e) { System.err.println("Icon not found for Save Comp"); }
        saveCompositionButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Composition As");
            fileChooser.setSelectedFile(new File("my_composition.png"));
            fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PNG Image", "png"));
            fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JPEG Image", "jpg", "jpeg"));

            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
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

                    BufferedImage composedImage = leftCanvas.getComposedImage();
                     if (composedImage == null) {
                        JOptionPane.showMessageDialog(this, "Nothing to save in composition.", "Save Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if ("jpg".equalsIgnoreCase(ext)) {
                        BufferedImage jpgImage = new BufferedImage(composedImage.getWidth(), composedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
                        Graphics2D g = jpgImage.createGraphics();
                        g.setColor(Color.WHITE); // Assuming white background for JPG
                        g.fillRect(0, 0, jpgImage.getWidth(), jpgImage.getHeight());
                        g.drawImage(composedImage, 0, 0, null);
                        g.dispose();
                        ImageIO.write(jpgImage, "jpg", file);
                    } else { // PNG
                        ImageIO.write(composedImage, "png", file);
                    }
                    JOptionPane.showMessageDialog(this, "Composition saved to " + file.getName(), "Save Successful", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error saving composition: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });
        toolbar.add(saveCompositionButton);

        JButton addCustomFromLibraryButton = new JButton("Lib Custom");
        addCustomFromLibraryButton.setToolTipText("Add a saved custom drawing from the library to the composition canvas"); // Fulfills "Add the custom image to the composition canvas (from library collection)"
        addCustomFromLibraryButton.setPreferredSize(shortButtonSize);
        try { ImageIcon icon = new ImageIcon(getClass().getResource("/icons/add_custom_library.png"));
             addCustomFromLibraryButton.setIcon(new ImageIcon(icon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH)));}
        catch (Exception e) { System.err.println("Icon not found for Lib Custom"); }
        addCustomFromLibraryButton.addActionListener(e -> {
            if (library.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Library is empty. Save a drawing first.", "Library Empty", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Create a dialog to select an image from the library
            JDialog libraryDialog = new JDialog(this, "Select from Library", true);
            libraryDialog.setLayout(new BorderLayout());
            DefaultListModel<ImageIcon> listModel = new DefaultListModel<>();
            for (BufferedImage img : library) {
                // Scale images for thumbnail display in the list
                Image thumbnail = img.getScaledInstance(100, -1, Image.SCALE_SMOOTH); // width 100, auto height
                listModel.addElement(new ImageIcon(thumbnail));
            }
            JList<ImageIcon> imageList = new JList<>(listModel);
            imageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            imageList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
            imageList.setVisibleRowCount(-1); // Adjust based on number of items
            JScrollPane scrollPane = new JScrollPane(imageList);
            libraryDialog.add(scrollPane, BorderLayout.CENTER);

            JButton selectButton = new JButton("Add Selected");
            selectButton.addActionListener(selectEvent -> {
                int selectedIndex = imageList.getSelectedIndex();
                if (selectedIndex != -1) {
                    BufferedImage originalImage = library.get(selectedIndex);
                    leftCanvas.addItem(new CustomImageItem(100, 100, originalImage));
                    libraryDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(libraryDialog, "Please select an image.", "No Selection", JOptionPane.WARNING_MESSAGE);
                }
            });
            libraryDialog.add(selectButton, BorderLayout.SOUTH);
            libraryDialog.pack();
            libraryDialog.setLocationRelativeTo(this);
            libraryDialog.setVisible(true);
        });
        toolbar.add(addCustomFromLibraryButton);
        toolbar.addSeparator();

        // Canvas Rotation Slider
        toolbar.add(new JLabel("Canvas Rot:"));
        canvasRotationSlider = new JSlider(0, 359, 0);
        canvasRotationSlider.setToolTipText("Rotate the entire left canvas");
        canvasRotationSlider.setPreferredSize(new Dimension(200, canvasRotationSlider.getPreferredSize().height));
        canvasRotationSlider.addChangeListener(e -> {
            if (leftCanvas != null) {
                leftCanvas.setCanvasRotationAngle(canvasRotationSlider.getValue());
            }
        });
        toolbar.add(canvasRotationSlider);

        // Selected Item Rotation Slider
        toolbar.add(new JLabel("Item Rot:"));
        itemRotationSlider = new JSlider(0, 359, 0);
        itemRotationSlider.setToolTipText("Rotate the selected item on the left canvas");
        itemRotationSlider.setPreferredSize(new Dimension(200, itemRotationSlider.getPreferredSize().height));
        itemRotationSlider.setEnabled(false); // Disabled by default
        itemRotationSlider.addChangeListener(e -> {
            DrawableItem selected = leftCanvas.getSelectedItem();
            if (selected instanceof CreationItem && itemRotationSlider.isEnabled()) {
                ((CreationItem) selected).setRotationAngle(itemRotationSlider.getValue());
                leftCanvas.repaint();
            }
        });
        toolbar.add(itemRotationSlider);

        // Item manipulation buttons
        JButton rotateItemButton = new JButton("Rot Item");
        rotateItemButton.setPreferredSize(new Dimension(80, 26));
        rotateItemButton.setToolTipText("Rotate selected item on the left canvas");
        rotateItemButton.addActionListener(e -> {
            DrawableItem selected = leftCanvas.getSelectedItem();
            if (selected != null) {
                selected.rotate(15); // Rotate by 15 degrees
                leftCanvas.repaint();
            }
        });
        toolbar.add(rotateItemButton);

        JButton flipItemButton = new JButton("Flip Item");
        flipItemButton.setPreferredSize(new Dimension(80, 26));
        flipItemButton.setToolTipText("Flip selected item on the left canvas");
        flipItemButton.addActionListener(e -> {
            DrawableItem selected = leftCanvas.getSelectedItem();
            if (selected != null) {
                selected.flip();
                leftCanvas.repaint();
            }
        });
        toolbar.add(flipItemButton);


         toolbar.addSeparator(new Dimension(10, 0)); // Small space

        // Add Scale Up/Down buttons
        // Add Merge Canvas Button
        JButton mergeCanvasButton = new JButton("Merge");
        mergeCanvasButton.setToolTipText("Merge all items on the left canvas into a single new image item");
        mergeCanvasButton.setPreferredSize(new Dimension(80, 26));
        try { ImageIcon icon = new ImageIcon(getClass().getResource("/icons/merge.png")); // Assuming you have a merge.png icon
             mergeCanvasButton.setIcon(new ImageIcon(icon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH)));}
        catch (Exception e) { System.err.println("Icon not found for Merge"); }

        mergeCanvasButton.addActionListener(e -> {
            if (leftCanvas.getItems().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Left canvas is empty. Nothing to merge.", "Merge Canvas", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // 1. Get the composed image from the left canvas. This image includes the canvas's current rotation.
            BufferedImage mergedImage = leftCanvas.getComposedImage();

            if (mergedImage != null) {
                // 2. Create a new CustomImageItem from this BufferedImage. Position it at (0,0) of the canvas.
                CustomImageItem mergedItem = new CustomImageItem(0, 0, mergedImage);

                // 3. Clear all existing items from the left canvas.
                leftCanvas.clear(); // This clears items and freehand, but not canvas rotation angle

                // 4. Reset the left canvas's rotation angle to 0, as it's now baked into the mergedItem.
                leftCanvas.setCanvasRotationAngle(0);
                canvasRotationSlider.setValue(0); // Update the slider UI

                // 5. Add the new merged item to the now-empty left canvas.
                leftCanvas.addItem(mergedItem);
                // leftCanvas.repaint(); // addItem and setCanvasRotationAngle should trigger repaint
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create merged image.", "Merge Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        toolbar.add(mergeCanvasButton);

        // TODO: Add Scale Up/Down buttons, Merge Canvas Button
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == leftCanvas && "selectedItem".equals(evt.getPropertyName())) {
            DrawableItem newItem = (DrawableItem) evt.getNewValue();
            if (newItem instanceof CreationItem) {
                itemRotationSlider.setEnabled(true);
                // Temporarily remove listener to prevent feedback loop while setting value
                javax.swing.event.ChangeListener[] listeners = itemRotationSlider.getChangeListeners();
                for (javax.swing.event.ChangeListener listener : listeners) {
                    itemRotationSlider.removeChangeListener(listener);
                }
                itemRotationSlider.setValue((int) Math.round(((CreationItem) newItem).getRotationAngle()));
                // Re-add listeners
                for (javax.swing.event.ChangeListener listener : listeners) {
                    itemRotationSlider.addChangeListener(listener);
                }
            } else {
                itemRotationSlider.setEnabled(false);
                 // Temporarily remove listener
                javax.swing.event.ChangeListener[] listeners = itemRotationSlider.getChangeListeners();
                for (javax.swing.event.ChangeListener listener : listeners) {
                    itemRotationSlider.removeChangeListener(listener);
                }
                itemRotationSlider.setValue(0); // Reset slider
                for (javax.swing.event.ChangeListener listener : listeners) {
                    itemRotationSlider.addChangeListener(listener);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DrawingStudioPro().setVisible(true));
    }
}

// Helper method in CanvasPanel to get items list (if needed, though not directly for this change)
// Add this to CanvasPanel.java if you need external access to the items list,
// for example, to check if it's empty from DrawingStudioPro.
// public List<DrawableItem> getItems() {
// return items;
// }