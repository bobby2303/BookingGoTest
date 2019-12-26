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

public class FindOptions
{
    private static final OkHttpClient client = new OkHttpClient().newBuilder().readTimeout(2000, TimeUnit.MILLISECONDS).build();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

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

    private JSONObject getRequest(String url, String supplier)
    {
        JSONObject json = null;
        //GET Request
        Request request = new Request.Builder().url(url).get().build();
        try (Response response = client.newCall(request).execute())
        {
            json = parseRequest(response.body().string());
            if(response.code() != 200)
            {
                System.out.println(supplier + " ERROR " + json.get("status") + ": " + json.get("error") + " - " + json.get("message"));
            }
            System.out.println(json.toString());
            return json;
        }
        catch(Exception e)
        {
            System.out.println("RESPONSE ERROR: No response from " + supplier);
        }
        return json;
    }

    private List<Option> getOptions(List<JSONObject> jsons, int maxPassengers)
    {
        List<Option> options = new ArrayList<Option>();
        try
        {
            for (JSONObject json : jsons)
            {
                //Create Options[] array
                String supplier = (String) json.get("supplier_id");
                JSONArray jsonArray = (JSONArray) json.get("options");

                for (int i = 0; i < jsonArray.length(); i++)
                {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String carType = (String) obj.get("car_type");
                    int price = (int) obj.get("price");

                    if (maxPassengers <= 4 && maxPassengers > 0)
                    {
                        if (!(carType.equals("PEOPLE_CARRIER") || carType.equals("LUXURY_PEOPLE_CARRIER") || carType.equals("MINIBUS")))
                        {
                            Option o = comparePrice(options, supplier, carType, price);
                            if (!(options.contains(o))) options.add(o);
                        }
                    }
                    else if (maxPassengers <= 6 && maxPassengers > 4)
                    {
                        if (!(carType.equals("MINIBUS")))
                        {
                            Option o = comparePrice(options, supplier, carType, price);
                            if (!(options.contains(o))) options.add(o);
                        }
                    }
                    else if (maxPassengers <= 16 && maxPassengers > 6)
                    {
                        Option o = comparePrice(options, supplier, carType, price);
                        if (!(options.contains(o))) options.add(o);
                    }
                }
            }
        }
        catch(Exception e)
        {
            System.out.println("ERROR");
        }
        return options;
    }

    private Option comparePrice(List<Option> options, String newSupplier, String newCarType, int newPrice)
    {
        for(Option o : options)
        {
            String oldCarType = o.getCarType();
            if(oldCarType.equals(newCarType))
            {
                if(newPrice < o.getPrice())
                {
                    o.setPrice(newPrice);
                    o.setSupplier(newSupplier);
                }
                return o;
            }
        }
        Option unchangedOption = new Option(newSupplier, newCarType, newPrice);
        return unchangedOption;
    }

    public List<Option> run(float pickupLatitude, float pickupLongitude, float dropoffLatitude, float dropoffLongitude, int maximumPassengers) throws IOException
    {
        try
        {
            float pickupLat = pickupLatitude;
            float pickupLong = pickupLongitude;
            float dropoffLat = dropoffLatitude;
            float dropoffLong = dropoffLongitude;
            int maxPassengers = maximumPassengers;

            if(maxPassengers > 16 || maxPassengers < 1)
            {
                System.out.println("Number of max passengers is not applicable. Must be between 1-16");
                System.exit(1);
            }

            String DaveURL = "https://techtest.rideways.com/dave?pickup=" + pickupLat + "," + pickupLong + "&dropoff=" + dropoffLat + "," + dropoffLong;
            String EricURL = "https://techtest.rideways.com/eric?pickup=" + pickupLat + "," + pickupLong + "&dropoff=" + dropoffLat + "," + dropoffLong;
            String JeffURL = "https://techtest.rideways.com/jeff?pickup=" + pickupLat + "," + pickupLong + "&dropoff=" + dropoffLat + "," + dropoffLong;
            //call
            List<JSONObject> jsons = new ArrayList<JSONObject>();
            JSONObject daveJSON = getRequest(DaveURL, "Dave");
            if(daveJSON != null) jsons.add(daveJSON);
            JSONObject ericJSON = getRequest(EricURL, "Eric");
            if(ericJSON != null) jsons.add(ericJSON);
            JSONObject jeffJSON = getRequest(JeffURL, "Jeff");
            if(jeffJSON != null) jsons.add(jeffJSON);

            List<Option> options = getOptions(jsons, maxPassengers);

            Collections.sort(options);

            //for(Option o: options)
            //{
            //    System.out.printf("%-21s - %-4s - %-8d \n", o.getCarType(), o.getSupplier(), o.getPrice());
            //}

            return options;

        }
        catch(NumberFormatException nfe)
        {
            System.out.println("Arguments must all be numbers");
        }
        return null;
    }



}

//supplier_id in JSONObject
//{"dropoff":"58.16724,-0.53187","options":[{"price":393381,"car_type":"STANDARD"},{"price":684384,"car_type":"EXECUTIVE"},{"price":142471,"car_type":"LUXURY"},{"price":562193,"car_type":"PEOPLE_CARRIER"},{"price":60544,"car_type":"MINIBUS"}],"pickup":"51.47002,-0.454295","supplier_id":"DAVE"}
//https://techtest.rideways.com/dave?dropoff=51.470020,-0.454295&pickup=52.167241,-0.443187
//java -cp .:json-20190722.jar:okhttp-4.2.2.jar:okio-2.0.0.jar:kotlin-stdlib-common-1.3.50.jar:kotlin-stdlib-1.3.50.jar Client 51.470020 -0.454295 58.167241 -0.53187
//java -cp .:json-20190722.jar:okhttp-4.2.2.jar:okio-2.0.0.jar:kotlin-stdlib-common-1.3.50.jar:kotlin-stdlib-1.3.50.jar Client 51.470020 -0.454295 52.167241 -0.443187
