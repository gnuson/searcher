package com.ola.searcher.rest;

import com.ola.searcher.service.SearchService;
import java.util.Arrays;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class SearchRestController {

  private final SearchService searchService;

  public SearchRestController(SearchService searchService) {
    this.searchService = searchService;
  }

  @GetMapping("search/")
  public ResponseEntity<String> getResults(@RequestParam String term) {
    if (term != null && !term.isBlank()) {
      String[] words = term.split("[ \\?,\\.\\!\\n]");
      if (words.length > 0) {
        if (words.length > 5) {
          return ResponseEntity.badRequest().body("Too many search terms 5 are allowed");
        }
        return ResponseEntity.ok(String.valueOf(
            Arrays.stream(words).map(searchService::getSearchCount).mapToLong(Long::longValue)
                .sum()));
      }
    }
    return ResponseEntity.badRequest().body("Missing search term");
  }
}
