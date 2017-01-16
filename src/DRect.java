
import java.awt.*;

public class DRect extends DShape {

	public DRect()
	{
		super();
	}
	
	public void draw(Graphics g)
	{	
		g.setColor(info.getC());
        g.fillRect(info.getX(),info.getY(),info.getWidth(),info.getHeight());
	}
	
	
	
	public String getName()
	{
		return "DRect";
	}
	
}	
