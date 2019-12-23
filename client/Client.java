import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.RequestBody;
import java.io.*;
import java.util.Scanner;
import org.json.JSONObject;

public class Client
{
    private static OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public static void main (String[] args)
    {
        if(args.length > 0)
        {
            try
            {
                float pickupLat = Float.parseFloat(args[0]);
                float pickupLong = Float.parseFloat(args[1]);
                float dropoffLat = Float.parseFloat(args[2]);
                float dropoffLong = Float.parseFloat(args[3]);

                String getResponse = "";
                String url = "https://techtest.rideways.com/dave?pickup=" + pickupLat + "," + pickupLong + "&dropoff=" + dropoffLat + "," + dropoffLong;


                //GET Request
                Request request = new Request.Builder().url(url).get().build();
                try (Response response = client.newCall(request).execute())
                {
                    int responseCode = response.code();
                    System.out.println("RESPONSE CODE:" + responseCode);
                    getResponse = response.body().string();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                System.out.println(getResponse);


            }
            catch(NumberFormatException nfe)
            {
                System.out.println("Arguments must all be floating numbers ");
            }
        }
        else
        {
            System.out.println("Add arguments");
        }

    }
}


//https://techtest.rideways.com/dave?dropoff=51.470020,-0.454295&pickup=52.167241,-0.443187
//java -cp .:json-20190722.jar:okhttp-4.2.2.jar:okio-2.0.0.jar:kotlin-stdlib-common-1.3.50.jar:kotlin-stdlib-1.3.50.jar Client 51.470020 -0.454295 58.167241 -0.53187
//java -cp .:json-20190722.jar:okhttp-4.2.2.jar:okio-2.0.0.jar:kotlin-stdlib-common-1.3.50.jar:kotlin-stdlib-1.3.50.jar Client 51.470020 -0.454295 52.167241 -0.443187