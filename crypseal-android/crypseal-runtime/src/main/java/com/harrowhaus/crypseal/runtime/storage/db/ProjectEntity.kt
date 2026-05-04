package com.harrowhaus.crypseal.runtime.storage.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String,
    val name: String,
    val rootPath: String,
    val createdAt: String,
    val updatedAt: String,
    val lastSessionId: String?
)
