package crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.Utils;

import java.io.IOException;

/**
 * Created by Darren on 4/3/2015.
 */
public class CrawlerThread implements Runnable
{
    public MultiThreadCrawaler crawler;
    public String url;

    public CrawlerThread(MultiThreadCrawaler crawler, String url)
    {
        this.crawler = crawler;
        this.url = url;
    }

    @Override
    public void run()
    {
        this.scanURL(url);
    }

    public void scanURL(String url)
    {
        try
        {
            Document doc = Jsoup.connect(url).get();

            Elements links = doc.select("a[href]");
            Elements words = doc.select("p");
            Elements images = doc.select("img");
            String title = doc.title();

            //grab required parts and add to db list

            //Grab first image
            String image = "";
            for(Element element: images)
            {
                image = element.absUrl("src");
                break;
            }

            //Grab words
            StringBuilder builder = new StringBuilder();
            for(Element element: words)
            {
                builder.append(element.text().toString());
            }
            //remove none word characters
            String combinedWords = builder.toString().replaceAll("[^\\p{L}\\p{Nd}]+", " ");

            String urlDescription;
            if(combinedWords.length() > 100)
            {
                urlDescription = combinedWords.substring(0,100);
            }
            else
            {
                urlDescription = combinedWords;
            }

            String[] wordList = combinedWords.split("[\\W]");

            //add to db
            //this.crawler.URLsToBeAddedToSQLList.add(new URLData(url, urlDescription, image, title, wordList));
            URLData urlData = new URLData(url, urlDescription, image, title, wordList);
            //this.crawler.SQLExecutor.execute(new SQLThread(this.crawler, urlData));
            this.crawler.URLsToBeAddedToSQLList.add(urlData);
            this.crawler.currentURLS++;
            System.out.println("Current URLS: " + this.crawler.currentURLS);

            //crawl for links
            for (Element element : links)
            {
                String link = element.attr("abs:href");
                this.addLinksToBeCrawled(link);
            }
        }
        catch (IOException e)
        {
            //Ignore for now
            //e.printStackTrace();
        }
    }

    public void addLinksToBeCrawled(String link)
    {
        String previousValue = this.crawler.alreadyScannedURLS.putIfAbsent(link, "");

        //have not yet been scanned
        if(this.crawler.currentURLS <= this.crawler.maxURLs)
        {
            if (previousValue == null)
            {
                if (Utils.getDomainName(link).equals(this.crawler.domain)) {
                    this.crawler.crawlerExecutor.execute(new CrawlerThread(this.crawler, link));
                }
                this.crawler.alreadyScannedURLS.put(link, "");
            }
        }
    }
}
