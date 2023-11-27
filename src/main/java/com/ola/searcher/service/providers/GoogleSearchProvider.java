package com.ola.searcher.service.providers;

import com.ola.searcher.service.UrlHandler;
import java.io.IOException;
import java.text.ParseException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @remarks can only handle free tiers only 100 requests per day.
 */
@Service
@Slf4j
public class GoogleSearchProvider implements SearchProvider {

  /**
   * Key for sending search requests to google.
   */
  private static final String key = "AIzaSyDm2GeWE_YQYC10AKGk3kCCfaRoDN5kzWk";
  /**
   * Application identifier.
   */
  private static final String application = "e16149a12d08f426c";
  /**
   * Static url create from keys and static url information.
   */
  private static final String url = String.format(
      "https://www.googleapis.com/customsearch/v1?key=%s&cx=%s&q=", key, application);

  private final UrlHandler urlHandler;

  public GoogleSearchProvider(UrlHandler urlHandler) {
    this.urlHandler = urlHandler;
  }

  @Override
  public long searchCount(String searchTerm) throws ParseException {
    try {
      return parse(urlHandler.request(url + searchTerm));
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
  private long parse(Map content) throws ParseException {
    if (content.containsKey("searchInformation")) {
      Object searchInformation = content.get("searchInformation");
      if (searchInformation instanceof Map searchInformationMap) {
        if (searchInformationMap.containsKey("totalResults")) {
          Object totalResults = searchInformationMap.get("totalResults");
          if (totalResults instanceof String totalResultsString) {
            return Long.parseLong(totalResultsString);
          }
          throw new ParseException("TotalResults wrong content type not String", 0);
        }
        throw new ParseException("Missing TotalResults in SearchInformation", 0);
      }
      throw new ParseException("SearchInformation wrong content type not Map", 0);
    }
    throw new ParseException("Missing SearchInformation in content", 0);
  }
}
