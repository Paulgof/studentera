package com.example.studentera;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent intent = result.getData();
                            Student s = intent.getParcelableExtra("Student");
                            if (mPosition < studentsList.size()) {
                                studentsList.set(mPosition, s);
                                mPosition = -1;
                            } else
                                studentsList.add(s);

                            Toast.makeText(
                                    getApplicationContext(),
                                    "Student " + s.toString() + " successfully saved",
                                    Toast.LENGTH_SHORT
                            ).show();
                            studentAdapter.notifyDataSetChanged();
                            markSelected();
                        }

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

        new Thread(new Runnable() {
            @Override
            public void run() {
                loadData();
                dataLoadHandler.sendEmptyMessage(DATA_LOADED);
            }
        }).start();

    }

    public void studentListCreate() {
        ListView listView = findViewById(R.id.studentList);
        studentAdapter = new StudentAdapter(this, studentsList);

        listView.setAdapter(studentAdapter);

        AdapterView.OnItemClickListener clStudent = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectStudent(i, view);
            }
        };
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
            studentsList.remove(mPosition);
            mPosition = -1;
            studentAdapter.notifyDataSetChanged();
            markSelected();
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
                long studentId = db.replace(DBHelper.TABLE_STUDENT, null, studentValues);
                if (studentId != -1 && !student.isSubjectsEmpty()) {
                    ContentValues subjectValues = new ContentValues();
                    for (Subject subject: student.getmSubjects()) {
                        subjectValues.put(DBHelper.MARK_SUBJECT, subject.getmName());
                        subjectValues.put(DBHelper.MARK_VALUE, subject.getmMark());
                        subjectValues.put(DBHelper.MARK_STUDENT, studentId);
                        db.replace(DBHelper.TABLE_MARK, null, subjectValues);
                        Log.i("MA", "save subject");
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