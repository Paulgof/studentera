package com.example.studentera;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "StudentEraDB";

    //Students table
    public static final String TABLE_STUDENT = "STUDENT";
    public static final String STUDENT_ID = "id";
    public static final String STUDENT_FIO = "fio";
    public static final String STUDENT_FACULTY = "student_faculty";
    public static final String STUDENT_GROUP = "student_group";

    //Marks table
    public static final String TABLE_MARK = "MARK";
    public static final String MARK_ID = "id";
    public static final String MARK_SUBJECT = "subject";
    public static final String MARK_STUDENT = "student_id";
    public static final String MARK_VALUE = "mark";

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private void createAllTables(SQLiteDatabase sqlDB) {
        //Create STUDENT table
        sqlDB.execSQL(String.format(
                "CREATE TABLE %s (" +
                        "%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        "%s TEXT NOT NULL," +
                        "%s TEXT," +
                        "%s TEXT," +
                        "CONSTRAINT student_faculty_unique UNIQUE (%s, %s))",
                TABLE_STUDENT,
                STUDENT_ID,
                STUDENT_FIO,
                STUDENT_FACULTY,
                STUDENT_GROUP,
                STUDENT_FIO, STUDENT_FACULTY
        ));

        //Create MARK table
        sqlDB.execSQL(String.format(
                "CREATE TABLE %s (" +
                        "%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        "%s TEXT," +
                        "%s TEXT," +
                        "%s INTEGER," +
                        "FOREIGN KEY(%s) REFERENCES %s(%s)," +
                        "CONSTRAINT student_subject_unique UNIQUE (%s, %s))",
                TABLE_MARK,
                MARK_ID,
                MARK_VALUE,
                MARK_SUBJECT,
                MARK_STUDENT,
                MARK_STUDENT, TABLE_STUDENT, STUDENT_ID,
                MARK_STUDENT, MARK_SUBJECT
        ));
    }

    private void dropOldV1Tables(SQLiteDatabase sqlDB) {
        sqlDB.execSQL("DROP TABLE STUDENT");
        sqlDB.execSQL("DROP TABLE MARK");
        sqlDB.execSQL("DROP TABLE SUBJECT");
    }

    private void dropOldV2Tables(SQLiteDatabase sqlDB) {
        sqlDB.execSQL("DROP TABLE STUDENT");
        sqlDB.execSQL("DROP TABLE MARK");
    }

    @Override
    public void onCreate(SQLiteDatabase sqlDB) {
        createAllTables(sqlDB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if (i == 1) dropOldV1Tables(sqLiteDatabase);
        if (i >= 2) dropOldV2Tables(sqLiteDatabase);
        createAllTables(sqLiteDatabase);
    }
}
