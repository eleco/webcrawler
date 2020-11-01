import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


public class Main {

    private static final Logger logger = Logger.getLogger("Main");


    public static void main(String[] args) throws Exception {
        String startUrl = args[0];
        if (startUrl != null) {

            Map<String, List<String>> sitemap = new Crawler(new DocumentFetcherImpl()).crawl(args[0]);

            for (Map.Entry<String, List<String>> e : sitemap.entrySet()) {
                System.out.println(e.getKey());
                for (String link : e.getValue()) {
                    System.out.println(" - " + link);
                }
            }
        }
    }


}

