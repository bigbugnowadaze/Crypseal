import os

root = r"c:\CHANGERS\Crypseal\crypseal-android\crypseal-runtime\src\main\java\com\harrowhaus\crypseal\runtime\storage\db"
os.makedirs(root, exist_ok=True)

# Project Entity
project_entity = """package com.harrowhaus.crypseal.runtime.storage.db

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
"""

# Session Entity
session_entity = """package com.harrowhaus.crypseal.runtime.storage.db

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
"""

# EventIndex Entity
event_index_entity = """package com.harrowhaus.crypseal.runtime.storage.db

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
"""

# DAOs
daos = """package com.harrowhaus.crypseal.runtime.storage.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects")
    fun getAllProjects(): List<ProjectEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProject(project: ProjectEntity)
}

@Dao
interface SessionDao {
    @Query("SELECT * FROM sessions WHERE projectId = :projectId")
    fun getSessionsForProject(projectId: String): List<SessionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSession(session: SessionEntity)
}

@Dao
interface EventIndexDao {
    @Query("SELECT * FROM event_index WHERE sessionId = :sessionId ORDER BY createdAt ASC")
    fun getEventIndexForSession(sessionId: String): List<EventIndexEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEventIndex(eventIndex: EventIndexEntity)
}
"""

# Database
database = """package com.harrowhaus.crypseal.runtime.storage.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ProjectEntity::class, SessionEntity::class, EventIndexEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CrypsealDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun sessionDao(): SessionDao
    abstract fun eventIndexDao(): EventIndexDao
}
"""

def write_file(filename, content):
    with open(os.path.join(root, filename), "w") as f:
        f.write(content)

write_file("ProjectEntity.kt", project_entity)
write_file("SessionEntity.kt", session_entity)
write_file("EventIndexEntity.kt", event_index_entity)
write_file("Daos.kt", daos)
write_file("CrypsealDatabase.kt", database)

print("A03 Implemented.")
