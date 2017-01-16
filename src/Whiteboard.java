import java.awt.*;

import javax.swing.*;

public class Whiteboard extends JFrame {
	private JFrame frame; 
	String title;
	
	public Whiteboard(String title)
	{
		this.title = title;
	}
	
	public Whiteboard()
	{
		title = "Whiteboard";
	}
	
	
	public void run()
	{
	    frame = new JFrame(title);
	    frame.setLayout(new BorderLayout());
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
	    ControlPanel controls = new ControlPanel();
	    controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
	    
	    WhiteboardNetworking net = new WhiteboardNetworking();
	    net.setCp(controls);
	    controls.setNet(net);
	    
	    Canvas board = new Canvas(controls, net);
	    frame.add(board, BorderLayout.CENTER);
	    
	    frame.add(controls,BorderLayout.WEST);

	    frame.pack();
	    frame.setVisible(true);
	}
	public static void main(String args[])
	{
        // Prefer the "native" look and feel.
        try {
           UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { 
        } 
        
		Whiteboard wb = new Whiteboard();
		wb.run();
	}
}
