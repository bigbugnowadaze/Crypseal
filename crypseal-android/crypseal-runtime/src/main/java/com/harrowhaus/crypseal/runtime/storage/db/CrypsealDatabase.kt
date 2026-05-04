package com.harrowhaus.crypseal.runtime.storage.db

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
