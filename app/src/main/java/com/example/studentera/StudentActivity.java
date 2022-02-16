package com.example.studentera;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class StudentActivity extends AppCompatActivity {

    Student mStudent;
    ArrayList<Subject> studentSubjects;
    SubjectAdapter subjectAdapter;

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

                return true;
            }
            case R.id.miChange:{

                return true;
            }
            case R.id.miDelete:{
                return true;
            }
            case R.id.miExit:{
                openQuitDialog();
                return true;
            }
            default:{}
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_info);

        if (getIntent().hasExtra("Student")) {
            mStudent = getIntent().getParcelableExtra("Student");
            ((EditText) findViewById(R.id.student_fio)).setText(mStudent.getFIO());
            ((EditText) findViewById(R.id.student_faculty)).setText(mStudent.getFaculty());
            ((EditText) findViewById(R.id.student_group)).setText(mStudent.getGroup());
        } else {
            mStudent = new Student();
        }

        studentSubjects = mStudent.getmSubjects();
        subjectAdapter = new SubjectAdapter(this, studentSubjects);
        ListView subjectsList = findViewById(R.id.subjectList);
        subjectsList.setAdapter(subjectAdapter);

        AdapterView.OnItemClickListener clSubject = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(
                        getApplicationContext(),
                        studentSubjects.get(i).toString(),
                        Toast.LENGTH_SHORT
                ).show();
                            }
        };
        subjectsList.setOnItemClickListener(clSubject);

    }

    public void addSubject(View view) {
//        studentSubjects.add(new Subject(
//                ((EditText) findViewById(R.id.edit_subject_name)).getText().toString(),
//                ((EditText) findViewById(R.id.edit_subject_mark)).getText().toString()
//        ));
//        subjectAdapter.notifyDataSetChanged();
//
//        ((EditText) findViewById(R.id.edit_subject_name)).setText("");
//        ((EditText) findViewById(R.id.edit_subject_mark)).setText("");

        AlertDialog.Builder inputDialog = new AlertDialog.Builder(StudentActivity.this);
        inputDialog.setTitle("Информация о дисциплине");
        inputDialog.setCancelable(false);
        View vv = (LinearLayout) getLayoutInflater().inflate(R.layout.subject_element, null);
        inputDialog.setView(vv);
        final EditText mName = vv.findViewById(R.id.subject_name);
        final Spinner mMark = vv.findViewById(R.id.subject_mark);

        inputDialog.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mStudent.getmSubjects().add(new Subject(
                        mName.getText().toString(),
                        mMark.getSelectedItem().toString()
                ));
                subjectAdapter.notifyDataSetChanged();
            }
        }).setNegativeButton("Отмена", null);
        inputDialog.show();
    }

    public void clSave(View view) {

        mStudent.setFIO(((EditText) findViewById(R.id.student_fio)).getText().toString());
        mStudent.setFaculty(((EditText) findViewById(R.id.student_faculty)).getText().toString());
        mStudent.setGroup(((EditText) findViewById(R.id.student_group)).getText().toString());
        Intent intent = new Intent();
        intent.putExtra("Student", mStudent);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void clCancel(View view) {
        finish();
    }

    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(this);
        quitDialog.setTitle("Сохранить изменения?");
        quitDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                clSave(null);
            }
        });

        quitDialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                clCancel(null);
            }
        });

        quitDialog.setNeutralButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {}
        });

        quitDialog.show();
    }

    @Override
    public void onBackPressed() {
        openQuitDialog();
    }
}