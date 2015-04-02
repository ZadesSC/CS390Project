import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * Main class for manager the web crawler
 * Created by Darren on 3/29/2015.
 */
public class Crawler
{
    public int maxURLs;
    public int currentURLs;
    public String domain;

    public MySQLAbstraction db;

    public ConcurrentLinkedQueue<String> URLList;
    public ConcurrentHashMap<String, String>  alreadyAddedURLs;

    public Crawler(int maxURLs, String domain, ConcurrentLinkedQueue<String> URLList, MySQLAbstraction db)
    {
        this.currentURLs = 0;
        this.maxURLs = maxURLs;
        this.domain = domain;

        this.db = db;

        this.URLList = new ConcurrentLinkedQueue<>();
        this.alreadyAddedURLs = new ConcurrentHashMap<>();
        while(!URLList.isEmpty() && this.currentURLs <= this.maxURLs)
        {
            this.addURLToQueue(URLList.poll());
        }
    }
    public void start()
    {
        while (!this.URLList.isEmpty() && this.currentURLs <= this.maxURLs)
        {
            this.fetchURL(this.URLList.poll());
            System.out.println(this.currentURLs);
        }
        System.out.println(this.URLList.size());
    }

    public void fetchURL(String urlStr)
    {

        try
        {
            Document doc = Jsoup.connect(urlStr).get();

            Elements links = doc.select("a[href]");

            //Body?
            //System.out.println(doc.body().text());

            //Links
            for(Element element: links)
            {
                String link = element.attr("abs:href");

                try
                {
                    if(Utils.getDomainName(link).equals(this.domain))
                    {
                        //this.db.insertURLtoURLTable(link, );
                        this.addURLToQueue(link);
                    }
                }
                catch(Exception e)
                {
                    //System.out.println("invalid URL: " + link +", skipping");
                }
                //System.out.println(link);
            }

            //add to db
            //this.db.insertURLtoURLTable();
        }
        catch (Exception e)
        {
            //System.out.println("Error fetching page, skipping");
            //Mostly 404 page not found
            //e.printStackTrace();
        }
    }

    public void addURLToQueue(String url)
    {
        if(!this.alreadyAddedURLs.containsKey(url))
        {
            //System.out.println(this.alreadyAddedURLs.get(url));
            this.URLList.add(url);
            this.currentURLs++;
            //System.out.println(url);
        }
        this.alreadyAddedURLs.put(url, "");
    }
}
