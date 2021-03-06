package crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.Utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Map;
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
    public int bodyCount = 0;
    public boolean finishedFlag = false;

    public String domain;

    public MySQLAbstraction db;

    public ConcurrentLinkedQueue<String> URLList;
    public ConcurrentHashMap<String, String> wordList;
    public ConcurrentHashMap<String, String>  alreadyAddedURLs;

    PrintWriter writer;
    PrintWriter wordWriter;

    public Crawler(int maxURLs, String domain, ConcurrentLinkedQueue<String> URLList, MySQLAbstraction db)
    {
        this.currentURLs = 0;
        this.maxURLs = maxURLs;
        this.domain = domain;

        this.db = db;

        this.URLList = new ConcurrentLinkedQueue<>();
        this.alreadyAddedURLs = new ConcurrentHashMap<>();
        this.wordList = new ConcurrentHashMap<>();


        while(!URLList.isEmpty())
        {
            this.addURLToQueue(URLList.poll());
        }
    }
    public void start()
    {
        try
        {
            this.db.batchInsertURLsStart();

            writer = new PrintWriter("the-file-name.txt", "UTF-8");
            wordWriter = new PrintWriter("wordFile.txt", "UTF-8");

            while (!this.URLList.isEmpty() && this.bodyCount <= this.maxURLs)
            {
                this.fetchURL(this.URLList.poll());
                //System.out.println("current URL: " + this.currentURLs);
            }
            System.out.println("Queue size: " + this.URLList.size());
            System.out.println("body count: " + this.bodyCount);
            this.db.batchInsertURLsFinish();

            this.addWordsToDatabase();

        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void fetchURL(String urlStr)
    {

        try
        {
            Document doc = Jsoup.connect(urlStr).get();

            Elements links = doc.select("a[href]");
            Elements words = doc.select("p");
            Elements images = doc.select("img");
            String title = doc.title();

            String image = "";
            for(Element element: images)
            {
                //for each element get the srs url
                image = element.absUrl("src");


                //System.out.println("Image Found!");
                //System.out.println("src attribute is : "+src);

            }
            //Body?
            //System.out.println(doc.body().text());

            //Links
            //ignore getting links if we over the limit

            for (Element element : links)
            {
                String link = element.attr("abs:href");

                this.addURLToQueue(link);

                //System.out.println(link);
            }

            //add to db
            //this.db.insertURLtoURLTable(urlStr, doc.body().text().substring(0, 1000));
            if(this.bodyCount <= this.maxURLs)
            {
                //store words
                StringBuilder builder = new StringBuilder();
                for(Element element: words)
                {
                    builder.append(element.text().toString());
                }

                //add links, this comes after words so that the description can be extracted
                this.db.batchInsertURLs(urlStr, builder.substring(0, 100), image, title);
                this.bodyCount++;
                System.out.println("adding to db: " + this.bodyCount + " link: " + urlStr + " with text: " + builder.substring(0, 100) + ", " + image + ", " + title + ")");

                writer.write("INSERT INTO url_table (URL, Description, Image, Title) VALUES ( \"" + urlStr + "\", \"" + builder.substring(0, 100) + "\", \"" + image + "\", \"" + title + "\");\n\n");

                this.wordList.put(urlStr, builder.toString());

                wordWriter.write(urlStr + "\n" + builder.toString() + "\n\n");
            }
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
        if(!this.alreadyAddedURLs.containsKey(url)  && Utils.getDomainName(url).equals(this.domain))
        {
            //System.out.println(this.alreadyAddedURLs.get(url));
            this.URLList.add(url);
            this.currentURLs++;
            //System.out.println(url);
        }

        this.alreadyAddedURLs.put(url, "");
    }

    public void addWordsToDatabase() throws IOException, SQLException {
        this.db.batchInsertWordsStart();
        for(Map.Entry<String, String> pair: this.wordList.entrySet())
        {
            int urlid = this.db.getURLID(pair.getKey());

            String[] splitWords = pair.getValue().split("[\\W]");
            for(String word: splitWords)
            {
                if(word.equals(""))
                {
                    break;
                }

                this.db.batchInsertWords(urlid, word);
                System.out.println("adding word: " + word);

            }
            //System.out.println("adding word...");
        }
        this.db.batchInsertWordsFinish();
    }
}
