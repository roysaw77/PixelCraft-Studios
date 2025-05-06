import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class DrawingProgram extends JFrame {
	public DrawingProgram(){
		super("Painter");
		JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
		toolbar.add(new Label("Drag mouse to draw"));
		this.add(toolbar,BorderLayout.SOUTH);
		setSize(800,600);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public static void main(String[] a){
		new DrawingProgram();
	}
}