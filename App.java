package it.unipi.lsmdb;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;;

public class App
{
    public static void main(String[] args) throws IOException {

        FileWriter file = new FileWriter("C://Users/pucci/Desktop/dataset.json");

        Document page = Jsoup.connect("https://untappd.com/search?q=beer&type=beer&sort=").get();
        Document doc = Jsoup.parse(String.valueOf(page));
        System.out.println(doc.title());

        Elements labelWrapper = page.getElementsByClass("label").select("img");

        /*Elements name = page.getElementsByClass("name");
        Elements brewery = page.getElementsByClass("brewery");
        Elements style = page.getElementsByClass("style");
        Elements abv = page.getElementsByClass("abv");
        Elements ibu = page.getElementsByClass("ibu");*/

        for(int index = 0; index < labelWrapper.size(); index++) {

            JSONObject document = new JSONObject();
            String label = labelWrapper.get(index).attr("src");
            document.put("label", label);

            /*jsonObject.put("name", name);
            jsonObject.put("brewery", brewery);
            jsonObject.put("style", style);
            jsonObject.put("abv", abv);
            jsonObject.put("ibu", ibu);*/

            file.write(document.toJSONString());
            file.write("\n");
        }
            file.close();

    }
}
