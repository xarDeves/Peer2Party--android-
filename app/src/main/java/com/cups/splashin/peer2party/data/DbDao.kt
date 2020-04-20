package com.cups.splashin.peer2party.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DbDao {

    @Insert
    suspend fun insert(entity: EntityDataClass)

    @Query("Select * from message")
    fun getAllMessages(): LiveData<List<EntityDataClass>>

    @Query("DELETE FROM message WHERE `key` = :position")
    fun delete(position: Int)

}