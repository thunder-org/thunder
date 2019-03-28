package org.conqueror.thunder.thunderpeacock.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/peacock-search")
public class PeacockController {

    private enum TermType {
        TITLE,
        BODY
    }

    @Value("${search.url}")
    private String searchUrl;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping(value = {"/title"}, produces = "application/json; charset=utf8", method = {RequestMethod.GET})
    public ResponseEntity<String> searchTitle(@RequestParam(value = "search_term", required = true) String searchTerm) {

        try {
            return search(TermType.TITLE, searchTerm);
        } catch (Exception e) {
            return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = {"/body"}, produces = "application/json; charset=utf8", method = {RequestMethod.GET})
    public ResponseEntity<String> searchBody(@RequestParam(value = "search_term", required = true) String searchTerm) {

        try {
            return search(TermType.BODY, searchTerm);
        } catch (Exception e) {
            return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
        }
    }

    private ResponseEntity<String> search(TermType termType,
                                          String searchTerm) throws Exception {
        Map<String, Map<String, Map<String, String>>> map = buildHeader(termType, searchTerm);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        HttpEntity<String> param = new HttpEntity<>(objectMapper.writeValueAsString(map), headers);
        return restTemplate.exchange(searchUrl, HttpMethod.POST, param, String.class);
    }

    private Map<String, Map<String, Map<String, String>>> buildHeader(TermType termType,
                               String searchTerm) {
        Map<String, Map<String, Map<String, String>>> map1 = new HashMap<>();
        Map<String, Map<String, String>> map2 = new HashMap<>();
        Map<String, String> map3 = new HashMap<>();

        map3.put((termType == TermType.TITLE) ? "title_terms" : "body_terms", searchTerm);
        map2.put("term", map3);
        map1.put("query", map2);
        return map1;
    }
}
