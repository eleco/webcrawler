import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.logging.Logger;

public class Crawler {

    private static final Logger logger = Logger.getLogger("Crawler");

    private final DocumentFetcher documentFetcher;

    public Crawler(DocumentFetcher documentFetcher) {
        this.documentFetcher = documentFetcher;
    }


    public Map<String, List<String>> crawl(String baseUrl) throws URISyntaxException {

        final Set<String> visited = new HashSet<String>();

        final Queue<String> candidates = new LinkedList<String>();

        final Map<String, List<String>> sitemap = new HashMap<>();

        candidates.add(baseUrl);


        while (!candidates.isEmpty()) {

            String polledUrl = candidates.poll();

            if (visited.contains(polledUrl)) {
                continue;
            }
            visited.add(polledUrl);


            try {
                logger.info("crawling " + polledUrl);
                Document document = documentFetcher.fetch(polledUrl);
                Elements linksOnPage = document.select("a[href]");

                for (Element page : linksOnPage) {
                    String childUrl = page.attr("abs:href");
                    sitemap.computeIfAbsent(polledUrl, k -> new ArrayList<>()).add(childUrl);
                    if (childUrl.contains(getDomainName(baseUrl))) {
                        candidates.add(childUrl);
                    }
                }
            } catch (IOException ioException) {
                logger.warning("unable to crawl " + polledUrl + " -> " + ioException.getMessage());
            }

        }

        return sitemap;

    }


    public static String getDomainName(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }

}
