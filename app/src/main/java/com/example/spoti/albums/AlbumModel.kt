package com.example.spoti.albums


data class AlbumModel(
    var artist: String = "",
    var title: String = "",
    var img: String = "",
    var url: String = "",
    var year: Long = 0
)