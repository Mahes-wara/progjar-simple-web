package simpleweb;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

public class getHTMLText_new {
    public static void main(String[] args) throws IOException{
        String host = "127.0.0.1";
        int port = 80;
        String requestedFile = "progjar-browser/simple-tes.php";

        InetAddress address = InetAddress.getByName(host);
        Socket socket = new Socket(address, port);
        StringBuilder body = new StringBuilder();
        
        try(
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ){
            String message = String.format(
                "GET /%s HTTP/1.1\r\nHost: %s\r\n\r\n", requestedFile, host);
            out.write(message);
            out.flush(); 

            String line;
            boolean isMsgHead = true;
            boolean isMsgBody = false;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
                if (isMsgBody) {
                    body.append(line);
                    body.append("\r\n");
                }
                if (!isMsgHead) {
                    isMsgBody = true;
                }
                if(line.contains("Content-Type")){
                    isMsgHead = false;
                }
            }
        }
        socket.close();
        System.out.println(body.toString());
    }
}
