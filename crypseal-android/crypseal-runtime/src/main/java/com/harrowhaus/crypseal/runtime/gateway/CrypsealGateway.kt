package com.harrowhaus.crypseal.runtime.gateway

import com.harrowhaus.crypseal.runtime.storage.jsonl.JsonlEventWriter
import com.harrowhaus.crypseal.runtime.storage.db.ProjectEntity
import com.harrowhaus.crypseal.runtime.storage.db.SessionEntity
import com.harrowhaus.crypseal.runtime.storage.db.CrypsealDatabase
import java.util.UUID

class CrypsealGateway(
    private val db: CrypsealDatabase,
    private val eventWriter: JsonlEventWriter
) {
    private val activeLanes = mutableMapOf<String, SessionLane>()

    fun createProject(name: String, rootPath: String): ProjectEntity {
        val project = ProjectEntity(
            id = UUID.randomUUID().toString(),
            name = name,
            rootPath = rootPath,
            createdAt = System.currentTimeMillis().toString(),
            updatedAt = System.currentTimeMillis().toString(),
            lastSessionId = null
        )
        db.projectDao().insertProject(project)
        return project
    }

    fun createSession(projectId: String, title: String?): SessionEntity {
        val session = SessionEntity(
            id = UUID.randomUUID().toString(),
            projectId = projectId,
            title = title,
            state = "Idle",
            jsonlPath = "",
            createdAt = System.currentTimeMillis().toString(),
            updatedAt = System.currentTimeMillis().toString()
        )
        db.sessionDao().insertSession(session)
        return session
    }

    fun getLane(sessionId: String): SessionLane {
        return activeLanes.getOrPut(sessionId) {
            SessionLane(sessionId, eventWriter)
        }
    }
}
