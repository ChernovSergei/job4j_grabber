import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.DateTimeParser;
import utils.HabrCareerDateTimeParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class HabrCareerParse implements Parse {

    private static final String SOURCE_LINK = "https://career.habr.com";
    public static final String PREFIX = "/vacancies?page=";
    public static final String SUFFIX = "&q=Java%20developer&type=all";
    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    private static String retrieveDescription(String link) throws IOException {
        Connection connection = Jsoup.connect(link);
        Document document = connection.get();
        Element row = document.select(".vacancy-description__text").first();
        String description = row.text();
        return description;
    }

    @Override
    public List<Post> list(String link) {
        List<Post> result = new LinkedList<>();
        Connection connection = Jsoup.connect(link);
        Document document = null;
        try {
            document = connection.get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements rows = document.select(".vacancy-card__inner");
        rows.forEach(row -> {
            Element titleElement = row.select(".vacancy-card__title").first();
            Element dateElement = row.select(".vacancy-card__date").first();
            String dateString = dateElement.child(0).attr("datetime");
            LocalDateTime date = dateTimeParser.parse(dateString);
            Element linkElement = titleElement.child(0);
            String vacancyName = titleElement.text();
            String descriptionLink = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
            String description = null;
            try {
                description = retrieveDescription(descriptionLink);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            result.add(new Post(1, vacancyName, descriptionLink, description, date));
        });
        return result;
    }

    public static void main(String[] args) {
        HabrCareerDateTimeParser timeParser = new HabrCareerDateTimeParser();
        HabrCareerParse habrCareerParse = new HabrCareerParse(timeParser);
        List<Post> result = new LinkedList<>();
        for (int pageNumber = 1; pageNumber <= 5; pageNumber++) {
            String fullLink = "%s%s%d%s".formatted(SOURCE_LINK, PREFIX, pageNumber, SUFFIX);
            result = habrCareerParse.list(fullLink);
        }
        result.forEach(System.out::println);
    }
}
