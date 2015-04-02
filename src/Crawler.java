import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Main class for manager the web crawler
 * Created by Darren on 3/29/2015.
 */
public class Crawler
{
    public int maxURLs;
    public String domain;

    public ArrayList<String> URLList;
    public ArrayList<String> scannedURLList;

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
            int ch;
            while ((ch = inputReader.read()) != -1)
            {
                builder.append((char) ch);
            }

            //prints out html document to check if it works
            //System.out.println("Printing HTML:");
            //System.out.println(builder.toString());

            //find tags in html document
            String patternString =  "<a\\s+href\\s*=\\s*(\"[^\"]*\"|[^\\s>]*)\\s*>";

            Pattern pattern =
                    Pattern.compile(patternString,
                            Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(builder);

            while (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();
                String match = builder.substring(start, end);
                String urlFound = matcher.group(1);
                System.out.println(urlFound);


                // Check if it is already in the database

                //System.out.println(match);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


    }
}
