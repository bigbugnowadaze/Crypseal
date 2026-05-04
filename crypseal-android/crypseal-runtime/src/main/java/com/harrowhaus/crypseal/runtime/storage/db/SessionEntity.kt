package com.harrowhaus.crypseal.runtime.storage.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val title: String?,
    val state: String,
    val jsonlPath: String,
    val createdAt: String,
    val updatedAt: String
)
