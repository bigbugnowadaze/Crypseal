package com.harrowhaus.crypseal.runtime.storage.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event_index")
data class EventIndexEntity(
    @PrimaryKey val id: String,
    val sessionId: String,
    val type: String,
    val createdAt: String,
    val summary: String?,
    val jsonlOffset: Long?
)
