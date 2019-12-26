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

    public static Map<String, String> getQueryMap(String query)
    {
        String[] newQuery = query.split("\\?");
        String[] params = newQuery[1].split("&");
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params)
        {
            String [] p=param.split("=");
            String name = p[0];
            if(p.length>1)
            {
                String value = p[1];
                map.put(name, value);
            }
        }
        return map;
    }

    public void handle(HttpExchange t) throws IOException
    {
        URI uri = t.getRequestURI();
        Map params=getQueryMap(uri.toString());

        String dropoff=(String)params.get("dropoff");
        String pickup=(String)params.get("pickup");
        String maxPassengers=(String)params.get("maxPassengers");

        System.out.println("Dropoff: " + dropoff + " Pickup: " + pickup + " MaxPass: " + maxPassengers);

        
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
//localhost:8080/taxis?name=Marcin&dropoff=10&pickup=12121,1&maxPassengers=16
////https://techtest.rideways.com/dave?dropoff=51.470020,-0.454295&pickup=52.167241,-0.443187
//http://localhost:8080/taxis/?name=Marcin&dropoff=10