package simpleweb;

import java.awt.*;
import java.awt.event.*;

import javax.print.attribute.standard.PrinterIsAcceptingJobs;
import javax.swing.*;
import javax.swing.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Simpleweb_new extends JFrame implements ActionListener, HyperlinkListener {
    private JTextField addressBar;
    private JEditorPane pane;
    Queue<JLabel> messageQueue = new LinkedList<>();
    String host;
    static int port = 80;
    String requestedFile;

    Simpleweb_new() {
        super("Swing HTML Browser");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addressBar = new JTextField();
        addressBar.addActionListener(this);
        pane = new JEditorPane();
        pane.setEditable(false);
        pane.addHyperlinkListener(this);
        add(addressBar, BorderLayout.NORTH);
        add(new JScrollPane(pane));
        setSize(new Dimension(400, 400));
    }

    private void setAddressBarText(String url) {
        addressBar.setText(url);
    }

    public static String getHTMLText(String url) throws IOException {
        String host = url;
        String requestedFile = "progjar-browser/simple-test.php";

        InetAddress address = InetAddress.getByName(host);
        Socket socket = new Socket(address, port);
        StringBuilder body = new StringBuilder();

        try (
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String message = String.format(
                    "GET /%s HTTP/1.1\r\nHost: %s\r\n\r\n", requestedFile, host);
            out.write(message);
            out.flush();

            String line;
            boolean isMsgHead = true;
            boolean isMsgBody = false;
            Queue<JLabel> messageQueue = new LinkedList<>();
            while ((line = in.readLine()) != null) {
                if (isMsgBody) {
                    body.append(line);
                    body.append("\r\n");
                    JLabel curJLabel = new JLabel();
                    curJLabel.setText(line);
                    messageQueue.add(curJLabel);
                }
                if (!isMsgHead) {
                    isMsgBody = true;
                }
                if (line.contains("Content-Type")) {
                    isMsgHead = false;
                }
            }
            System.out.println(messageQueue);
        }
        socket.close();
        return body.toString();
    }

    private void extractHTMLMessage(String url) throws IOException {
        host = url;
        requestedFile = "progjar-browser/simple-test.php";

        InetAddress address = InetAddress.getByName(host);
        Socket socket = new Socket(address, port);
        StringBuilder body = new StringBuilder();

        try (
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String message = String.format(
                    "GET /%s HTTP/1.1\r\nHost: %s\r\n\r\n", requestedFile, host);
            out.write(message);
            out.flush();

            String line;
            boolean isMsgHead = true;
            boolean isMsgBody = false;
            Queue<JLabel> messageQueue = new LinkedList<>();
            while ((line = in.readLine()) != null) {
                if (isMsgBody) {
                    body.append(line);
                    body.append("\r\n");
                    JLabel curJLabel = new JLabel();
                    curJLabel.setText(line);
                    messageQueue.add(curJLabel);
                }
                if (!isMsgHead) {
                    isMsgBody = true;
                }
                if (line.contains("Content-Type")) {
                    isMsgHead = false;
                }
            }
            System.out.println(messageQueue);
        }
        socket.close();
        //return body.toString();
    }

    public void actionPerformed(ActionEvent evt) {
        String url = addressBar.getText();
        try {
            //host = ;
            requestedFile = useRegex(url, "(?<=/)");
            pane.setText(getHTMLText(url));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private String useRegex(final String input, final String regex) {
        // Compile regular expression
        final Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        // Match regex against input
        final Matcher matcher = pattern.matcher(input);
        // Use results...
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }

    private String extractHref(final String input) {
        // Compile regular expression
        final Pattern pattern = Pattern.compile("href=\"[^\"]*\">", Pattern.CASE_INSENSITIVE);
        // Match regex against input
        final Matcher matcher = pattern.matcher(input);
        // Use results...
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }

    // Adapt <a> element to look like a link
    private JLabel changeToLink(JLabel link) {
        link.setForeground(Color.BLUE.darker());
        link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        String url = host + extractHref(link.getText());
        link.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setAddressBarText(url);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        return link;
    }

    // Render final HTML
    public void renderHTMLElement() {

        Iterator<JLabel> it = messageQueue.iterator();
        while (it.hasNext()) {
            JLabel currLabel = it.next();
            if (currLabel.getText().contains("<a ") || currLabel.getText().contains("<a>")) {
                currLabel = changeToLink(currLabel);
            }
            pane.add(currLabel);
        }
    }

    public void hyperlinkUpdate(HyperlinkEvent evt) {
        if (evt.getEventType() != HyperlinkEvent.EventType.ACTIVATED) {
            return;
        }
        JEditorPane srcPane = (JEditorPane) evt.getSource();
        String url = evt.getURL().toString();
        addressBar.setText(url);
        try {
            pane.setText(getHTMLText(url));
            ;
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void main(String args[]) {
        Simpleweb_new browser = new Simpleweb_new();
        browser.setVisible(true);
    }
}