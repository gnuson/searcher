package com.ola.searcher.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

import com.google.gson.Gson;
import com.ola.searcher.service.UrlHandler;
import java.io.IOException;
import java.text.ParseException;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class SearchRestControllerTest {

  @SpyBean
  private SearchRestController searchRestController;

  @SpyBean
  private UrlHandler urlHandler;

  private String goodGoogle = """
      {
        "searchInformation": {
          "totalResults": "1000"
        }
      }
          """;
  private String goodBing = """
      {
        "webPages": {
          "totalEstimatedMatches": 5000
        }
      }
              """;

  @Test
  void performEmptySearch() {
    ResponseEntity<String> i = searchRestController.getResults("");
    assertEquals(i.getStatusCode(), HttpStatus.BAD_REQUEST);
  }

  @Test
  void performToBigSearch() {
    ResponseEntity<String> i = searchRestController.getResults("1 2 3 4 5 6 7 8 9");
    assertEquals(i.getStatusCode(), HttpStatus.BAD_REQUEST);
  }

  @Test
  void performGoodMockRequest() throws IOException {
    doReturn(MapOf(goodBing)).when(urlHandler).request(anyString(), any());
    doReturn(MapOf(goodGoogle)).when(urlHandler).request(anyString());
    ResponseEntity<String> i = searchRestController.getResults("Hello");
    assertEquals(i.getStatusCode(), HttpStatus.OK);
    assertEquals(i.getBody(), "6000");
  }

  @Test
  void performBadMockRequest() throws IOException {
    doReturn(MapOf(goodGoogle)).when(urlHandler).request(anyString(), any());
    doReturn(MapOf(goodBing)).when(urlHandler).request(anyString());
    RuntimeException exceptionThrown = assertThrows(RuntimeException.class, () -> {
      searchRestController.getResults("Hello");
    });
    assertEquals(exceptionThrown.getCause().getClass(), ParseException.class);
  }

  private Map MapOf(String json) {
    Gson gson = new Gson();
    return gson.fromJson(json, Map.class);
  }
}