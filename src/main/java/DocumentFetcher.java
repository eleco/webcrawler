import org.jsoup.nodes.Document;

import java.io.IOException;

public interface DocumentFetcher {


    Document fetch(String url) throws IOException;
}


