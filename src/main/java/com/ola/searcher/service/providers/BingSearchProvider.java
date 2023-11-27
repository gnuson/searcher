package com.ola.searcher.service.providers;

import com.ola.searcher.service.UrlHandler;
import com.ola.searcher.service.UrlHandler.StringEntry;
import java.io.IOException;
import java.text.ParseException;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
/**
 * @remarks can only handle free tiers so no more than six requests per second and only 1000 requests per month.
 */
public class BingSearchProvider implements SearchProvider {

  /**
   * Static url.
   */
  static String url = "https://api.bing.microsoft.com/v7.0/search?q=";
  /**
   * Subscription keys.
   */
  private static String[] subscriptionKeys = new String[]{"7c8dbd6fa06340f99ca67f2ee4497671",
      "a88adcdd16114c1188464892f4af3040"};
  private final UrlHandler urlHandler;
  /**
   * Current subscriptionKey to use.
   */
  private int currentKey = 0;

  public BingSearchProvider(UrlHandler urlHandler) {
    this.urlHandler = urlHandler;
  }

  @Override
  public long searchCount(String searchTerm) throws ParseException {
    try {
      long toReturn = Math.round(parse(
          urlHandler.request(url + searchTerm,
              new StringEntry("Ocp-Apim-Subscription-Key", subscriptionKeys[currentKey]))));
      currentKey = (currentKey + 1) % 2;
      return toReturn;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  /**
   * Go through map result to find the estimated number of search results.
   *
   * @param content map of request response.
   * @return number of estimated search results.
   */
  private Double parse(Map content) throws ParseException {
    if (content.containsKey("webPages")) {
      Object webPages = content.get("webPages");
      if (webPages instanceof Map webPagesMap) {
        if (webPagesMap.containsKey("totalEstimatedMatches")) {
          Object totalEstimatedMatches = webPagesMap.get("totalEstimatedMatches");
          if (totalEstimatedMatches instanceof Double) {
            return (Double) (webPagesMap.get("totalEstimatedMatches"));
          }
          throw new ParseException("TotalEstimatedMatches wrong content type not Double", 0);
        }
        throw new ParseException("Missing TotalEstimatedMatches in WebPages", 0);
      }
      throw new ParseException("WebPages wrong content type not Map", 0);
    }
    throw new ParseException("Missing webPages in content", 0);
  }
}
