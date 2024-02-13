import utils.HabrCareerDateTimeParser;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store {

    private Connection connection;

    PsqlStore(Properties config) {
        try (InputStream input = PsqlStore.class.getResourceAsStream("db/liquibase.properties")) {
            config.load(input);
            Class.forName(config.getProperty("driver-class-name"));
            connection = DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private void setIdFromDB(ResultSet resultSet, Post post) throws SQLException {
        int id = -1;
        if (resultSet.next()) {
            id = resultSet.getInt(1);
        }
        if (id != -1) {
            post.setId(id);
        }
    }

    private Post createPost(ResultSet resultSet) throws SQLException {
        return new Post(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(4), resultSet.getString(3), resultSet.getTimestamp(5).toLocalDateTime());
    }

    @Override
    public void save(Post post) {

        try (PreparedStatement statement = connection.prepareStatement("insert into post(name, description, link, created) values (?, ?, ?, ?) on conflict (link) do nothing", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getDescription());
            statement.setString(3, post.getLink());
            statement.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            statement.setString(5, post.getTitle());
            statement.setString(6, post.getDescription());
            statement.setTimestamp(7, Timestamp.valueOf(post.getCreated()));
            statement.execute();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                setIdFromDB(resultSet, post);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        ResultSet resultSet;
        List<Post> result = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("select * from post")) {
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(createPost(resultSet));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Post findById(int id) {
        ResultSet resultSet;
        Post result = null;
        try (PreparedStatement statement = connection.prepareStatement("select * from post where id = ?")) {
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                result = new Post(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(4), resultSet.getString(3), resultSet.getTimestamp(5).toLocalDateTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void close() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    public static void main(String[] args) {
        String sourceLink = "https://career.habr.com";
        String prefix = "/vacancies?page=";
        String suffix = "&q=Java%20developer&type=all";

        HabrCareerDateTimeParser timeParser = new HabrCareerDateTimeParser();
        HabrCareerParse habrCareerParse = new HabrCareerParse(timeParser);
        List<Post> result = new LinkedList<>();
        PsqlStore store = new PsqlStore(new Properties());
        for (int pageNumber = 1; pageNumber <= 5; pageNumber++) {
            String fullLink = "%s%s%d%s".formatted(sourceLink, prefix, pageNumber, suffix);
            result.addAll(habrCareerParse.list(fullLink));
        }
        result.forEach(store::save);
        store.getAll().forEach(System.out::println);
        System.out.println();
        System.out.println(store.findById(1));
    }
}
