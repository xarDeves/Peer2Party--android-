package com.cups.splashin.peer2party.android.app.data

import androidx.lifecycle.LiveData


class Repository(dao: DbDao) {

    val allMessages: LiveData<List<EntityDataClass>> = dao.getAllMessages()

/*        suspend fun insertEntity(entity: EntityDataClass) {
            dao.insert(entity)
        }

        suspend fun deleteEntity(position: Int) {
            dao.delete(position)
        }

        suspend fun updateChecked(isClicked: Boolean, position: Int) {
            dao.updateChecked(isClicked, position)
    }
    */
}