import org.apache.commons.validator.routines.UrlValidator;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Darren on 4/2/2015.
 */
public class Utils
{
    public static String getDomainName(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }

    public static boolean isvalidURL(String url)
    {
        UrlValidator urlValidator = new UrlValidator();
        return urlValidator.isValid(url);
    }
}
