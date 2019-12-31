import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.RequestBody;
import java.io.*;
import java.util.Scanner;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONTokener;
import java.util.*;
import java.util.concurrent.TimeUnit ;

/**
 * Client - Find all cheapest taxi options from APIs
 */
public class Client
{
    private static final OkHttpClient client = new OkHttpClient().newBuilder()
            .readTimeout(2000, TimeUnit.MILLISECONDS).build();                                                          //Set timeout of 2 seconds for response from API
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    /**
     * Create JSONObject from receieved String
     * @param jsonString to parse as a JSONObject
     * @return JSONObject
     */
    private JSONObject parseRequest(String jsonString)
    {
        JSONTokener jt = new JSONTokener(jsonString);
        String output = "";
        char line;
        while ((line = jt.next()) != 0)
        {
            output += line;
        }
        JSONObject json = new JSONObject(output);
        return json;
    }

    /**
     * Perform a GET Request to the URL parameter
     * Return a JSONObject
     * @param url - URL to GET from
     * @param supplier - the API host
     * @return JSONObject from GET request
     */
    private JSONObject getRequest(String url, String supplier)
    {
        JSONObject json = null;
        //GET Request
        Request request = new Request.Builder().url(url).get().build();
        try (Response response = client.newCall(request).execute())
        {
            json = parseRequest(response.body().string());
            if(response.code() != 200)                                                                                  //If the response is not OK, print error
            {
                System.out.println("GET ERROR: " + json.get("status") + ": " + json.get("error") + " - "                //Suitable error message
                        + json.get("message") + " from " +supplier);
                json = null;                                                                                            //Return a null JSONObject
            }
            System.out.println(json.toString());                                                                        //Output the result of the GET request to terminal
            return json;
        }
        catch(Exception e)
        {
            System.out.println("RESPONSE ERROR: No response from " + supplier);
        }
        return json;
    }

    /**
     * Method to get all the cheapest taxi options from APIS
     * Filters results based on nuber of passengers inputted
     * Initiate Option class for each API Response
     * @param jsons - List of all JSONObject responses from GET request
     * @param maxPassengers - number of max passengers specified to filter results
     * @return
     */
    private List<Option> getOptions(List<JSONObject> jsons, int maxPassengers)
    {
        List<Option> options = new ArrayList<Option>();
        try
        {
            for (JSONObject json : jsons)
            {
                String supplier = (String) json.get("supplier_id");
                JSONArray jsonArray = (JSONArray) json.get("options");                                                  //Create JSONArray from the 'options' key in each JSONObject

                for (int i = 0; i < jsonArray.length(); i++)
                {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String carType = (String) obj.get("car_type");                                                      //Retrieve the Car Type and Price from each Option
                    int price = (int) obj.get("price");

                    if (maxPassengers <= 4 && maxPassengers > 0)                                                        //If between 0-4...
                    {
                    if (!(carType.equals("PEOPLE_CARRIER") || carType.equals("LUXURY_PEOPLE_CARRIER")                   //Exclude PEOPLE_CARRIER, LUXURY_PEOPLE_CARRIER, MINIBUS as these hold more passengers than required
                                || carType.equals("MINIBUS")))
                        {
                            Option o = comparePrice(options, supplier, carType, price);                                 //Create an Option object for each and call comparePrice
                            if (!(options.contains(o))) options.add(o);                                                 //If the Option is unique, add to the options List
                        }
                    }
                    else if (maxPassengers <= 6 && maxPassengers > 4)                                                   //If between 5-6
                    {
                        if (!(carType.equals("MINIBUS")))                                                               //Exclude MINIBUS
                        {
                            Option o = comparePrice(options, supplier, carType, price);                                 //Call comparePrice
                            if (!(options.contains(o))) options.add(o);
                        }
                    }
                    else if (maxPassengers <= 16 && maxPassengers > 6)                                                  //If between 7-16 do not exclude any car types
                    {
                        Option o = comparePrice(options, supplier, carType, price);                                     //Call comparePrice
                        if (!(options.contains(o))) options.add(o);
                    }
                }
            }
        }
        catch(Exception e)
        {
            System.out.println("ERROR in getOptions: " + e);
        }
        return options;                                                                                                 //Return the completed optionsList of all cheapest taxi options
    }

    /**
     * Compare and update the price of an option to display the cheapest option
     * @param options  - Current List of Options
     * @param newSupplier - Supplier to check
     * @param newCarType - Car Type to compare
     * @param newPrice - Price to compare
     * @return - return the updated Option
     */
    private Option comparePrice(List<Option> options, String newSupplier, String newCarType, int newPrice)
    {
        for(Option o : options)                                                                                         //Loop through all existing options
        {
            String oldCarType = o.getCarType();
            if(oldCarType.equals(newCarType))                                                                           //Find the matching car type to compare price
            {
                if(newPrice < o.getPrice())                                                                             //Compare the old and new prices
                {
                    o.setPrice(newPrice);                                                                               //Update the price and supplier of the Option object if the price is cheaper
                    o.setSupplier(newSupplier);
                }
                return o;                                                                                               //Return the option
            }
        }
        Option unchangedOption = new Option(newSupplier, newCarType, newPrice);                                         //Else created an Option object that is unchanged
        return unchangedOption;                                                                                         //Return the unchanged Option object - which is not unique and hence not duplicated into the list
    }

    /**
     * Main Method to begin execution. Get parameters and perform valudation.
     * Create URL to call APIs using parameters
     * @param args  - parameters of pickup, dropoff and maxPassengers to use with APIs
     * @throws IOException
     */
    public void run(String[] args) throws IOException
    {
        try
        {
            //Parse parameters as variables
            float pickupLat = Float.parseFloat(args[0]);
            float pickupLong = Float.parseFloat(args[1]);
            float dropoffLat = Float.parseFloat(args[2]);
            float dropoffLong = Float.parseFloat(args[3]);
            int maxPassengers = Integer.parseInt(args[4]);

            if(maxPassengers > 16 || maxPassengers < 1)                                                                 //Validation on number of passengers
            {
                System.out.println("Number of max passengers is not applicable. Must be between 1-16");
                System.exit(1);
            }

            String DaveURL = "https://techtest.rideways.com/dave?pickup=" + pickupLat + "," + pickupLong + "&dropoff=" + dropoffLat + "," + dropoffLong;
            String EricURL = "https://techtest.rideways.com/eric?pickup=" + pickupLat + "," + pickupLong + "&dropoff=" + dropoffLat + "," + dropoffLong;
            String JeffURL = "https://techtest.rideways.com/jeff?pickup=" + pickupLat + "," + pickupLong + "&dropoff=" + dropoffLat + "," + dropoffLong;

            List<JSONObject> jsons = new ArrayList<JSONObject>();                                                       //List of JSONObject to store response from GET request from APIs
            JSONObject daveJSON = getRequest(DaveURL, "Dave");
            if(daveJSON != null) jsons.add(daveJSON);                                                                   //Add the response from GET: DaveURL if not null
            JSONObject ericJSON = getRequest(EricURL, "Eric");
            if(ericJSON != null) jsons.add(ericJSON);                                                                   //Add the response from GET: EricURL if not null
            JSONObject jeffJSON = getRequest(JeffURL, "Jeff");
            if(jeffJSON != null) jsons.add(jeffJSON);                                                                   //Add the response from GET: JeffURL if not null

            List<Option> options = getOptions(jsons, maxPassengers);                                                    //Retrieve a completed List of all the cheapest options by calling getOptions

            Collections.sort(options);                                                                                  //Sort the options List by descending order by price

            for(Option o: options)
            {
                System.out.printf("%-21s - %-4s - %-8d \n", o.getCarType(), o.getSupplier(), o.getPrice());             //Pretty print the results to terminal
            }

        }
        catch(NumberFormatException nfe)
        {
            System.out.println("Arguments must all be numbers");
        }
    }


    public static void main (String[] args) throws IOException
    {
        if(args.length > 0)
        {
            Client c = new Client();
            c.run(args);
        }
        else
        {
            System.out.println("Add arguments");
        }

    }
}

//javac *.java -cp .:json-20190722.jar:okhttp-4.2.2.jar:okio-2.0.0.jar:kotlin-stdlib-common-1.3.50.jar:kotlin-stdlib-1.3.50.jar
//java -cp .:json-20190722.jar:okhttp-4.2.2.jar:okio-2.0.0.jar:kotlin-stdlib-common-1.3.50.jar:kotlin-stdlib-1.3.50.jar Client 51.470020 -0.454295 58.167241 -0.53187 16
//java -cp .:json-20190722.jar:okhttp-4.2.2.jar:okio-2.0.0.jar:kotlin-stdlib-common-1.3.50.jar:kotlin-stdlib-1.3.50.jar Client 51.470020 -0.454295 52.167241 -0.443187 16
