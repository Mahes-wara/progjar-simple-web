package simpleWeb;


import javax.swing.JLabel;

public class LinkObject extends JLabel {
	private String content;
	private String href;
	public LinkObject(String content, String href) {
		super();
		this.content = content;
		this.href = href;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	
}
