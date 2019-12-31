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

/**
 * Create a HTTP Sever to act as a RESTful API
 * Return JSON Payload of all cheapest options
 */
public class Server implements HttpHandler
{
    /**
     * Method to get a Map of Key,Value parameters from the URL
     * @param query - the URL with parameters
     * @return - the map of key,value pairs
     */
    public static Map<String, String> getQueryMap(String query)
    {
        String[] newQuery = query.split("\\?");
        String[] params = newQuery[1].split("&");                                                                       //Split by each parameters and extract information
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

    /**
     * Main method to run for HTTP server, waiting for a request then run execution
     * @param t - the HTTPExchange details
     * @throws IOException
     */
    public void handle(HttpExchange t) throws IOException
    {
        JSONObject payload = new JSONObject();                                                                          //Empty JSONObject - payload to return to client
        URI uri = t.getRequestURI();
        Map params=getQueryMap(uri.toString());                                                                         //Map of parameters from URL

        try
        {
            String dropoff=(String)params.get("dropoff");                                                               //Retrieve dropoff parameter
            Float dropoffLat = Float.parseFloat(dropoff.split("\\,")[0]);                                               //Extract dropoff latitude
            Float dropoffLong = Float.parseFloat(dropoff.split("\\,")[1]);                                              //Extract dropoff longitude
            String pickup=(String)params.get("pickup");                                                                 //Retrieve pickup parameters
            Float pickupLat = Float.parseFloat(pickup.split("\\,")[0]);                                                 //Extract pickup latitude
            Float pickupLong = Float.parseFloat(pickup.split("\\,")[1]);                                                //Extract pickup longitude
            String sMaxPassengers=(String)params.get("maxPassengers");                                                  //Retrieve maxPassengers parameter
            int maxPassengers = Integer.parseInt(sMaxPassengers);                                                       //Parse as an integer

            //System.out.println("Dropoff: " + dropoffLat + "," + dropoffLong + " Pickup: " + pickupLat+ "," +  pickupLong + " MaxPass: " + maxPassengers);

            List<Option> options = FindOptions.run(pickupLat, pickupLong, dropoffLat, dropoffLong, maxPassengers);      //Run FindOptions.run() to get a List of all the cheapest options (Part 1)

            //Add the user-entered details to the JSON payload
            payload.put("dropoff", dropoff);                                                                            //Add dropoff details
            payload.put("pickup", pickup);                                                                              //Add pickup details
            payload.put("maxPassengers", maxPassengers);                                                                //Add maxPassengers details
            JSONArray jsonArray = new JSONArray();                                                                      //Create a JSONArray to hold all cheapest Options

            for(Option o: options)                                                                                      //Loop through all options
            {
                System.out.printf("%-21s - %-4s - %-8d \n", o.getCarType(), o.getSupplier(), o.getPrice());             //Print results to termnial
                JSONObject jsonOptions = new JSONObject();                                                              //Create JSONObject for each option
                jsonOptions.put("car_type", o.getCarType());                                                            //Put Option details into the new JSONObject
                jsonOptions.put("supplier_id", o.getSupplier());
                jsonOptions.put("price",  o.getPrice());
                jsonArray.put(jsonOptions);                                                                             //Add the new JSONObject to the jsonArray

            }

            payload.put("options", jsonArray);                                                                          //Add JSONArray to the results JSONObject - payload

        }
        catch(Exception e)
        {
            System.out.println("ERROR " + e);
        }

        String response = payload.toString();

        //Add header and body for client response
        t.getResponseHeaders().add("Content-Type:", "application/json");
        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());                                                                                  //Return to client
        os.close();
    }

    public static void main (String[] args)
    {
        try
        {
            System.out.println("Hello Server");
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);                                      //Create and initiate server on localhost, port 8080
            server.createContext("/taxis", new Server());                                                               //Context of /taxis
            server.setExecutor(null);                                                                                   // creates a default executor
            server.start();                                                                                             //Start the server
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
}

//localhost:8080/taxis?dropoff=51.470020,-0.454295&pickup=52.167241,-0.443187&maxPassengers=16