package jsoup;


import java.io.IOException;
import java.net.URL;

public class Main {
    public static void main(String[] args) throws IOException {
        URL url = new URL("https://www.newhomesource.com/communities/tx/dallas-area/lavon");
        RequestManager requestManager = new RequestManager();
        requestManager.connect("/home/dmytro/IdeaProjects/Lab12/scrapes.db");
        System.out.println(requestManager.getHTMLByURL(url));
    }
}