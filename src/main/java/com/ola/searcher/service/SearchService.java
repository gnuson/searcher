package com.ola.searcher.service;

import com.ola.searcher.service.providers.SearchProvider;
import java.text.ParseException;
import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

/**
 * Service to handle the request to find estimated number of search results from available search
 * providers.
 */
@Service
@EnableCaching
public class SearchService {

  /**
   * List of all available search providers that implement the SearchProvider interface.
   */
  private final List<SearchProvider> searchProviders;

  public SearchService(List<SearchProvider> searchProviders) {
    this.searchProviders = searchProviders;
  }

  /**
   * Ask search providers for estimated number of search results.
   *
   * @param searchTerm to search for.
   * @return number of estimated search results.
   * @Remarks this method is cached so calls for the same searchTerm will hit cache and not be
   * forwarded to searchProviders.
   */
  @Cacheable("searchTerm")
  public long getSearchCount(String searchTerm) {
    return searchProviders.stream().map(searchProvider -> {
          try {
            return searchProvider.searchCount(searchTerm);
          } catch (ParseException e) {
            throw new RuntimeException(e);
          }
        })
        .mapToLong(Long::longValue).sum();
  }
}
