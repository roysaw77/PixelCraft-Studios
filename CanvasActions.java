import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.function.Consumer;

public class CanvasActions {
    private CanvasActions() {} // Prevent instantiation

    public static class AddAssetAction extends AbstractAction {
    private JFrame owner;
    private CanvasPanel compositionCanvas;
    private String assetType;
    private String[] assetNames;
    private String assetBasePath;
    private ItemFactory itemFactory;

    public AddAssetAction(String assetType, ImageIcon icon, JFrame owner, CanvasPanel compositionCanvas, String[] assetNames, String assetBasePath, ItemFactory itemFactory) {
        super(assetType, icon);
        this.owner = owner;
        this.compositionCanvas = compositionCanvas;
        this.assetType = assetType;
        this.assetNames = assetNames;
        this.assetBasePath = assetBasePath;
        this.itemFactory = itemFactory;
        putValue(SHORT_DESCRIPTION, "Insert " + assetType.toLowerCase() + " image from library collection");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String selectedAssetName = (String) JOptionPane.showInputDialog(
                owner,
                "Select a " + assetType.toLowerCase() + " from the library:",
                "Insert " + assetType,
                JOptionPane.PLAIN_MESSAGE,
                null,
                assetNames,
                assetNames[0]);

        if (selectedAssetName != null) {
            String assetPath = assetBasePath + selectedAssetName + ".png";
            if (getClass().getResource(assetPath) == null) {
                JOptionPane.showMessageDialog(owner, "Asset not found: " + assetPath + "\nPlease ensure the image exists in your resources.", "Asset Error", JOptionPane.ERROR_MESSAGE);
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
            int result = JOptionPane.showConfirmDialog(owner, sizePanel, "Specify " + selectedAssetName + " Size (px)", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    int w = Integer.parseInt(widthField.getText());
                    int h = Integer.parseInt(heightField.getText());
                    compositionCanvas.addItem(itemFactory.create(50, 50, assetPath, w, h));
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(owner, "Invalid dimensions. Using default size.", "Error", JOptionPane.ERROR_MESSAGE);
                    compositionCanvas.addItem(itemFactory.create(50, 50, assetPath, 100, 100));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(owner, "Error loading asset: " + assetPath + "\nCheck if the path is correct and the file exists.", "Asset Load Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
   public static class AddFromLibraryAction extends AbstractAction {
    private JFrame owner;
    private CanvasPanel compositionCanvas;
    private List<BufferedImage> library;

    public AddFromLibraryAction(String text, ImageIcon icon, JFrame owner, CanvasPanel compositionCanvas, List<BufferedImage> library) {
        super(text, icon);
        this.owner = owner;
        this.compositionCanvas = compositionCanvas;
        this.library = library;
        putValue(SHORT_DESCRIPTION, "Add a saved custom drawing from the library to the composition");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (library.isEmpty()) {
            JOptionPane.showMessageDialog(owner, "Library is empty. Save a drawing first.", "Library Empty", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog libraryDialog = new JDialog(owner, "Select from Library", true);
        libraryDialog.setLayout(new BorderLayout());
        DefaultListModel<ImageIcon> listModel = new DefaultListModel<>();
        for (BufferedImage img : library) {
            Image thumbnail = img.getScaledInstance(100, -1, Image.SCALE_SMOOTH);
            listModel.addElement(new ImageIcon(thumbnail));
        }
        JList<ImageIcon> imageList = new JList<>(listModel);
        imageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        imageList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        imageList.setVisibleRowCount(-1);
        JScrollPane scrollPane = new JScrollPane(imageList);
        libraryDialog.add(scrollPane, BorderLayout.CENTER);

        JButton selectButton = new JButton("Add Selected");
        selectButton.addActionListener(selectEvent -> {
            int selectedIndex = imageList.getSelectedIndex();
            if (selectedIndex != -1) {
                BufferedImage originalImage = library.get(selectedIndex);
                compositionCanvas.addItem(new CustomImageItem(100, 100, originalImage));
                libraryDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(libraryDialog, "Please select an image.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        libraryDialog.add(selectButton, BorderLayout.SOUTH);
        libraryDialog.pack();
        libraryDialog.setLocationRelativeTo(owner);
        libraryDialog.setVisible(true);
    }
}
   public static class ChangePenColorAction extends AbstractAction {
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
    public static class ChangePenSizeAction extends AbstractAction {
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
                if (size > 0 && size < 50) {
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
    public static class ClearCanvasAction extends AbstractAction {
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

    public static class ManipulateItemAction extends AbstractAction {
        private CanvasPanel canvas;
        private Consumer<DrawableItem> operation;
        public ManipulateItemAction(String text, ImageIcon icon, String tooltip, CanvasPanel canvas, Consumer<DrawableItem> operation) {
            super(text, icon); this.canvas = canvas; this.operation = operation;
            putValue(SHORT_DESCRIPTION, tooltip);
        }
        public void actionPerformed(ActionEvent e) {
            DrawableItem selected = canvas.getSelectedItem();
            if (selected != null) { operation.accept(selected); canvas.repaint(); }
        }
    }

    public static class MergeCanvasAction extends AbstractAction {
        private CanvasPanel canvas;
        private JSlider canvasRotationSlider;
        public MergeCanvasAction(String text, ImageIcon icon, CanvasPanel canvas, JSlider slider) {
            super(text, icon); this.canvas = canvas; this.canvasRotationSlider = slider;
            putValue(SHORT_DESCRIPTION, "Merge all items on the left canvas into a single new image item");
        }
        public void actionPerformed(ActionEvent e) {
            if (canvas.getItems().isEmpty()) { return; }
            BufferedImage mergedImage = canvas.getComposedImage();
            if (mergedImage != null) {
                canvas.clear(); canvas.setCanvasRotationAngle(0);
                canvasRotationSlider.setValue(0);
                canvas.addItem(new CustomImageItem(0, 0, mergedImage));
            }
        }
    }

    public static class SaveCompositionAction extends AbstractAction {
         private JFrame owner;
         private CanvasPanel compositionCanvas;
         public SaveCompositionAction(String text, ImageIcon icon, JFrame owner, CanvasPanel canvas) {
             super(text, icon); this.owner = owner; this.compositionCanvas = canvas;
             putValue(SHORT_DESCRIPTION, "Save the current composition to a file");
         }
         public void actionPerformed(ActionEvent e) {
             JFileChooser fc = new JFileChooser(); fc.setDialogTitle("Save Composition As");
             fc.setSelectedFile(new File("my_composition.png")); //
             fc.addChoosableFileFilter(new FileNameExtensionFilter("PNG Image", "png"));
             if (fc.showSaveDialog(owner) == JFileChooser.APPROVE_OPTION) {
                 try {
                     ImageIO.write(compositionCanvas.getComposedImage(), "png", fc.getSelectedFile());
                 } catch (Exception ex) { /* ...error handling... */ }
             }
         }
    }

    public static class SaveDrawingAction extends AbstractAction {
        private JFrame owner;
        private CanvasPanel drawingCanvas;
        private List<BufferedImage> library;
        public SaveDrawingAction(String text, ImageIcon icon, JFrame owner, CanvasPanel canvas, List<BufferedImage> lib) {
            super(text, icon); this.owner = owner; this.drawingCanvas = canvas; this.library = lib;
            putValue(SHORT_DESCRIPTION, "Save custom drawing to a file and add to library");
        }
        public void actionPerformed(ActionEvent e) {
            JFileChooser fc = new JFileChooser(); fc.setDialogTitle("Save Drawing As");
            fc.setSelectedFile(new File("my_drawing.png"));
            fc.addChoosableFileFilter(new FileNameExtensionFilter("PNG Image", "png"));
             if (fc.showSaveDialog(owner) == JFileChooser.APPROVE_OPTION) {
                 try {
                     BufferedImage drawing = drawingCanvas.getFreehandImage();
                     ImageIO.write(drawing, "png", fc.getSelectedFile());
                     library.add(drawing); //
                     JOptionPane.showMessageDialog(owner, "Drawing saved and added to library.", "Save Successful", JOptionPane.INFORMATION_MESSAGE); //
                 } catch (Exception ex) { /* ...error handling... */ }
             }
        }
    }
}