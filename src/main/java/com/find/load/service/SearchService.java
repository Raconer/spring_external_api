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
    // KaKao api Key(application.properties에 저장합니다.)
    String apiKey;

    // KaKao api url 입니다.
    @Value("${kakao.rest.api.url}")
    String apiUrl;

    // 도로명 주소 가져오는 Regex
    final String LOAD_REGEX = "([\s0-9가-힣A-Za-z\\·\\~\\.\\-]+(로|길).[0-9]*)";
    // 도로명 주소를 검색 하니 Regex와 Pattern을 final로 고정 시킵니다.
    final Pattern loadPtr = Pattern.compile(LOAD_REGEX);

    // 검색한 API를 변환하기 위해 선언 합니다.
    ObjectMapper objectMapper = new ObjectMapper();

    /**
     * @param addrList
     * @return
     */
    public List<FindLoad> findLoadByAddrList(String[] addrList) {

        List<FindLoad> findLoads = new ArrayList<>();

        // 배열 하나 씩 도로명 주소를 검색합니다.
        Arrays.stream(addrList).forEach(addr -> {
            String load = this.findLoad(addr);
            // 검색완료한 데이터를 셋팅 합니다.
            findLoads.add(new FindLoad(addr, load));
        });

        return findLoads;
    }

    // 주소를 검색합니다.
    public String findLoad(String addr) {
        // 받아온 주소에 Regex에 Match된 데이터를 찾습니다.
        Matcher matcher = loadPtr.matcher(addr);
        String load = null;

        // 데이터가 있을경우 while문으로
        // 현재 한국에 있는 도로명인지 체크 합니다.
        while (matcher.find()) {
            // KaKao API를 사용하여 도로명 주소를 체크합니다.
            load = this.searchKakao(matcher.group());
            // 도로명 주소가 있을경우 break 합니다.
            if (load != null)
                break;
        }

        return load;
    }

    /**
     * @param addressStr
     * @return
     * @desc : 실질적으로 KaKao API에 도로명 주소가 존재하는지 체크 합니다.
     */
    public String searchKakao(String addressStr) {

        try {
            // 외부 API 호출 셋팅 합니다.
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders header = new HttpHeaders();
            header.set("Authorization", "KakaoAK " + apiKey);
            HttpEntity<?> entity = new HttpEntity<>(header);

            UriComponents uri = UriComponentsBuilder
                    .fromHttpUrl(apiUrl)
                    .queryParam("query", addressStr) // 도로명 주소를 입력
                    .queryParam("page", "1") // 존재 여부 체크 이므로 1번 페이지를 가져옵니다.
                    .queryParam("size",
                            "1") // 최소한의 갯수를 셋팅하기 위해 size도 1로 설정합니다.
                    .build();
            // KaKao API를 호출합니다.
            ResponseEntity<?> response = restTemplate.exchange(
                    uri.toString(),
                    HttpMethod.GET, entity, Object.class);

            // 받아온 Json데이터를 사용할수있게 가공합니다.
            JSONParser jsonParser = new JSONParser();
            String bodyStr = objectMapper.writeValueAsString(response.getBody());
            JSONObject documents = (JSONObject) jsonParser.parse(bodyStr);
            JSONArray addressList = (JSONArray) documents.get("documents");
            // Response Json에서 데이터가 존재하는 "documents"를 가져와 존재 여부를 확인합니다.
            if (!addressList.isEmpty()) {
                JSONObject address = (JSONObject) addressList.get(0);
                JSONObject roadAddress = (JSONObject) address.get("road_address");
                // 데이터가 존재 하면 도로명 주소를 Return합니다.
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
