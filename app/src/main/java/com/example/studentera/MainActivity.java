package com.example.studentera;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<Student> studentsList;
    StudentAdapter studentAdapter;
    private int mPosition = -1;
    private View mPositionView;
    private ActivityResultLauncher<Intent> mActivityResultLauncher;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    AlertDialog.Builder infoDialog;

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
        studentListCreate(null);

    }

    public void studentListCreate(View view) {
        ListView listView = findViewById(R.id.studentList);


        if (studentsList == null) {
            studentsList = new ArrayList<>();
            studentsList.add(new Student("Федоров Федор Федорович", "ФКТиПМ", "15/1"));
            studentsList.add(new Student("Петров Петр Петрович", "ФКТиПМ", "16/1"));
            studentsList.add(new Student("Иванов Иван Иванович", "ФМиФ", "15/1"));
            studentsList.add(new Student("А Б В", "ФМиФ", "16/1"));
            studentsList.add(new Student("Амбисарабил Дон Корнеолле Фон Рейман", "ФМиФ", "26/2"));
        }

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


    @Override
    protected void onDestroy() {
        if (studentsList != null) {
            SharedPreferences.Editor ed = getPreferences(MODE_PRIVATE).edit();
//            GsonBuilder builder = new GsonBuilder();
//            Gson gson = builder.create();
//            ed.putInt("count", studentsList.size());
//            for (int i = 0; i < studentsList.size(); ++i) {
//                String s = gson.toJson(studentsList.get(i));
//                ed.putString("student" + i, s);
//            }
//            ed.commit();
        }
        super.onDestroy();
    }
}