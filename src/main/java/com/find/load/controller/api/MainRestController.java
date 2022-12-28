package com.find.load.controller.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.find.load.model.common.DefDataRes;
import com.find.load.model.search.FindLoad;
import com.find.load.service.SearchService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class MainRestController {

    // 주소 검색 Service
    SearchService searchService;

    /**
     * @param addrList
     * @return
     * @Desc : 문자열에서 도로명 주소를 찾는 API
     */
    @PostMapping
    public ResponseEntity<?> search(@RequestBody String[] addrList) {
        List<FindLoad> findLoads = this.searchService.findLoadByAddrList(addrList);
        return ResponseEntity.ok(new DefDataRes(HttpStatus.OK, findLoads));
    }
}
