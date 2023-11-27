package com.ola.searcher.service.providers;

import java.text.ParseException;

public interface SearchProvider {

  /**
   * Requests search from search providers.
   *
   * @param searchTerm to ask search provider for.
   * @return estimated number of search result.
   */
  long searchCount(String searchTerm) throws ParseException;
}
