package com.harrowhaus.crypseal.runtime.storage.db

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
