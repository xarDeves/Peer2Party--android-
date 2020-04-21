package com.cups.splashin.peer2party.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "message")
data class EntityDataClass(

    var layoutRes: Int,
    var payload: String?,
    var date: String? = null,
    var alias: String? = null,
    var size: String? = null,
    var clicked: Boolean = false

) {
    @PrimaryKey(autoGenerate = true)
    var key: Int? = null
}


