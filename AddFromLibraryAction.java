import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.List;

public class AddFromLibraryAction extends AbstractAction {
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