import org.jsoup.nodes.Document;

import java.io.IOException;

class DocumentFetcherImpl implements DocumentFetcher {

    public Document fetch(String url) throws IOException {
        return SSLHelper.getConnection(url).get();
    }
}
