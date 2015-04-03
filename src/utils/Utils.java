package utils;

import org.apache.commons.validator.routines.UrlValidator;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Darren on 4/2/2015.
 */
public class Utils
{
    public static String getDomainName(String url){
        URI uri = null;
        try
        {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            //e.printStackTrace();
            return "";
        }

        //System.out.println("URL: " + url);

        String domain = uri.getHost();

        if(domain == null)
        {
            return "";
        }

        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }

    public static boolean isvalidURL(String url)
    {
        UrlValidator urlValidator = new UrlValidator();
        return urlValidator.isValid(url);
    }
}
