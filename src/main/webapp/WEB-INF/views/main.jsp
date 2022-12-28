<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-rbsA2VBKQhggwzxH7pPCaAqO46MgnOM80zW1RWuH61DGLwZJEdK2Kadq2F9CUG65" crossorigin="anonymous">
    <title>주소 검색</title>
</head>
<body>
    <div class="container-md mt-5">
        <div class="row">
            <div class="col">
                <div class="input-group mb-3">
                    <textarea id="address" class="form-control"  placeholder="주소 검색" aria-label="주소 검색" rows="3"></textarea>
                    <div class="btn-group" role="group" aria-label="Basic example">
                        <button id="add" type="button" class="btn btn-outline-primary">추가</button>
                        <button id="search" type="button" class="btn n btn-outline-primary">검색</button>
                    </div>
                </div>
            </div>
        </div>
        결과 값
        <ul id="resultList">
        </ul>
        <hr>
        입력 값
        <ul id="addressList">
        </ul>
    </div>
</body>
    <script src="js/jquery/jquery-3.6.1.min.js" type="text/javascript" ></script>
    <script src="js/addrSrch.js" type="text/javascript" ></script>
</html>