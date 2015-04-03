package crawler;

/**
 * Created by Darren on 4/3/2015.
 */
public class CrawlerThread implements Runnable
{
    public MultiThreadCrawaler crawler;

    public CrawlerThread(MultiThreadCrawaler crawler)
    {
        this.crawler = crawler;
    }

    @Override
    public void run()
    {
        this.scanURL();
    }

    public void scanURL()
    {

    }
}
