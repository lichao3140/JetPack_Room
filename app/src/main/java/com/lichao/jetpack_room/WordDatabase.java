package com.lichao.jetpack_room;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Word.class}, version = 1, exportSchema = false)
public abstract class WordDatabase extends RoomDatabase {

    private static WordDatabase INSTANCE;

    static synchronized WordDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), WordDatabase.class, "word_database")
                    //.fallbackToDestructiveMigration() //强制数据库版本升级，有数据丢失，不采取
                    .addMigrations()// 数据库版本升级管理
                    .build();
        }
        return INSTANCE;
    }

    public abstract WordDao getWordDao();

    /**
     * 数据库版本从 2 升级到 3
     * 数据增加一行字段 bar_data
     */
    static final Migration MIGRATION_2_3 = new Migration(2,3) {

        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE word ADD COLUMN bar_data INTEGER NOT NULL DEFAULT 1");
        }
    };

    /**
     * 数据库从 4 升级到 5
     * 升级数据库修改数据库字段
     */
    static final Migration MIGRATION_3_4 = new Migration(3,4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE word_temp (id INTEGER PRIMARY KEY NOT NULL , english_word TEXT," +
                    "chinese_meaning TEXT)");
            database.execSQL("INSERT INTO word_temp (id,english_word,chinese_meaning) " +
                    "SELECT id,english_word,chinese_meaning FROM word");
            database.execSQL("DROP TABLE word");
            database.execSQL("ALTER TABLE word_temp RENAME to word");
        }
    };
}
