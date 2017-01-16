
public class DTextModel extends DShapeModel {
	String text;
    String fontType;
    
	public DTextModel(int x , int y, int width, int height, String text,String fontType)
	{
		super(x,y,width,height);
		this.text = text;
		this.fontType = fontType;
	}
	
	public DTextModel()
	{
		super();
	    text = "Hello";
		fontType = "Dialog.plain";
	}
	
	public String getText()
	{
		return text;
	}
	
	public void setText(String newText)
	{
		text = newText;
	}
	
	public String getFontType()
	{
		return fontType;
	}
	
	public void setFontType(String font)
	{
		fontType = font;
	}
	
}
