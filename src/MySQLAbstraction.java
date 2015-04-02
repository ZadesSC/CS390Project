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

    public void openConnection() throws SQLException, IOException
    {

        this.connection = DriverManager.getConnection( this.databaseURL, this.username, this.password);
    }

    public void resetDatabase() throws SQLException, IOException
    {
        Statement stat = connection.createStatement();

        // Delete the table first if any
        try
        {
            stat.executeUpdate("DROP TABLE " + this.URL_TABLE + ""); //If Exists???
            stat.executeUpdate("DROP TABLE " + this.WORD_TABLE + "");
        }
        catch (Exception e)
        {

        }

        this.createURLTable();
        this.createWordTable();
    }

    public void createURLTable() throws SQLException, IOException
    {
        Statement stat = connection.createStatement();

        stat.executeUpdate(
                "CREATE TABLE " + this.URL_TABLE                                                            +
                "("                                                                                         +
                        "URLID INT PRIMARY KEY NOT NULL AUTO_INCREMENT,"                                                   +
                        "URL VARCHAR(512),"                                                                 +
                        "Description VARCHAR(200)"                                                          +
                ")"
        );

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
    }

    public void insertURLtoURLTable(String url, String urlDescription)
    {

    }

}
