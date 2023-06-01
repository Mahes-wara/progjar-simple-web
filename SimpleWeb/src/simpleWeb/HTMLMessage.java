package simpleWeb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JLabel;



public class HTMLMessage {
	private int statusCode;
	private String statusMessage;
	private Queue<JLabel> content;
	private String contentType;
	private String host;
	private String requestedFile;
	private String currentFolder;
	private int port;
	
	private String useRegex(final String input, final String regex) {
		// Compile regular expression
		final Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		// Match regex against input
		final Matcher matcher = pattern.matcher(input);
		// Use results...
		if (matcher.find()) {
			return matcher.group();
		}
		return null;
	}

	private String extractHref(final String input) {
		return this.useRegex(input, "(?<=href=\\\").*(?=\\\")");
	}
	private String extractLinkContent(final String input) {
		return this.useRegex(input, "(?<=>).*(?=</a>)");
	}
	
	public HTMLMessage(String host,
			String requestedFile, int port) {
		super();
		this.statusCode = 200;
		this.statusMessage = "OK";
		this.contentType = "text/html";
		this.host = host;
		this.requestedFile = requestedFile;
		this.port = port;
		
		String currentFolder = "";
		String[] tokens = requestedFile.split("/");
        int x = tokens.length;
        for (String a : tokens) {
        	currentFolder += a;
        	x--;
        	if(x == 1) {
        		break;
        	}
        	currentFolder += "/";
        }
        this.currentFolder = currentFolder;
		
		//requestedFile = "progjar-browser/simple-test.php";
		try {
			InetAddress address = InetAddress.getByName(this.host);
			Socket socket = new Socket(address, port);
			StringBuilder body = new StringBuilder();
			
			try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
					BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
				String message = String.format("GET /%s HTTP/1.1\r\nHost: %s\r\n\r\n", requestedFile, host);
				out.write(message);
				out.flush();
				
				String line;
				boolean isMsgHead = true;
				boolean isMsgBody = false;
				this.content = new LinkedList<>();
				while ((line = in.readLine()) != null) {
					if (isMsgBody) {
						body.append(line);
						body.append("\r\n");
						if(this.extractHref(line) != null) {
							LinkObject link = new LinkObject(this.extractLinkContent(line), this.extractHref(line));
							link.setText(link.getContent());
							this.content.add(link);
						} else {
							JLabel curJLabel = new JLabel();
							curJLabel.setText(line);
							this.content.add(curJLabel);							
						}
					}
					if (!isMsgHead) {
						isMsgBody = true;
					}
					if (line.contains("Content-Type")) {
						isMsgHead = false;
					}
				}
				//System.out.println(this.content);
			}
			socket.close();
			// return body.toString();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	public Queue<JLabel> getContent(){
		return this.content;
	}
	public String getContentString(){
		return this.content.toString();
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public String getContentType() {
		return contentType;
	}

	public String getHost() {
		return host;
	}

	public String getRequestedFile() {
		return requestedFile;
	}

	public int getPort() {
		return port;
	}

	public String getCurrentFolder() {
		return currentFolder;
	}
	
}
