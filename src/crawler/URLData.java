package crawler;

/**
 * Created by Darren on 4/3/2015.
 */
public class URLData
{
    public String URL;
    public String description;
    public String image;
    public String title;
    public String[] words;

    public URLData(String url, String des, String image, String title, String[] words)
    {
        this.URL = url;
        this.description = des;
        this.image = image;
        this.title = title;
        this.words = words;
    }
}
