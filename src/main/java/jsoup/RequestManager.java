package jsoup;

import lombok.SneakyThrows;

import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestManager {
    private Connection co;

    @SneakyThrows
    public void connect(String dbURL) {
        try {
            Class.forName("org.sqlite.JDBC");
            co = DriverManager.getConnection(
                    "jdbc:sqlite:"+dbURL
            );
            System.out.println("Connected!");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @SneakyThrows
    private void addHTMLToCache(URL url) {
        String query = String.format("INSERT INTO parses (url, html) VALUES ('" +
                url + "', '%s');", parseToData(url));
        System.out.println(query);
        Statement statement = co.createStatement();
        statement.executeUpdate(query);
    }

    @SneakyThrows
    public String getHTMLByURL(URL url) {
        String query = "SELECT url, html FROM parses WHERE url = '" + url + "'";
        Statement statement = co.createStatement();
        ResultSet rs = statement.executeQuery(query);
        if (rs.next()) {
            return rs.getString("html");
        }
        addHTMLToCache(url);
        return getHTMLByURL(url);
    }

    @SneakyThrows
    private String parseToData(URL url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Api-Key", "");
            connection.connect();
            String html = new Scanner(connection.getInputStream()).useDelimiter("\\Z").next();
            Pattern pattern = Pattern.compile("<script type=application\\/ld\\+json>[\\S\\s]*?<\\/script>");
            Matcher matcher = pattern.matcher(html);
            List<String> matchesList = new ArrayList<>();
            while (matcher.find()) {
                matchesList.add(matcher.group());
            }
            return String.valueOf(matchesList);
        }
        catch (Exception e) {
            return "";
        }
    }
}
