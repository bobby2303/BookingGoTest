import java.io.*;
import java.net.*;
import java.util.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONTokener;
import static javax.imageio.ImageIO.read;

public class Server implements HttpHandler
{

    public void handle(HttpExchange t) throws IOException
    {
        //Header Handling
        String response = "Test";
        t.getResponseHeaders().add("Content-Type:", "application/json");
        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    public static void main (String[] args)
    {
        try
        {
            System.out.println("Hello Server");
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/taxis", new Server());
            server.setExecutor(null); // creates a default executor
            server.start();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
}