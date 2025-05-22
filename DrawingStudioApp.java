import javax.swing.*;

import java.awt.*;

public class DrawingStudioApp extends JFrame {
    private DrawingCanvas rightCanvas;
    private LeftCanvas leftCanvas;

    public DrawingStudioApp() {
        setTitle("Drawing Studio Pro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLayout(new BorderLayout());

        // Setup canvases
        leftCanvas = new LeftCanvas();
        rightCanvas = new DrawingCanvas();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftCanvas, rightCanvas);
        splitPane.setDividerLocation(500);
        add(splitPane, BorderLayout.CENTER);

        // Setup toolbar
        JToolBar toolBar = new JToolBar();

        JButton btnClearRight = new JButton("New Drawing");
        btnClearRight.addActionListener(e -> rightCanvas.clearCanvas());

        JButton btnColor = new JButton("Pen Color");
        btnColor.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(this, "Choose Pen Color", Color.BLACK);
            if (chosen != null) rightCanvas.setPenColor(chosen);
        });

        JButton btnStroke = new JButton("Stroke Size");
        btnStroke.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Enter Stroke Size:", "2");
            if (input != null) {
                try {
                    int size = Integer.parseInt(input);
                    rightCanvas.setStrokeSize(size);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid number");
                }
            }
        });

        JButton btnInsertAnimal = new JButton("Insert Animal");
        btnInsertAnimal.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                String path = fileChooser.getSelectedFile().getAbsolutePath();
                AnimalItem animal = new AnimalItem(path, 50, 50);
                leftCanvas.addItem(animal);
            }
        });

        JButton btnInsertFlower = new JButton("Insert Flower");
         btnInsertFlower.addActionListener(e -> {
         JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        String path = fileChooser.getSelectedFile().getAbsolutePath();
        FlowerItem flower = new FlowerItem(path, 60, 60);
        leftCanvas.addItem(flower);
    }
});

JButton btnInsertCustom = new JButton("Insert Custom");
btnInsertCustom.addActionListener(e -> {
    JFileChooser fileChooser = new JFileChooser();
    if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        String path = fileChooser.getSelectedFile().getAbsolutePath();
        Image img = new ImageIcon(path).getImage();
        CustomImageItem custom = new CustomImageItem(img, 70, 70);
        leftCanvas.addItem(custom);
    }
});


        toolBar.add(btnClearRight);
        toolBar.add(btnColor);
        toolBar.add(btnStroke);
        toolBar.add(btnInsertAnimal);
        toolBar.add(btnInsertFlower);
         toolBar.add(btnInsertCustom);
        add(toolBar, BorderLayout.NORTH);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DrawingStudioApp::new);
    }
}
