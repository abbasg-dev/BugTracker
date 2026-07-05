package com.example.bugtracker.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(entities = [Issue::class], version = 1)
abstract class BugTrackerDatabase : RoomDatabase() {

    abstract fun issueDao(): IssueDao

    companion object {
        @Volatile private var INSTANCE: BugTrackerDatabase? = null

        fun getDatabase(context: Context): BugTrackerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BugTrackerDatabase::class.java,
                    "bugtracker_db"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}
