import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * Main class for manager the web crawler
 * Created by Darren on 3/29/2015.
 */
public class Crawler
{
    public int maxURLs;
    public String domain;

    public MySQLAbstraction db;

    public ConcurrentLinkedQueue<String> URLList;

    public Crawler(int maxURLs, String domain, ConcurrentLinkedQueue<String> URLList, MySQLAbstraction db)
    {
        this.maxURLs = maxURLs;
        this.domain = domain;
        this.URLList = URLList;

        this.db = db;
    }
    public void start()
    {
        while (!this.URLList.isEmpty())
        {
            this.fetchURL(this.URLList.poll());
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
                        this.URLList.add(link);
                    }
                }
                catch(Exception e)
                {
                    System.out.println("invalid URL: " + link +", skipping");
                }
                //System.out.println(link);
            }
        }
        catch (Exception e)
        {
            //System.out.println("Error fetching page, skipping");
            //Mostly 404 page not found
            e.printStackTrace();
        }
    }
}
