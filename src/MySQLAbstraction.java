import java.io.IOException;
import java.sql.*;

/**
 * Created by Darren on 3/29/2015.
 */
public class MySQLAbstraction {
    public Connection connection = null;

    public String databaseURL = "jdbc:mysql://localhost:3306/CS390";
    public String username = "student";
    public String password = "cs390";

    public static final String URL_TABLE = "url_table";
    public static final String WORD_TABLE = "word_table";

    public PreparedStatement batchURLStatement;
    public PreparedStatement batchWordStatement;

    public int count = 0;

    public void openConnection() throws SQLException, IOException {

        this.connection = DriverManager.getConnection(this.databaseURL, this.username, this.password);
    }

    public void closeConnection() throws SQLException {
        this.connection.close();
    }

    public void resetDatabase() throws SQLException, IOException
    {
        this.openConnection();
        Statement stat = this.connection.createStatement();

        // Delete the table first if any
        try
        {
            stat.executeUpdate("DROP TABLE " + this.WORD_TABLE + "");
            stat.executeUpdate("DROP TABLE " + this.URL_TABLE + ""); //If Exists???
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        this.createURLTable();
        this.createWordTable();

        stat.close();
        this.connection.close();
    }

    public void createURLTable() throws SQLException, IOException
    {
        Statement stat = this.connection.createStatement();

        stat.executeUpdate(
                "CREATE TABLE " + this.URL_TABLE +
                        "(" +
                        "URLID INT PRIMARY KEY NOT NULL AUTO_INCREMENT," +
                        "URL VARCHAR(512)," +
                        "Description VARCHAR(200)" +
                        ")"
        );
        stat.close();
    }
    public void createWordTable() throws SQLException, IOException
    {
        Statement stat = connection.createStatement();

        stat.executeUpdate(
                "CREATE TABLE " + this.WORD_TABLE                                                           +
                "("                                                                                         +
                        "Word VARCHAR(255) NOT NULL,"                                                       +
                        "URLID INT NOT NULL,"                                                               +
                        "FOREIGN KEY (URLID) REFERENCES " + this.URL_TABLE + "(URLID)"                                                                                   +
                ")"
        );
        stat.close();
    }


    //query to get an urlid from a url, does not open or close connections to make it faster
    public int getURLID(String url) throws SQLException {
        Statement stat = connection.createStatement();

        ResultSet rs = stat.executeQuery(
                "SELECT " + this.URL_TABLE + ".URLID \n" +
                        "FROM " + this.URL_TABLE + "\n" +
                        "WHERE " + this.URL_TABLE + ".URL = " + "\"" + url + "\""
        );

//        if(rs.getFetchSize() == 0)
//        {
//            System.out.println("Result of URL query yield size of 0 for url: " + url);
//            return 0;
//        }
//
//        if(rs.getFetchSize() > 1)
//        {
//            System.out.println("Result of URL query yield size greater than 1 for url: " + url);
//            return 0;
//        }

        rs.next();
        return rs.getInt("URLID");

//        SELECT url_table.URLID
//        FROM url_table
//        WHERE url_table.URL = "https://www.cs.purdue.edu/index.html";
    }


    //dont need
    public void insertURLtoURLTable(String url, String urlDescription) throws SQLException, IOException {
        this.openConnection();
        Statement stat = connection.createStatement();

        stat.executeUpdate(
                "INSERT INTO " + this.URL_TABLE + " (URL, Description)\n" +
                        "VALUES ( \"" + url + "\", \"" + urlDescription + "\" )");

        stat.close();
        this.connection.close();
    }

    public void batchInsertURLsStart() throws IOException, SQLException {

        String sqlStatement = "INSERT INTO url_table (URL, Description) VALUES (?,?)";

        this.openConnection();
        this.batchURLStatement = connection.prepareStatement(sqlStatement);
    }

    public void batchInsertURLs(String url, String urlDescription) throws SQLException {


        this.batchURLStatement.setString(1, url);
        this.batchURLStatement.setString(2, urlDescription);
        this.batchURLStatement.addBatch();

//        this.batchURLStatement.addBatch(
//                "INSERT INTO " + this.URL_TABLE + " (URL, Description)\n" +
//                        "VALUES ( \"" + url + "\", \"" + urlDescription + "\" )");

        this.count++;

    }

    public void batchInsertURLsFinish() throws SQLException
    {
        this.batchURLStatement.executeBatch();
        this.batchURLStatement.close();
        this.batchURLStatement.close();

        System.out.println("amount: " + this.count);
        this.count = 0;
    }

    public void batchInsertWordsStart() throws IOException, SQLException {

        String sqlStatement = "INSERT INTO word_table (URLID, Word) VALUES (?,?)";

        this.openConnection();
        this.batchWordStatement = connection.prepareStatement(sqlStatement);
    }

    public void batchInsertWords(int urlid, String word) throws SQLException {

        this.batchWordStatement.setInt(1, urlid);
        this.batchWordStatement.setString(2, word);
        this.batchWordStatement.addBatch();
//        this.batchWordStatement.addBatch(
//                "INSERT INTO " + this.WORD_TABLE + " (URLID, Word)\n" +
//                        "VALUES ( \"" + urlid + "\", \"" + word + "\" )");
        this.count++;

    }


    public void batchInsertWordsFinish() throws SQLException
    {
        this.batchWordStatement.executeBatch();
        this.batchWordStatement.close();
        this.connection.close();

        System.out.println("amount: " + this.count);
        this.count = 0;
    }

}
