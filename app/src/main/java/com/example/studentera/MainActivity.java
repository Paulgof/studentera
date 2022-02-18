package com.example.studentera;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<Student> studentsList;
    StudentAdapter studentAdapter;
    private int mPosition = -1;
    private View mPositionView;
    private ActivityResultLauncher<Intent> mActivityResultLauncher;

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    final int DATA_LOADED = 1010;
    Handler dataLoadHandler;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.miAdd:{
                addStudent();
                return true;
            }
            case R.id.miChange:{
                changeStudent();
                return true;
            }
            case R.id.miDelete:{
                deleteStudent();
                return true;
            }
            case R.id.miExit:{
                finish();
                return true;
            }
            default:{}
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();

        mActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        assert intent != null;
                        Student s = intent.getParcelableExtra("Student");
                        if (mPosition < studentsList.size()) {
                            studentsList.set(mPosition, s);
                            mPosition = -1;
                        } else
                            studentsList.add(s);

                        studentAdapter.notifyDataSetChanged();
                        markSelected();
                    }

                }
        );

        dataLoadHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == DATA_LOADED) {
                    studentListCreate();
                }
            }
        };

        new Thread(() -> {
            loadData();
            dataLoadHandler.sendEmptyMessage(DATA_LOADED);
        }).start();

    }

    public void studentListCreate() {
        ListView listView = findViewById(R.id.studentList);
        studentAdapter = new StudentAdapter(this, studentsList);

        listView.setAdapter(studentAdapter);

        AdapterView.OnItemClickListener clStudent = (adapterView, view, i, l) -> selectStudent(i, view);
        listView.setOnItemClickListener(clStudent);

    }

    public void selectStudent(int selectPosition, View selectView) {
        if (mPosition == selectPosition) {
            mPosition = -1;
            mPositionView.setBackgroundResource(R.color.white);
            return;
        }
        if (mPosition != -1 && mPositionView != null) {
            mPositionView.setBackgroundResource(R.color.white);
        }
        selectView.setBackgroundResource(R.color.selected);
        mPosition = selectPosition;
        mPositionView = selectView;
    }

    public void markSelected() {
        if (mPosition != -1 && mPositionView != null) {
            mPositionView.setBackgroundResource(R.color.selected);
        }
    }

    public void addStudent() {
        mPosition = studentsList.size();
        Intent intent = new Intent(MainActivity.this, StudentActivity.class);
        mActivityResultLauncher.launch(intent);
    }

    public void changeStudent() {
        if (mPosition != -1 && mPositionView != null) {
            Intent intent = new Intent(MainActivity.this, StudentActivity.class);
            intent.putExtra("Student", studentsList.get(mPosition));
            mActivityResultLauncher.launch(intent);
        }
    }

    public void deleteStudent() {
        if (mPosition != -1 && mPositionView != null) {
            Student removedStudent = studentsList.remove(mPosition);
            mPosition = -1;
            studentAdapter.notifyDataSetChanged();
            markSelected();
            if (removedStudent != null && removedStudent.getId() != -1)
                new Thread(() -> db.delete(
                        DBHelper.TABLE_STUDENT,
                        DBHelper.STUDENT_ID + " = ?",
                        new String[] {Integer.toString(removedStudent.getId())}))
                        .start();
        }
    }

    public void loadData() {
        if (studentsList == null)
            studentsList = new ArrayList<>();

        Cursor curStudents = db.query(DBHelper.TABLE_STUDENT, null, null,
                null, null, null, null);
        if (curStudents.moveToFirst()) {
            int studentIdIndex = curStudents.getColumnIndex(DBHelper.STUDENT_ID);
            int studentFIOIndex = curStudents.getColumnIndex(DBHelper.STUDENT_FIO);
            int studentFacultyIndex = curStudents.getColumnIndex(DBHelper.STUDENT_FACULTY);
            int studentGroupIndex = curStudents.getColumnIndex(DBHelper.STUDENT_GROUP);
            do {
                studentsList.add(new Student(
                        curStudents.getInt(studentIdIndex),
                        curStudents.getString(studentFIOIndex),
                        curStudents.getString(studentFacultyIndex),
                        curStudents.getString(studentGroupIndex)
                ));
            } while (curStudents.moveToNext());
        }
        curStudents.close();
    }

    public void flushData() {
        if (studentsList != null) {
            ContentValues studentValues = new ContentValues();
            for (Student student: studentsList) {
                studentValues.put(DBHelper.STUDENT_FIO, student.getFIO());
                studentValues.put(DBHelper.STUDENT_FACULTY, student.getFaculty());
                studentValues.put(DBHelper.STUDENT_GROUP, student.getGroup());
                long studentId = -1;
                if (student.getId() != -1) {
                    int affected = db.update(
                        DBHelper.TABLE_STUDENT,
                        studentValues,
                        DBHelper.STUDENT_ID + " = ?",
                        new String[] {Integer.toString(student.getId())});

                    if (affected > 0) studentId = student.getId();
                }
                if (studentId == -1) {
                    studentId = db.insert(DBHelper.TABLE_STUDENT, null, studentValues);
                }

                if (studentId != -1 && !student.isSubjectsEmpty()) {
                    ContentValues subjectValues = new ContentValues();
                    for (Subject subject: student.getmSubjects()) {
                        subjectValues.put(DBHelper.MARK_SUBJECT, subject.getmName());
                        subjectValues.put(DBHelper.MARK_VALUE, subject.getmMark());
                        subjectValues.put(DBHelper.MARK_STUDENT, studentId);
                        db.replace(DBHelper.TABLE_MARK, null, subjectValues);
                        subjectValues.clear();
                    }
                }

                studentValues.clear();
            }
        }
    }

    @Override
    protected void onDestroy() {
        flushData();
        super.onDestroy();
    }
}