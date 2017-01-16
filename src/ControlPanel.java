import java.awt.*;

import java.util.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;

import java.io.*;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.*;
import javax.swing.border.LineBorder;

public class ControlPanel extends JPanel {
	static JTextField textDisplay;
	static JComboBox<Font> fonts;
	static GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    static Font[] allFonts = ge.getAllFonts();
	static JButton setColor;
	static JButton moveFront;
	static JButton moveBack;
	static JButton remove;
	static JButton open;
	static JButton save;
	static JButton startServer;
	static JButton startClient;
	static JLabel networkMode;
	public Canvas view;
	private WhiteboardNetworking net;
	
	double theX;
	double theY;
	double theHeight;
	double theWidth;
	
	public ControlPanel()
	{
		super();
		this.net = net;
		this.addButtons();
		//view = new Canvas();
	}
	
	public void addButtons()
	{
		JPanel shapePanel = new JPanel();
		JPanel colorPanel = new JPanel();
		JPanel fontBox = new JPanel();
		JPanel movePanel = new JPanel();
		JPanel networkPanel = new JPanel();
		JTextArea textBox = new JTextArea();
		JPanel tablePanel = new JPanel();
		
		textBox.setBorder(new LineBorder(Color.gray, 2));
		textBox.setEditable(false);
		
		JButton rect = new JButton("Rect");
		rect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!net.isClient()){
					int random = (int )(Math.random() * 400 + 1);
					int random2 = (int )(Math.random() * 400 + 1);
					int random3 = (int )(Math.random() * 150 + 10);
					int random4 = (int )(Math.random() * 150 + 10);
					DRectModel bounds = new DRectModel();
					bounds.setX(random);
					bounds.setY(random2);
					bounds.setWidth(random3);
					bounds.setHeight(random4);
					bounds.setID(Canvas.shapesList.size());
					Canvas.addShape(bounds);
		           if(net.isServer())
		           	net.doSend("add", bounds);
		           Iterator<DShape> it = Canvas.shapesList.iterator();
		           while (it.hasNext()) {
		           	DShapeModel temp = it.next().info;
		           	theX = temp.getX();
		           	theY = temp.getY();
		           	theHeight = temp.getHeight();
		           	theWidth = temp.getWidth();
		           	
		           }
				}
			}
		});
		
		JButton oval = new JButton("Oval");
		oval.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!net.isClient()){
					int random = (int )(Math.random() * 400 + 1);
					int random2 = (int )(Math.random() * 400 + 1);
					int random3 = (int )(Math.random() * 150 + 10);
					int random4 = (int )(Math.random() * 150 + 10);				
					DOvalModel bounds = new DOvalModel(random, random2, random3, random4);			
					bounds.setID(Canvas.shapesList.size());
					Canvas.addShape(bounds);
		           if(net.isServer())
		           	net.doSend("add", bounds);
				}

			}
		});
		
		JButton line = new JButton("Line");
		line.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!net.isClient()){
					int random = (int )(Math.random() * 350 + 2);
					int random2 = (int )(Math.random() * 350 + 2);
					int random3 = (int )(Math.random() * 150 + 10);
					int random4 = (int )(Math.random() * 150 + 10);		
					DLineModel bounds = new DLineModel(random, random2, random3, random4);	
					//DLineModel bounds = new DLineModel(0,0,20,20);
					bounds.setID(Canvas.shapesList.size());
					Canvas.addShape(bounds);
		           if(net.isServer())
		           	net.doSend("add", bounds);
				}

			}
		});
		
		JButton text = new JButton("Text");
		text.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!net.isClient()){
					int random = (int )(Math.random() * 400 + 2);
					int random2 = (int )(Math.random() * 400 + 2);
				/*	int random3 = (int )(Math.random() * 100 + 5);
					int random4 = (int )(Math.random() * 100 + 5);		
					DTextModel bounds = new DTextModel(random, random2, random3, random4);	*/
					DTextModel bounds = new DTextModel(random,random2,200,100,"Hello","Dialog.plain"); 
					bounds.setID(Canvas.shapesList.size());
					Canvas.addShape(bounds);
		           if(net.isServer())
		           	net.doSend("add", bounds);
				}

			}
		});
		
		setColor = new JButton("Set Color");
		setColor.addActionListener(new ActionListener() { 
		public void actionPerformed(ActionEvent e) {
			if(!net.isClient()){
				Color initialcolor = Color.GRAY;
				Color newColor = JColorChooser.showDialog(setColor, "Select a color", initialcolor);
				if (Canvas.selectedModel == null)
				{
					return;
				}
				for (DShapeModel d : DShapeModel.listeners)
				{
					if (Canvas.selectedModel.equals(d))
					Canvas.selectedModel.setC(newColor);
					Canvas.selected.modelChanged(Canvas.selectedModel);
					repaint();
		           if(net.isServer())
		           	net.doSend("change", Canvas.selectedModel);
				} 
			}
		 }
		});
		
		moveFront = new JButton("Move To Front");
		moveFront.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				if(!net.isClient()){
					Canvas.printReverse();
					System.out.println(" ");
					if (Canvas.selected != null && !Canvas.shapesList.isEmpty())
					{
						Canvas.shapesList.remove(Canvas.shapesList.indexOf(Canvas.selected));
						Canvas.shapesList.add(0, Canvas.selected);
					}
					Canvas.printList();
					net.doSend("front", Canvas.selected.info);
				}
			}
		});
		
		moveBack = new JButton("Move To Back");
		moveBack.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				if(!net.isClient()){
					Canvas.printReverse();
					System.out.println(" ");
					if (Canvas.selected != null && !Canvas.shapesList.isEmpty())
					{
						Canvas.shapesList.remove(Canvas.shapesList.indexOf(Canvas.selected));
						Canvas.shapesList.add(Canvas.selected);
					}
					Canvas.printReverse();
					net.doSend("back", Canvas.selected.info);
				}
			}
		});
		
		remove = new JButton("Remove Shape");
		remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!net.isClient()){
					if (Canvas.selected != null) {
						if (Canvas.shapesList.contains(Canvas.selected)) {
							DShapeModel.listeners.remove(Canvas.selectedModel);
							Canvas.shapesList.remove(Canvas.selected);
							repaint();
				           if(net.isServer())
				           	net.doSend("remove", Canvas.selectedModel);
						}
					}
				}
			}
		});
		
		open = new JButton("Open");
		open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!net.isClient()) {
					String result = JOptionPane.showInputDialog("File Name", null);
	               if (result != null) {
	                   File f = new File(result);
	           		try {
	                       XMLDecoder xmlIn = new XMLDecoder(new BufferedInputStream(new FileInputStream(f))); 
	                       DShape[] shapeArray = (DShape[]) xmlIn.readObject();
	                       xmlIn.close();
	                       Canvas.shapesList.clear();
	                       DShapeModel.listeners.clear();
	                       for(DShape shape : shapeArray) {
	                       	Canvas.shapesList.add(shape);
	                       	DShapeModel.listeners.add(shape.info);
	                       }
	               		repaint();
	               		if(net.isServer()) {
	               			net.openNewCanvas();
	               		}
	               		
	                   } catch (IOException x) {
	                       x.printStackTrace();
	                   }
	               }
				}
			}
		});
		
		save = new JButton("Save");
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String result = JOptionPane.showInputDialog("File Name", null);
                if (result != null) {
                    File f = new File(result);
                    try {
                        XMLEncoder xmlOut = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(f)));
                        DShape[] shapeArray = Canvas.shapesList.toArray(new DShape[Canvas.shapesList.size()]);
                        xmlOut.writeObject(shapeArray);
                        xmlOut.close();
                    } catch (IOException x) {
                    	x.printStackTrace();
                    }
                }
			}
		});
		
		networkMode = new JLabel("    Mode:  Normal");
		
		startServer = new JButton("Start Server");
		startServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
	           if(!net.isServer() && !net.isClient())
	           	net.doServer();
			}
		});
		
		startClient = new JButton("Start Client");
		startClient.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
	           if(!net.isServer() && !net.isClient()){
					int dialogButton = JOptionPane.YES_NO_OPTION;
					int dialogResult = JOptionPane.showConfirmDialog(null, "This will clear your whiteboard. Are you sure?", "Open Client", dialogButton);
					if(dialogResult == 0) {
						Canvas.shapesList.clear();
		               DShapeModel.listeners.clear();
		       		repaint();
						net.doClient();
					} else {
					 // do nothing
					} 
	           }

			}
		});
		
		String[] columnNames = {"X", "Y", "Width", "Height"};
		
		Object [][] data = {
				{theX, theY, theWidth, theHeight},
				{"1", "2", "1", "2"},
				{"1", "2", "1", "2"},
				{"1", "2", "1", "2"},
		};
		
		JTable table = new JTable(data, columnNames);
		table.setPreferredScrollableViewportSize(new Dimension(500, 50));
		table.setFillsViewportHeight(true);
		
		JScrollPane scrollPane = new JScrollPane(table);
		
		
		shapePanel.setLayout(new BoxLayout(shapePanel, BoxLayout.X_AXIS)); 
		shapePanel.add(new JLabel("    Add  "));
		shapePanel.add(rect);
		shapePanel.add(oval);
		shapePanel.add(line);
		shapePanel.add(text);
		shapePanel.add(Box.createRigidArea(new Dimension(0,50))); // creates white space between the panels.
		
		colorPanel.setLayout(new BoxLayout(colorPanel, BoxLayout.X_AXIS));
		colorPanel.add(setColor);
		colorPanel.add(open);
		colorPanel.add(save);
		colorPanel.add(Box.createRigidArea(new Dimension(0,50)));
		
		textDisplay = new JTextField(10);
		fonts = new JComboBox<Font>(allFonts);
		//this gets the actual font name into the box, without like java.awt.font blah blah
		fonts.setRenderer(new DefaultListCellRenderer() {
		  @Override
		  public Component getListCellRendererComponent(JList<?> list,
		        Object value, int index, boolean isSelected, boolean cellHasFocus) {
		     if (value != null) {
		        Font font = (Font) value;
		        value = font.getName();
		     }
		     return super.getListCellRendererComponent(list, value, index,
		           isSelected, cellHasFocus);
		  }
		});
		textDisplay.setMaximumSize(new Dimension(100, textDisplay.getPreferredSize().height));
		disableButtons();
		fontBox.setLayout(new BoxLayout(fontBox, BoxLayout.X_AXIS));
		fontBox.add(textDisplay);
		fontBox.add(Box.createRigidArea(new Dimension(20,0)));
		fontBox.add(fonts);
		colorPanel.add(Box.createRigidArea(new Dimension(0,50)));
		
		movePanel.setLayout(new BoxLayout(movePanel, BoxLayout.X_AXIS));
		movePanel.add(moveFront);
		movePanel.add(moveBack);
		movePanel.add(remove);
		movePanel.add(Box.createRigidArea(new Dimension(0,50)));
		
		networkPanel.add(startServer);
		networkPanel.add(startClient);
		networkPanel.add(networkMode);
		
		tablePanel.add(scrollPane);
		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(shapePanel);
		this.add(fontBox);
		this.add(colorPanel);
		this.add(movePanel);
		this.add(networkPanel);
		this.add(textBox);
		this.add(tablePanel);
		for(Component comp: this.getComponents()){
			((JComponent)comp).setAlignmentX(LEFT_ALIGNMENT); // aligns all the panels to the left margin.
		}
	}
	
	public void setNet(WhiteboardNetworking net) {
		this.net = net;
	}
	
	public void setNetworkMode(String str) {
		networkMode.setText(str);
	}

	public static void enableButtons()
	{
		textDisplay.setEnabled(true);
		fonts.setEnabled(true);
	}
	
	public static void disableButtons()
	{
		textDisplay.setEnabled(false);
		fonts.setEnabled(false);
	}
	
	
	
}