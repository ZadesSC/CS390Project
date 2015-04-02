/**
 * Created by Darren on 3/29/2015.
 */
public class Driver
{
    public MySQLAbstraction db;
    public Crawler crawler;

    public Driver(String[] args)
    {
        

        this.db = new MySQLAbstraction();
        this.crawler = new Crawler(0, "purdue.edu", null, db);

        //this.db.resetDatabase();
        this.crawler.start();
        //this.crawler.fetchURL("http://www.purdue.edu");
    }

    public static void main(String[] args)
    {
        Driver driver = new Driver(args);
    }
}
