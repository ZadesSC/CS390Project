import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Main class for manager the web crawler
 * Created by Darren on 3/29/2015.
 */
public class Crawler
{
    public int maxURLs;
    public String domain;
    public ArrayList<String> URLList;

    public Crawler(int maxURLs, String domain, ArrayList<String> URLList)
    {
        this.maxURLs = maxURLs;
        this.domain = domain;
        this.URLList = URLList;
    }
    public void start()
    {

    }

    public void fetchURL(String urlStr)
    {
        try
        {
            URL url = new URL(urlStr);

            System.out.println("Scanning " + urlStr + " url.path=" + url.getPath());

            //read in url
            InputStreamReader inputReader = new InputStreamReader(url.openStream());

            //read contents into string builder
            StringBuilder builder = new StringBuilder();




        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


    }
}
