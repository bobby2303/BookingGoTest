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
            String[] p = param.split("=");
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
        JSONObject results = new JSONObject();
        URI uri = t.getRequestURI();
        Map params=getQueryMap(uri.toString());

        try
        {
            String dropoff=(String)params.get("dropoff");
            Float dropoffLat = Float.parseFloat(dropoff.split("\\,")[0]);
            Float dropoffLong = Float.parseFloat(dropoff.split("\\,")[1]);
            String pickup=(String)params.get("pickup");
            Float pickupLat = Float.parseFloat(pickup.split("\\,")[0]);
            Float pickupLong = Float.parseFloat(pickup.split("\\,")[1]);
            String sMaxPassengers=(String)params.get("maxPassengers");
            int maxPassengers = Integer.parseInt(sMaxPassengers);

            System.out.println("Dropoff: " + dropoffLat + "," + dropoffLong + " Pickup: " + pickupLat+ "," +  pickupLong + " MaxPass: " + maxPassengers);

            List<Option> options = FindOptions.run(pickupLat, pickupLong, dropoffLat, dropoffLong, maxPassengers);

            results = new JSONObject();
            results.put("dropoff", dropoff);
            results.put("pickup", pickup);
            results.put("maxPassengers", maxPassengers);
            JSONArray jsonArray = new JSONArray();


            for(Option o: options)
            {
                System.out.printf("%-21s - %-4s - %-8d \n", o.getCarType(), o.getSupplier(), o.getPrice());
                JSONObject jsonOptions = new JSONObject();
                jsonOptions.put("car_type", o.getCarType());
                jsonOptions.put("supplier_id", o.getSupplier());
                jsonOptions.put("price",  o.getPrice());
                jsonArray.put(jsonOptions);

            }

            results.put("options", jsonArray);

        }
        catch(Exception e)
        {
            System.out.println("ERROR " + e);
        }



        //Header Handling
        String response = results.toString();
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
//localhost:8080/taxis?name=Marcin&dropoff=51.470020,-0.454295&pickup=52.167241,-0.443187&maxPassengers=16
////https://techtest.rideways.com/dave?dropoff=51.470020,-0.454295&pickup=52.167241,-0.443187
//http://localhost:8080/taxis/?name=Marcin&dropoff=10