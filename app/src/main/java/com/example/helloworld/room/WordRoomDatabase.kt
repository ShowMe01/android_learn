package com.example.helloworld.room

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory


@Database(entities = arrayOf(Word::class), version = 1, exportSchema = false)
abstract class WordRoomDatabase : RoomDatabase() {

    abstract fun wordDao(): WordDao

    companion object {

        const val TAG = "WordRoomDatabase"

        private const val ENCRYPT_DB_NAME = "word_database_2"
        private const val DB_NAME = "word_database"
        private val passphrase = SQLiteDatabase.getBytes("your_secure_key".toCharArray())
        private val factory = SupportFactory(passphrase)

        @Volatile
        private var INSTANCE: WordRoomDatabase? = null

        fun getDataBase(
            context: Context,
            scope: CoroutineScope,
        ): WordRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WordRoomDatabase::class.java,
                    DB_NAME
                ).addCallback(
                    WordDatabaseCallback(scope)
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class WordDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            Log.d(TAG, "onCreate: ")
            INSTANCE?.let { database ->
                scope.launch {
                    val wordDao = database.wordDao()

                    // Delete all content here.
                    wordDao.deleteAll()

                    // Add sample words.
                    var word = Word("Hello")
                    wordDao.insert(word)
                    word = Word("World!")
                    wordDao.insert(word)

                    // TODO: Add your own words!
                    word = Word("TODO!")
                    wordDao.insert(word)
                }
            }
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            Log.d(TAG, "onOpen: ")
        }

        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
            super.onDestructiveMigration(db)
            Log.d(TAG, "onDestructiveMigration: ")
        }

    }

}

