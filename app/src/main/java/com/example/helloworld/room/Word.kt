package com.example.helloworld.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "word_table")
class Word(
    @PrimaryKey @ColumnInfo(name = "word") val word: String,
    @ColumnInfo(name = "create_time") val createTime: Long,
    @ColumnInfo(name = "length") val length: Int,
) {
}