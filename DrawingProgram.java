import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class DrawingProgram extends JFrame {
    private Color currentColor = Color.BLACK; // Default drawing color

    public DrawingProgram() {
        super("Painter");
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.add(new JLabel("Drag mouse to draw"));

        // Add a button to open the color chooser
        JButton colorButton = new JButton("Choose Color");
        colorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color newColor = JColorChooser.showDialog(null, "Choose Drawing Color", currentColor);
                if (newColor != null) {
                    currentColor = newColor;
                }
            }
        });
        toolbar.add(colorButton);

        this.add(toolbar, BorderLayout.SOUTH);

        // Add drawing panel
        DrawingPanel drawingPanel = new DrawingPanel();
        this.add(drawingPanel, BorderLayout.CENTER);

        setSize(800, 600);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    // Custom panel for drawing
    class DrawingPanel extends JPanel {
        private int lastX, lastY;

        public DrawingPanel() {
            setBackground(Color.WHITE);
            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    lastX = e.getX();
                    lastY = e.getY();
                }
            });
            addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseDragged(MouseEvent e) {
                    Graphics g = getGraphics();
                    g.setColor(currentColor);
                    g.drawLine(lastX, lastY, e.getX(), e.getY());
                    lastX = e.getX();
                    lastY = e.getY();
                    g.dispose();
                }
            });
        }
    }

    public static void main(String[] a) {
        new DrawingProgram();
    }
}
