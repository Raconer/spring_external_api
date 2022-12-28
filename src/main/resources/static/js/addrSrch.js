let addressList = [];

let addressListUl = document.getElementById("addressList");

// 검색 주소 추가
$(document).on("click", "#add", () => {
    let addressElement = document.getElementById("address");
    let text = addressElement.value;
    let textList = text.split('\n')

    textList.forEach(address => {
        if (address) { 
            addAddressList(address);
        }
    });
    addressElement.value = "";
});

// 검색
$(document).on("click", "#search", () => {
    searchAjax()
        .then(data => { 
            addResultList(data.data);
            removeAddressList();
        })
        .catch(error => { 
            console.log(error);
        });
});

// 결과 값 출력
let addResultList = (loadList) => {
    loadList.forEach(load => {
        if (load) { 
            addResult(load.addr, load.load);
        }
    });
}

let addResult = (addr, load) => {
    let resultListUI = document.getElementById("resultList");
    let li = addAddressUI(addr + " -> " + load);
    resultListUI.append(li);
}

// 검색 주소 배열 추가
let addAddressList = (address) => { 
    addressList.push(address);
    let li = addAddressUI(address);
    addressListUl.append(li);
}

// 검색 주소 배열 제거
let removeAddressList = () => { 
    addressList = [];
    addressListUl.innerHTML = "";
}


// 검색 주소 화면 li tag 추가
let addAddressUI = (address) => { 
    let li = document.createElement("li");
    li.textContent = address;
    return li;
}

let searchAjax = () => {
    return new Promise((resolve, reject) => { 
     $.ajax({
            type: "POST",
         url: "",
            contentType: 'application/json',
            data: JSON.stringify(addressList),
            success: (data) => {
                resolve(data);
            },
            error : (error) => {
                alert("Error!");
                reject();
            }
        })
    })
}