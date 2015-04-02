import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Darren on 3/29/2015.
 */
public class MySQLAbstraction
{
    public Connection connection = null;

    public String databaseURL = "jdbc:mysql://localhost:3306/CS390";
    public String username = "student";
    public String password = "cs390";

    public static final String URL_TABLE = "url_table";
    public static final String WORD_TABLE = "word_table";

    public Statement batchURLStatement;
    public Statement batchWordStatement;

    public int count = 0;

    public void openConnection() throws SQLException, IOException
    {

        this.connection = DriverManager.getConnection( this.databaseURL, this.username, this.password);
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

    public void insertURLtoURLTable(String url, String urlDescription) throws SQLException, IOException {
        this.openConnection();
        Statement stat = connection.createStatement();

        stat.executeUpdate(
                "INSERT INTO " + this.URL_TABLE + " (URL, Description)\n" +
                        "VALUES ( \"" + url + "\", \"" + urlDescription +"\" )");

        stat.close();
        this.connection.close();
    }

    public void batchInsertURLsStart() throws IOException, SQLException {
        this.openConnection();
        this.batchURLStatement = connection.createStatement();
    }

    public void addToBatchURL(String url, String urlDescription) throws SQLException {
        this.batchURLStatement.addBatch(
                "INSERT INTO " + this.URL_TABLE + " (URL, Description)\n" +
                        "VALUES ( \"" + url + "\", \"" + urlDescription + "\" )");
        this.count++;

    }

    public void batchInsertURLsFinish() throws SQLException
    {
        this.batchURLStatement.executeBatch();
        this.batchURLStatement.close();
        this.connection.close();

        System.out.println("amount: " + this.count);
        this.count = 0;
    }

}
