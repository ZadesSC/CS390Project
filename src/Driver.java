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
        this.crawler = new Crawler(0, null, null);

        //this.db.resetDatabase();

    }

    public static void main(String[] args)
    {
        Driver driver = new Driver(args);
    }
}
