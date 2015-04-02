import org.apache.commons.cli.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Darren on 3/29/2015.
 */
public class Driver
{
    public MySQLAbstraction db;
    public Crawler crawler;

    public int maxURL = 1000;
    public String domain = null;

    public ConcurrentLinkedQueue<String> que;


    public Driver(String[] args)
    {
        this.que = new ConcurrentLinkedQueue<String>();

        //setting up the command line parser
        Option maxURL = OptionBuilder.withArgName("u")
                .hasArg()
                .withDescription("set the max number of urls to be crawled")
                .withType(Number.class)
                .create("u");

        Option domain = OptionBuilder.withArgName("d")
                .hasArg()
                .withDescription("limit crawl to domain")
                .create("d");

        Options options = new Options();
        options.addOption(maxURL);
        options.addOption(domain);


        CommandLineParser parser = new GnuParser();
        CommandLine cmd;
        try
        {
            cmd = parser.parse( options, args);

            if(cmd.hasOption("u"))
            {
                this.maxURL = Integer.parseInt(cmd.getOptionValue("u"));
                System.out.println("has u: " + this.maxURL );
            }
            if(cmd.hasOption("d"))
            {
                this.domain = cmd.getOptionValue("d");
                if(Utils.isvalidURL(this.domain))
                {
                    System.out.println("Domain URL not valid");
                    System.exit(-1);
                }

                System.out.println("has d: " + this.domain );
            }

            if(cmd.getArgList().size() ==0)
            {
                System.out.println("Need at least one URL");
                System.exit(-1);
            }

            //setup Queue

            for(Object arg: cmd.getArgList())
            {
                if(Utils.isvalidURL((String)arg))
                {
                    que.add((String)arg);
                }
            }

            if(que.size() == 0)
            {
                System.out.println("URLs not valid");
                System.exit(-1);
            }

            if(this.domain == null)
            {
                try {
                    this.domain = Utils.getDomainName(que.peek());
                }
                catch (URISyntaxException e)
                {
                    System.out.println("You fucked up");
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(-1);
        }



        this.db = new MySQLAbstraction();
        try
        {
            this.db.openConnection();
            this.db.resetDatabase();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(-1);
        }

        this.crawler = new Crawler(this.maxURL, this.domain, this.que, db);

        this.crawler.start();
        //this.crawler.fetchURL("http://www.purdue.edu");
    }

    public static void main(String[] args)
    {
        Driver driver = new Driver(args);
    }
}
