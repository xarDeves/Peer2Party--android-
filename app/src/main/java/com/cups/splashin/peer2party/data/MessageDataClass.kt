package com.cups.splashin.peer2party.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "message")
class MessageDataClass(

    var layoutRes: Int,
    var text: String?,
    var bytes: ByteArray?

) {
    @PrimaryKey(autoGenerate = true)
    var key: Int? = null
}


