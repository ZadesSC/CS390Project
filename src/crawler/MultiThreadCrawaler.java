package crawler;

import utils.Utils;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Darren on 4/3/2015.
 */
public class MultiThreadCrawaler
{
    public int maxURLs;
    public int currentURLS;

    public String domain;

    public MultiThreadMySQL db;

    public ConcurrentLinkedQueue<URLData> URLsToBeAddedToSQLList;
    public ConcurrentLinkedQueue<String> URLsToBeScannedList;

    public ConcurrentHashMap<String, String> alreadyScannedURLS;

    public int threadCount = 10;
    public Executor crawlerExecutor;
    public ArrayList<Runnable> threads;

    public MultiThreadCrawaler(int maxURLs, String domain, ConcurrentLinkedQueue<String> URLList, MultiThreadMySQL db)
    {
        //init scturues
        this.URLsToBeAddedToSQLList = new ConcurrentLinkedQueue<>();
        this.URLsToBeScannedList = new ConcurrentLinkedQueue<>();
        this.alreadyScannedURLS = new ConcurrentHashMap<>();
        this.threads = new ArrayList<>(this.threadCount);

        this.maxURLs = maxURLs;
        this.domain = domain;
        this.db = db;

        //add urls to list
        for(String url: URLList)
        {
            this.addToScanList(url);
        }

        //start executor
        this.crawlerExecutor = Executors.newFixedThreadPool(this.threadCount);
        for(int x = 0; x < this.threadCount; x++)
        {
            Runnable crawlerSlave= new CrawlerThread(this);
            threads.add(crawlerSlave);
            this.crawlerExecutor.execute(crawlerSlave);
        }
    }

    //Not thread safe, used only for the initial seed urls
    public void addToScanList(String url)
    {
        if(!this.alreadyScannedURLS.containsKey(url))
        {
            if(Utils.getDomainName(url).equals(this.domain))
            {
                this.URLsToBeScannedList.add(url);
            }
            this.alreadyScannedURLS.put(url, "");
        }
    }

}
