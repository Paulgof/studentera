package com.example.studentera;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class SubjectAdapter extends BaseAdapter {
    ArrayList<Subject> subjects = new ArrayList<>();
    Context ctx;
    LayoutInflater lInflater;

    SubjectAdapter(Context context, ArrayList<Subject> subjects) {
        ctx = context;
        this.subjects = subjects;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return this.subjects.size();
    }

    @Override
    public Object getItem(int i) {
        return this.subjects.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View new_view = lInflater.inflate(R.layout.subject_element, viewGroup, false);
        if (subjects.isEmpty()) return new_view;

        ((TextView) new_view.findViewById(R.id.subject_name)).setText(subjects.get(i).getmName());
        ((TextView) new_view.findViewById(R.id.subject_mark)).setText(subjects.get(i).getmMark());


        return new_view;
    }

}
