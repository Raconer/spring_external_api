package com.find.load.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.find.load.model.search.FindLoad;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SearchService {

    @Value("${kakao.rest.api.key}")
    String apiKey;

    @Value("${kakao.rest.api.url}")
    String apiUrl;

    final String LOAD_REGEX = "([\s0-9가-힣A-Za-z\\·\\~\\.\\-]+(로|길).[0-9]*)";
    final Pattern loadPtr = Pattern.compile(LOAD_REGEX);

    ObjectMapper objectMapper = new ObjectMapper();

    public List<FindLoad> findLoadByAddrList(String[] addrList) {

        List<FindLoad> findLoads = new ArrayList<>();

        Arrays.stream(addrList).forEach(addr -> {
            String load = this.findLoad(addr);
            findLoads.add(new FindLoad(addr, load));
        });

        return findLoads;
    }

    public String findLoad(String addr) {
        Matcher matcher = loadPtr.matcher(addr);
        String load = null;

        while (matcher.find()) {
            load = this.searchKakao(matcher.group());
            if (load != null)
                break;
        }

        return load;
    }

    public String searchKakao(String addressStr) {

        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders header = new HttpHeaders();
            header.set("Authorization", "KakaoAK " + apiKey);
            HttpEntity<?> entity = new HttpEntity<>(header);

            UriComponents uri = UriComponentsBuilder
                    .fromHttpUrl(apiUrl)
                    .queryParam("query", addressStr)
                    .queryParam("page", "1")
                    .queryParam("size",
                            "1")
                    .build();
            ResponseEntity<?> response = restTemplate.exchange(
                    uri.toString(),
                    HttpMethod.GET, entity, Object.class);

            JSONParser jsonParser = new JSONParser();
            String bodyStr = objectMapper.writeValueAsString(response.getBody());
            JSONObject documents = (JSONObject) jsonParser.parse(bodyStr);
            JSONArray addressList = (JSONArray) documents.get("documents");
            if (!addressList.isEmpty()) {
                JSONObject address = (JSONObject) addressList.get(0);
                JSONObject roadAddress = (JSONObject) address.get("road_address");
                return roadAddress.get("road_name").toString();
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.info("ERROR : {}", e.toString());

        } catch (Exception e) {
            log.info("ERROR : {}", e.toString());
        }
        return null;
    }
}
