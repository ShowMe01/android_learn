package com.example.helloworld.room

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory


@Database(entities = arrayOf(Word::class), version = 3, exportSchema = false)
abstract class WordRoomDatabase : RoomDatabase() {

    abstract fun wordDao(): WordDao

    companion object {

        const val TAG = "WordRoomDatabase"

        private const val ENCRYPT_DB_NAME = "word_database_2"
        private const val DB_NAME = "word_database"
        private val passphrase = SQLiteDatabase.getBytes("your_secure_key".toCharArray())
        private val factory = SupportFactory(passphrase)

        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 添加新的列，并为旧数据设置默认值为0
                database.execSQL("ALTER TABLE word_table ADD COLUMN create_time INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 首先添加新的列，初始化为0或合适的默认值
                database.execSQL("ALTER TABLE word_table ADD COLUMN length INTEGER NOT NULL DEFAULT 0")

                // 更新新列的值基于word列的长度
                database.execSQL("UPDATE word_table SET length = LENGTH(word)")
            }
        }


        @Volatile
        private var INSTANCE: WordRoomDatabase? = null

        fun getDataBase(
            context: Context,
            scope: CoroutineScope,
        ): WordRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, WordRoomDatabase::class.java, DB_NAME
                ).addCallback(
                    WordDatabaseCallback(scope)
                ).addMigrations(MIGRATION_1_2, MIGRATION_2_3).build()
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
                    val word1 = "Hello"
                    var word = Word(word1, createTime = System.currentTimeMillis(), word1.length)

                    wordDao.insert(word)
                    val word2 = "World!"
                    word = Word(word2, createTime = System.currentTimeMillis(), word2.length)
                    wordDao.insert(word)

                    val word3 = "TODO!"
                    word = Word(word3, createTime = System.currentTimeMillis(), word3.length)
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

