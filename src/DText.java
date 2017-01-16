
import java.awt.*;

public class DText extends DShape {
    double fontSize = 1.0;
    Font selectedFont;
    FontMetrics metrics;
    int fontHeight;
    int fontDescent;
    String textLine;
    
	public DText()
	{
		super();
	}
	
	public Font computeFont(Graphics g)
	{
		while (info.getHeight() > fontHeight)
		{	
			fontSize = (fontSize * 1.10) + 1;
			selectedFont = new Font(((DTextModel) info).getFontType(), Font.PLAIN, (int) fontSize);
			metrics = g.getFontMetrics(selectedFont);
			fontHeight = metrics.getHeight();
			fontDescent = metrics.getDescent();
			fontSize = (fontSize * 1.10) + 1;
		}
		return selectedFont;
	}
	
	//RenderingHints makes the text smoother and less edgy I think...
	public void draw(Graphics g)
	{	
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

	    rh.put(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);

	    ((Graphics2D) g).setRenderingHints(rh);

	    selectedFont = computeFont(g);
	    
	    selectedFont = new Font(((DTextModel) info).getFontType(), Font.PLAIN, (int) fontSize);
		
	    g.setFont(selectedFont);
	           
	    g.setColor(info.getC());
	    
	    //place holder box drawn to see if the text fits inside the box
	    g.drawRect(info.getX(), info.getY(),info.getWidth(),info.getHeight());
	    
	    Shape clip = g.getClip();
	    
	    //Clips the width of the text within the box
	    g.setClip(clip.getBounds().createIntersection(getBounds()));
	    
	    g.drawString(((DTextModel) info).getText(), info.getX() , info.getY() + info.getHeight() - fontDescent );
	    
	    //Restore old clip
	    g.setClip(clip);

	}
	
	public String getName()
	{
		return "DText";
	}
	
}
