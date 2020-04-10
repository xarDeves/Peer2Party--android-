package com.cups.splashin.peer2party.data

import androidx.lifecycle.LiveData


class Repository(private val dao: DbDao) {

    val allMessages: LiveData<List<MessageDataClass>> = dao.getAllMessages()

    suspend fun insertEntity(entity: MessageDataClass){
        dao.insert(entity)
    }

   /* var observableText: MutableLiveData<MessageDataClass> = MutableLiveData()
    var observableImage: MutableLiveData<MessageDataClass> = MutableLiveData()
    //var observableProgressUpdater: MutableLiveData<Int> = MutableLiveData()

    var messages: MutableList<MessageDataClass> = mutableListOf()
*/
}