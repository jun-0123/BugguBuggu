package com.example.buggubuggu.model

data class Item (
    val LOCPLC: String, // 소재지
    val NO: Int,     // 번호 소재지 오름차순
    val INDUTY: String, // 업종
    val BSSH_NM: String, // 업소명
    val APNTDT: String  // 지정일
)

data class ApiResponse(
    val header: Header,
    val body: Body
)

data class Items(
    val item:List<Item>
)

data class Header(
    val resultCode: String,
    val resultMsg: String
)
data class Body(
    val items:Items,
    val perPage: Int,     // 한 페이지 결과수
    val currentPage: Int, // 현재 페이지 번호
    val totalRows: Int    // 데이터 총 개수
)