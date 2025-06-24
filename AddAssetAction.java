import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AddAssetAction extends AbstractAction {
    private JFrame owner;
    private CanvasPanel compositionCanvas;
    private String assetType; // "Animal", "Flower"
    private String[] assetNames; // {"Lion", "Tiger"}, {"Rose", "Tulip"}
    private String assetBasePath; // "/assets/animals/", "/assets/flowers/"
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