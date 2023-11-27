package com.ola.searcher.service;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import javax.net.ssl.HttpsURLConnection;
import org.springframework.stereotype.Component;

/**
 * Create and handle the http request.
 */
@Component
public class UrlHandler {

  /**
   * Request data from urlString.
   *
   * @param urlString url to make requests to.
   * @param pairs     for additional request properties to add to connection.
   * @return a map of all the results.
   * @throws IOException if problem with creating url, open connection of getting input stream.
   */
  public Map request(String urlString, StringEntry... pairs) throws IOException {
    URL url = new URL(urlString);
    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
    Arrays.stream(pairs).forEach(pair -> connection.setRequestProperty(pair.key, pair.value));
    InputStream stream = connection.getInputStream();
    String response = new Scanner(stream).useDelimiter("\\A").next();
    Gson gson = new Gson();
    return gson.fromJson(response, Map.class);
  }

  public record StringEntry(String key, String value) {

  }
}
