package crawler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Created by Darren on 4/3/2015.
 */
public class SQLThread implements Runnable {
    public Connection connection;
    public URLData data;
    public MultiThreadCrawaler crawler;

    public SQLThread(MultiThreadCrawaler crawler) {
        //this.data = data;
        this.crawler = crawler;
    }

    @Override
    public void run()
    {
        //this.addDataToDB(this.data);
        this.grabDataFromList();
    }

    public void grabDataFromList()
    {
        try
        {

            this.addBatchDataToDB(this.crawler.URLsToBeAddedToSQLList);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //Forbatch
    public void addBatchDataToDB(ConcurrentLinkedQueue<URLData> dataList)
    {
        try
        {
            this.connection = DriverManager.getConnection(this.crawler.databaseURL, this.crawler.username, this.crawler.password);

            //add shit to db
            String URLStatement = "INSERT INTO url_table_mt (URL, Description, Image, Title) VALUES (?,?,?,?)";
            String wordStatement = "INSERT INTO word_table_mt (URLID, Word) SELECT url_table_mt.URLID, ? FROM url_table_mt WHERE url_table_mt.URL = ?";

            PreparedStatement URLPStatement = this.connection.prepareStatement(URLStatement);
            PreparedStatement wordPStatement = this.connection.prepareStatement(wordStatement);

            for (int x = 0; x < 500; x++) {
                if(this.crawler.URLsToBeAddedToSQLList.isEmpty())
                {
                    Thread.sleep(5000);
                    continue;
                }
                URLData data = this.crawler.URLsToBeAddedToSQLList.poll();

                if (data == null) {
                    continue;
                }

                URLPStatement.setString(1, data.URL);
                URLPStatement.setString(2, data.description);
                URLPStatement.setString(3, data.image);
                URLPStatement.setString(4, data.title);
                URLPStatement.addBatch();

                //URLPStatement.execute();

                this.crawler.currentSQLs++;
                System.out.println("List Size: " + this.crawler.URLsToBeAddedToSQLList.size() + " Current SQLs: " + this.crawler.currentSQLs);

                for (String word : data.words) {
                    if (word != null && !word.equals("") && !word.equals(" ")) {
                        wordPStatement.setString(1, word);
                        wordPStatement.setString(2, data.URL);
                        wordPStatement.addBatch();
                    }
                }

                //wordPStatement.executeBatch();
            }
            URLPStatement.executeBatch();
            wordPStatement.executeBatch();
            this.connection.close();
            System.out.println("test");

            this.crawler.SQLExecutor.execute(new SQLThread(this.crawler));

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    //For singles
    public void addDataToDB(URLData data)
    {
        try
        {
            this.connection = DriverManager.getConnection(this.crawler.databaseURL, this.crawler.username, this.crawler.password);

            //add shit to db
            String URLStatement = "INSERT INTO url_table_mt (URL, Description, Image, Title) VALUES (?,?,?,?)";
            String wordStatement = "INSERT INTO word_table_mt (URLID, Word) SELECT word_table_mt.URLID, ? FROM word_table_mt WHERE word_table_mt.URL = ?";

            PreparedStatement URLPStatement = this.connection.prepareStatement(URLStatement);
            PreparedStatement wordPStatement = this.connection.prepareStatement(wordStatement);

            URLPStatement.setString(1, data.URL);
            URLPStatement.setString(2, data.description);
            URLPStatement.setString(3, data.image);
            URLPStatement.setString(4, data.title);

            URLPStatement.execute();
            //this.connection.commit();

            this.crawler.currentSQLs++;
            System.out.println("Current SQLs: " + this.crawler.currentSQLs);

            for(String word: data.words)
            {
                if(word != null && !word.equals("") && !word.equals(" "))
                {
                    wordPStatement.setString(1, word);
                    wordPStatement.setString(2, data.URL);
                    wordPStatement.addBatch();
                }
            }

            wordPStatement.executeBatch();
           //this.connection.commit();

            this.connection.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

    }
}
