package crawler;

import utils.Utils;

import java.util.ArrayList;
import java.util.concurrent.*;

/**
 * Created by Darren on 4/3/2015.
 */
public class MultiThreadCrawaler
{
    public int maxURLs;
    public int currentURLS;
    public int currentSQLs;

    public String domain;

    //Database stuff
    public String databaseURL = "jdbc:mysql://localhost:3306/CS390";
    public String username = "student";
    public String password = "cs390";

    public ConcurrentLinkedQueue<URLData> URLsToBeAddedToSQLList;
    public LinkedBlockingQueue<Runnable> URLsToBeScannedList;

    public ConcurrentHashMap<String, String> alreadyScannedURLS;

    public int crawlerThreadCount = 20;
    public int SQLThreadcount = 26;
    public Executor crawlerExecutor;
    public Executor SQLExecutor;
    public ArrayList<Runnable> threads;

    public MultiThreadCrawaler(int maxURLs, String domain, ConcurrentLinkedQueue<String> URLList)
    {
        //init structures
        this.URLsToBeAddedToSQLList = new ConcurrentLinkedQueue<>();
        this.URLsToBeScannedList = new LinkedBlockingQueue<>();
        this.alreadyScannedURLS = new ConcurrentHashMap<>();
        this.threads = new ArrayList<>(this.crawlerThreadCount);

        //start executor
        this.crawlerExecutor = Executors.newFixedThreadPool(this.crawlerThreadCount);
        this.SQLExecutor = Executors.newFixedThreadPool(this.SQLThreadcount);
        for(int x = 0; x < this.SQLThreadcount; x++)
        {
            this.SQLExecutor.execute(new SQLThread(this));
        }

        this.maxURLs = maxURLs;
        this.currentURLS = 0;
        this.currentSQLs = 0;
        this.domain = domain;

        //add urls to list
        for(String url: URLList)
        {
            this.addToScanList(url);
        }
    }

    //Not thread safe, used only for the initial seed urls
    public void addToScanList(String url)
    {
        if(!this.alreadyScannedURLS.containsKey(url))
        {
            if(Utils.getDomainName(url).equals(this.domain))
            {
                this.crawlerExecutor.execute(new CrawlerThread(this, url));
            }
            this.alreadyScannedURLS.put(url, "");
        }
    }

}
