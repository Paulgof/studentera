package com.example.studentera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class StudentAdapter extends BaseAdapter {
    ArrayList<Student> students;
    Context ctx;
    LayoutInflater lInflater;
    Integer mSelected = null;

    View.OnLongClickListener fioLongClickListener, facultyLongClickListener, groupLongClickListener;

    public void setSelectedPosition(Integer position) {
        if (isEqualSelected(position)) mSelected = null;
        else mSelected = position;
    }

    StudentAdapter(Context context, ArrayList<Student> students) {
        ctx = context;
        this.students = students;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return this.students.size();
    }

    @Override
    public Object getItem(int i) {
        return this.students.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public Integer getmSelected() {
        return mSelected;
    }

    public void setmSelected(Integer mSelected) {
        this.mSelected = mSelected;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        @SuppressLint("ViewHolder")
        View new_view = lInflater.inflate(R.layout.student_element, viewGroup, false);
        if (students.isEmpty()) return new_view;

        TextView textViewFIO = ((TextView) new_view.findViewById(R.id.twFIO));
        textViewFIO.setText(students.get(i).getFIO());
        textViewFIO.setOnLongClickListener(fioLongClickListener);
        textViewFIO.setSelected(true);
        TextView textViewFaculty = ((TextView) new_view.findViewById(R.id.twFaculty));
        textViewFaculty.setText(students.get(i).getFaculty());
        textViewFaculty.setOnLongClickListener(facultyLongClickListener);
        TextView textViewGroup = ((TextView) new_view.findViewById(R.id.twGroup));
        textViewGroup.setText(students.get(i).getGroup());
        textViewGroup.setOnLongClickListener(groupLongClickListener);

        return new_view;
    }

    boolean isEqualSelected(int position) {
        return mSelected != null &&
                students.get(position).getFaculty().equals(students.get(mSelected).getFaculty()) &&
                students.get(position).getGroup().equals(students.get(mSelected).getGroup());
    }

    public void setFioLongClickListener(View.OnLongClickListener fioLongClickListener) {
        this.fioLongClickListener = fioLongClickListener;
    }

    public void setFacultyLongClickListener(View.OnLongClickListener facultyLongClickListener) {
        this.facultyLongClickListener = facultyLongClickListener;
    }

    public void setGroupLongClickListener(View.OnLongClickListener groupLongClickListener) {
        this.groupLongClickListener = groupLongClickListener;
    }
}
