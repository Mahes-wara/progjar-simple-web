package simpleWeb;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.LinkedList;
import java.util.Queue;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class SimpleWebUI extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -563149275837909422L;
	private JTextField addressBar;
	private JEditorPane pane;
	Queue<JLabel> messageQueue = new LinkedList<>();
	String host;
	static int port = 80;
	String requestedFile;
	private HTMLMessage msg;

	SimpleWebUI() {
        super("Swing HTML Browser");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addressBar = new JTextField();
        addressBar.addActionListener(this);
        pane = new JEditorPane();
        pane.setEditable(false);
        add(addressBar, BorderLayout.NORTH);
        add(new JScrollPane(pane));
        setSize(new Dimension(400, 400));
    }

	private void setAddressBarText(String url) {
		addressBar.setText(url);
	}

	// Adapt <a> element to look like a link
	private JLabel changeToLink(LinkObject link) {
		link.setForeground(Color.BLUE.darker());
		link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		String url = this.msg.getHost() + "/" + this.msg.getCurrentFolder() + "/" + link.getHref();
		link.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setAddressBarText(url);
				ActionEvent evt = new ActionEvent(link, 1, "enter");
				actionPerformed(evt);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				link.setForeground(Color.PINK);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				link.setForeground(Color.BLUE.darker());
			}
		});
		return link;
	}

	
	
	public void actionPerformed(ActionEvent evt) {
		String url = addressBar.getText();
		try {
			// host = ;
			//url 127.0.0.1/progjar-browser/simple-test.php
			pane.removeAll();
			String[] tokens = url.split("/", 2);
			requestedFile = tokens[1];
			String host = tokens[0];
			this.msg = new HTMLMessage(host, requestedFile, 80);
			//pane.setText(msg.getContentString());
			int size = msg.getContent().size();
			pane.setSize(100, size*50);
			//pane.set
			//Queue<JLabel> content = msg.getContent();
			int y = 10;
			for(JLabel item : msg.getContent()) {
				item.setBounds(50,y,500,30);
				if(item instanceof LinkObject) {
					this.changeToLink((LinkObject)item);
				}
				pane.add(item);
				y += 20;
			}
			//pane.setText(getHTMLText(url));
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public static void main(String args[]) {
		SimpleWebUI browser = new SimpleWebUI();
		browser.setVisible(true);
	}

}
