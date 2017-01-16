import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class WhiteboardNetworking {
	/*********NETWORK VARIABLES*********/
	   // The are thread inner classes to handle
	   // the networking.
	   private ServerAccepter serverAccepter;
	   private ClientHandler clientHandler;
	   // List of object streams to which we send data
	   private java.util.List<ObjectOutputStream> outputs =
	      new ArrayList<ObjectOutputStream>();
	   private boolean isServer;
	   private boolean isClient;
	   /************************************/
	   private ControlPanel cp;
/*********************************************** NETWORKING ************************************************/
	public WhiteboardNetworking(){
		isServer = false;
		isClient = false;
	}
	
    // Adds an object stream to the list of outputs
    // (this and sendToOutputs() are synchronzied to avoid conflicts)
    public synchronized void addOutput(ObjectOutputStream out) {
        outputs.add(out);
    }
	
    // Server thread accepts incoming client connections
    class ServerAccepter extends Thread {
        private int port;
        ServerAccepter(int port) {
            this.port = port;
        }
        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                isServer = true;
                while (true) {
                    Socket toClient = null;
                    // this blocks, waiting for a Socket to the client
                    toClient = serverSocket.accept();
                    System.out.println("server: got client");
                    // Get an output stream to the client, and add it to
                    // the list of outputs
                    // (our server only uses the output stream of the connection)
                    ObjectOutputStream newClientOutput = new ObjectOutputStream(toClient.getOutputStream());
                    addOutput(newClientOutput);
                    intializeNewClient(newClientOutput);
                }
            } catch (IOException ex) {
                ex.printStackTrace(); 
            }
        }
    }
	
    // Syncs client whiteboard with server whiteboard
    // clearing current client whiteboard
    public void intializeNewClient(ObjectOutputStream newClientOutput) {
        Iterator<DShape> it = Canvas.shapesList.iterator();
        while (it.hasNext()) {
            DShapeModel current = it.next().info;
        	OutputStream memStream = new ByteArrayOutputStream();
            XMLEncoder encoder = new XMLEncoder(memStream);
            encoder.writeObject(current);
            encoder.close();
            String message = memStream.toString();
            
        	OutputStream memStream2 = new ByteArrayOutputStream();
            XMLEncoder encoder2 = new XMLEncoder(memStream2);
            encoder2.writeObject("add");
            encoder2.close();
            String command = memStream2.toString();
            
            try {
            	newClientOutput.writeObject(command);
            	newClientOutput.writeObject(message);
            	newClientOutput.flush();
            }
            catch (Exception ex) {
                ex.printStackTrace();
                it.remove();
                // Cute use of iterator and exceptions --
                // drop that socket from list if have probs with it
            }
        }
    }
    
    public void doServer() {
		cp.setNetworkMode("    Mode:  Server");
		String result = JOptionPane.showInputDialog("Run server on port", "24823");
        if (result!=null) {
        	System.out.println("server: start");
            serverAccepter = new ServerAccepter(Integer.parseInt(result.trim()));
            serverAccepter.start();
        }
    }
    
    public boolean isServer() {
		return isServer;
	}

	public boolean isClient() {
		return isClient;
	}

	// Initiate message send -- send both local and remote (must be on swing thread)
    // Wired to text field.
    public void doSend(String command, DShapeModel message) {
        //sendLocal(message);
        sendRemote(command);
        sendRemote(message);

    }
    
    // Appends a message to the local GUI (must be on swing thread)
    public void sendLocal(DShapeModel shape) {
    	Canvas.addShape(shape);
    	//DShapeModel.listeners.add(shape.info);
    }
    
    // Sends a message to all of the outgoing streams.
    // Writing rarely blocks, so doing this on the swing thread is ok,
    // although could fork off a worker to do it.
    public synchronized void sendRemote(Object message) {
        //status.setText("Server send");
        System.out.println("server: send ");
        
        // Convert the message object into an xml string.
        OutputStream memStream = new ByteArrayOutputStream();
        XMLEncoder encoder = new XMLEncoder(memStream);
        encoder.writeObject(message);
        encoder.close();
        String xmlString = memStream.toString();
        
        
        // Now write that xml string to all the clients.
        Iterator<ObjectOutputStream> it = outputs.iterator();
        while (it.hasNext()) {
            ObjectOutputStream out = it.next();
            try {
                out.writeObject(xmlString);
                out.flush();
            }
            catch (Exception ex) {
                ex.printStackTrace();
                it.remove();
                // Cute use of iterator and exceptions --
                // drop that socket from list if have probs with it
            }
        }
    }
    
    // Given a message, puts that message in the local GUI.
    // Can be called by any thread.
    public void invokeToGUI(String command, DShapeModel serverShape) {
    	if("add".equals(command)){
        	final DShapeModel temp = serverShape;
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    //status.setText("Client receive");
                    sendLocal(temp);
                }
            });
    	}
    	else if("remove".equals(command)){
    		final DShape DShape = findShape(serverShape.getID());
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    //status.setText("Client receive");
        			if (Canvas.shapesList.contains(DShape)) {
        				DShapeModel.listeners.remove(DShape);
        				Canvas.shapesList.remove(DShape);
        				cp.repaint();			
        			}
                }
            });

    	}
    	else if("front".equals(command)){
    		final DShape DShape = findShape(serverShape.getID());
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
					if (DShape != null && !Canvas.shapesList.isEmpty())
					{
						Canvas.shapesList.remove(DShape);
						Canvas.shapesList.add(0, DShape);
					}
                }
            });

    	}
    	else if("back".equals(command)){
    		final DShape DShape = findShape(serverShape.getID());
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
            		if (DShape != null && !Canvas.shapesList.isEmpty())
            		{
            			Canvas.shapesList.remove(DShape);
            			Canvas.shapesList.add(DShape);
            		}
                }
            });

    	}
    	else if("change".equals(command)){
    		final DShape clientShape = findShape(serverShape.getID());
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
        			if (Canvas.shapesList.contains(clientShape)) {
        				clientShape.info.mimic(serverShape);
        				cp.repaint();			
        			}
                }
            });

    	}
    	else if("clear".equals(command)){
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    Canvas.shapesList.clear();
                    DShapeModel.listeners.clear();
                }
            });
    	}
    	

    }
    
    // Client runs this to handle incoming messages
    // (our client only uses the inputstream of the connection)
    private class ClientHandler extends Thread {
        private String name;
        private int port;
        ClientHandler(String name, int port) {
            this.name = name;
            this.port = port;
        }
    // Connect to the server, loop getting messages
        public void run() {
            try {
                ObjectInputStream in = null;

                // make connection to the server name/port
                try {
	            	Socket toServer = new Socket(name, port);
	                // get input stream to read from server and wrap in object input stream
	                in = new ObjectInputStream(toServer.getInputStream());
	                System.out.println("client: connected!");
	                cp.setNetworkMode("    Mode:  Client");
	            	isClient = true;
                } catch (UnknownHostException e) {
                    System.out.println("Unknown host: " + name);
                } catch (ConnectException e) {
                    System.out.println("Connection refused: " + port);
                    JOptionPane.showMessageDialog(null, "Connection refused " + port + " Continuing in normal mode.");                
                    }
                
                if(isClient) {
	                // we could do this if we wanted to write to server in addition
	                // to reading
	                // out = new ObjectOutputStream(toServer.getOutputStream());
	                while (true) {
	                    // Get the xml string, decode to a Message object.
	                    // Blocks in readObject(), waiting for server to send something.
	                	String xmlString = (String) in.readObject();
	                    XMLDecoder decoder = new XMLDecoder(new ByteArrayInputStream(xmlString.getBytes()));
	                    String command = (String) decoder.readObject();
	                	
	                    String xmlString2 = (String) in.readObject();
	                    XMLDecoder decoder2 = new XMLDecoder(new ByteArrayInputStream(xmlString2.getBytes()));
	                    DShapeModel shape = (DShapeModel) decoder2.readObject();
	                    
	                    invokeToGUI(command,shape);
	                    System.out.println("client: read " + command);
	
	                }
                }
            }
            catch (Exception ex) { // IOException and ClassNotFoundException
               ex.printStackTrace();
            }
            // Could null out client ptr.
            // Note that exception breaks out of the while loop,
            // thus ending the thread.
       }
   } 
         
    // Runs a client handler to connect to a server.
    // Wired to Client button.
    public void doClient() {
		String result = JOptionPane.showInputDialog("Connect to host:port", "127.0.0.1:24823");
        if (result!=null) {
            String[] parts = result.split(":");
            System.out.println("client: start");
            clientHandler = new ClientHandler(parts[0].trim(), Integer.parseInt(parts[1].trim()));
            clientHandler.start();
        }
    }
    
    public void setCp(ControlPanel cp) {
		this.cp = cp;
	}

	// Finds Dshape in Canvas.shapesList given id
	public DShape findShape(int id) {
        boolean found = false;
        DShape target = null;
		Iterator<DShape> it = Canvas.shapesList.iterator();
        while (it.hasNext()) {
        	DShape current = it.next();
        	//System.out.println(id);
        	//System.out.println(current.getID());
        	if(current.getID() == id) {
        		found = true;
        		target = current;
        		break;
        	}
        		
        }
        if(found == false)
        	System.out.println("Shape not found.");
        return target;
	}

	public void openNewCanvas() {
		doSend("clear", new DShapeModel()); // clears all client canvases, DShapeModel is nothing.
		for(ObjectOutputStream oos: outputs) {
			intializeNewClient(oos);
		}
	}
	/*********************************************** NETWORKING ***********************************************/

}
