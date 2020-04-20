package com.cups.splashin.peer2party.data

import androidx.lifecycle.LiveData


class Repository(private val dao: DbDao) {

    val allMessages: LiveData<List<EntityDataClass>> = dao.getAllMessages()

    suspend fun insertEntity(entity: EntityDataClass){
        dao.insert(entity)
    }

}