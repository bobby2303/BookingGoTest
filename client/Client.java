import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.*;
import java.util.Scanner;

public class Client
{
    private static OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");


    public static void main (String[] args)
    {
        System.out.println("Hello Client");
        String url = "https://techtest.rideways.com/dave", getResponse = "";

        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute())
        {
            getResponse = response.headers().toString() + response.body().string() ;
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        System.out.println(getResponse);


    }
}