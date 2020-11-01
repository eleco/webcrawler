import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.core.Is.is;
import static org.jsoup.Jsoup.parse;
import static org.jsoup.parser.Parser.xmlParser;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class CrawlerTest {

    @Mock
    DocumentFetcher documentFetcher = mock(DocumentFetcher.class);


    @Test
    public void shouldCrawlInternalLinks() throws Exception {

        String html1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"><a href=\"http://test.com/sub1\">test</a></xml>";
        String html2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"><a href=\"http://test.com/sub2\">test</a></xml>";
        String html3 = "<?xml version=\"1.0\" encoding=\"UTF-8\"></xml>";

        when(documentFetcher.fetch(eq("http://test.com"))).thenReturn(parse(html1, "", xmlParser()));
        when(documentFetcher.fetch(eq("http://test.com/sub1"))).thenReturn(parse(html2, "", xmlParser()));
        when(documentFetcher.fetch(eq("http://test.com/sub2"))).thenReturn(parse(html3, "", xmlParser()));


        Crawler crawler = new Crawler(documentFetcher);
        Map<String, List<String>> map = crawler.crawl("http://test.com");

        assertThat(map, hasEntry(is("http://test.com"), containsInAnyOrder("http://test.com/sub1")));
        assertThat(map, hasEntry(is("http://test.com/sub1"), containsInAnyOrder("http://test.com/sub2")));


    }

    @Test
    public void shouldMapLinks_withIOException() throws Exception {

        String html = "<?xml version=\"1.0\" encoding=\"UTF-8\">" +
                "<a href=\"http://test.com/sub1\">test" +
                "<a href=\"http://test.com/sub2\">test" +
                "<a href=\"http://test.com/sub3\">test" +
                "</a></xml>";
        Document doc = parse(html, "", xmlParser());
        when(documentFetcher.fetch(eq("http://test.com"))).thenReturn(doc);
        when(documentFetcher.fetch(eq("http://test.com/sub1"))).thenThrow(new IOException());
        when(documentFetcher.fetch(eq("http://test.com/sub2"))).thenThrow(new IOException());
        when(documentFetcher.fetch(eq("http://test.com/sub3"))).thenThrow(new IOException());


        Crawler crawler = new Crawler(documentFetcher);
        Map<String, List<String>> map = crawler.crawl("http://test.com");

        assertThat(map, hasEntry(is("http://test.com"),
                containsInAnyOrder("http://test.com/sub1", "http://test.com/sub2", "http://test.com/sub3")));

    }

    @Test
    public void shouldNotCrawlExternalLinks() throws Exception {

        String html = "<?xml version=\"1.0\" encoding=\"UTF-8\"><a href=\"http://external.com\">test</a></xml>";
        Document doc = parse(html, "", xmlParser());
        when(documentFetcher.fetch(eq("http://test.com"))).thenReturn(doc);

        Crawler crawler = new Crawler(documentFetcher);
        Map<String, List<String>> map = crawler.crawl("http://test.com");

        assertThat(map, hasEntry(is("http://test.com"),
                containsInAnyOrder("http://external.com")));

        verify(documentFetcher, times(1)).fetch(any());

    }
}