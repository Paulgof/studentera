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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class StudentActivity extends AppCompatActivity {

    Student mStudent;
    ArrayList<Subject> studentSubjects;
    SubjectAdapter subjectAdapter;

    private boolean areSubjectsChanged = false;
    private int mPosition = -1;
    private View mPositionView;
    private ArrayList<String> allSubjectsList;
    private ArrayList<String> allMarksList;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.miAdd:{
                addSubject();
                return true;
            }
            case R.id.miChange:{
                changeSubject();
                return true;
            }
            case R.id.miDelete:{
                deleteSubject();
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

        allSubjectsList = new ArrayList<>(
                Arrays.asList(getResources().getStringArray(R.array.subjects_names))
        );
        allMarksList = new ArrayList<>(
                Arrays.asList(getResources().getStringArray(R.array.marks_array))
        );

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
                selectSubject(i, view);
            }
        };
        subjectsList.setOnItemClickListener(clSubject);

    }

    public void selectSubject(int selectPosition, View selectView) {
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

    public void addSubject() {
        AlertDialog.Builder inputDialog = new AlertDialog.Builder(StudentActivity.this);
        inputDialog.setTitle("Добавить предмет");
        inputDialog.setCancelable(false);
        View vv = (LinearLayout) getLayoutInflater().inflate(R.layout.subject_form, null);
        inputDialog.setView(vv);
        final Spinner mName = vv.findViewById(R.id.subjects_dropdown);
        final Spinner mMark = vv.findViewById(R.id.marks_dropdown);

        inputDialog.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mStudent.getmSubjects().add(new Subject(
                        mName.getSelectedItem().toString(),
                        mMark.getSelectedItem().toString()
                ));
                subjectAdapter.notifyDataSetChanged();
                mPosition = -1;
                areSubjectsChanged = true;
            }
        }).setNegativeButton("Отмена", null);
        inputDialog.show();
    }

    public void changeSubject() {
        if (mPosition == -1 || mPositionView == null)
            return;

        Subject selectedSubject = studentSubjects.get(mPosition);


        AlertDialog.Builder inputDialog = new AlertDialog.Builder(StudentActivity.this);
        inputDialog.setTitle("Изменить оценку");
        inputDialog.setCancelable(false);
        View vv = (LinearLayout) getLayoutInflater().inflate(R.layout.subject_form, null);
        inputDialog.setView(vv);
        final Spinner mName = vv.findViewById(R.id.subjects_dropdown);
        mName.setSelection(allSubjectsList.indexOf(selectedSubject.getmName()));
        mName.setEnabled(false);
        mName.setClickable(false);
        final Spinner mMark = vv.findViewById(R.id.marks_dropdown);
        mMark.setSelection(allMarksList.indexOf(selectedSubject.getmMark()));


        inputDialog.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selectedSubject.setmMark(mMark.getSelectedItem().toString());
                subjectAdapter.notifyDataSetChanged();
                mPosition = -1;
                areSubjectsChanged = true;
            }
        }).setNegativeButton("Отмена", null);
        inputDialog.show();
    }

    public void deleteSubject() {
        if (mPosition == -1 || mPositionView == null)
            return;

        studentSubjects.remove(mPosition);
        subjectAdapter.notifyDataSetChanged();
        mPosition = -1;
        areSubjectsChanged = true;
    }

    public boolean isStudentInfoChanged() {
        String fio = ((EditText) findViewById(R.id.student_fio)).getText().toString();
        String faculty = ((EditText) findViewById(R.id.student_faculty)).getText().toString();
        String group = ((EditText) findViewById(R.id.student_group)).getText().toString();
        return !(mStudent.getFIO().equals(fio)
                && mStudent.getFaculty().equals(faculty)
                && mStudent.getGroup().equals(group));
    }

    public boolean checkFieldsFullFit() {
        boolean checkStatus = true;

        if (mStudent.isFioEmpty()) {
            checkStatus = false;
            ((EditText) findViewById(R.id.student_fio)).setError("ФИО не может быть пустым");
        }
        if (mStudent.isFacultyEmpty()) {
            checkStatus = false;
            ((EditText) findViewById(R.id.student_faculty)).setError("Нужно указать факультет");
        }
        if (mStudent.isGroupEmpty()) {
            checkStatus = false;
            ((EditText) findViewById(R.id.student_group)).setError("Нужно указать группу");
        }

        return checkStatus;
    }

    public void clSave() {

        mStudent.setFIO(((EditText) findViewById(R.id.student_fio)).getText().toString());
        mStudent.setFaculty(((EditText) findViewById(R.id.student_faculty)).getText().toString());
        mStudent.setGroup(((EditText) findViewById(R.id.student_group)).getText().toString());
        if (checkFieldsFullFit()) {
            Intent intent = new Intent();
            intent.putExtra("Student", mStudent);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    public void clCancel() {
        finish();
    }

    private void openQuitDialog() {
        if (!areSubjectsChanged && !isStudentInfoChanged()) {
            clCancel();
            return;
        }

        AlertDialog.Builder quitDialog = new AlertDialog.Builder(this);
        quitDialog.setTitle("Сохранить изменения?");
        quitDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                clSave();
            }
        });

        quitDialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                clCancel();
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