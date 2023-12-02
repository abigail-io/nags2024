package com.example.donna_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
public class StudentRecordAdapter extends ArrayAdapter<StudentRecord> {

    public StudentRecordAdapter(Context context, List<StudentRecord> records) {
        super(context, 0, records);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        StudentRecord record = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_layout, parent, false);
        }

        TextView dateRecordedTextView = convertView.findViewById(R.id.dateRecordedTextView);
        TextView remarksTextView = convertView.findViewById(R.id.remarksTextView);
        TextView statusTextView = convertView.findViewById(R.id.statusTextView);
//        TextView studentIdTextView = convertView.findViewById(R.id.studentIdTextView);
//        TextView violationIdTextView = convertView.findViewById(R.id.violationIdTextView);
//        TextView guidanceIdTextView = convertView.findViewById(R.id.guidanceIdTextView);
        TextView violationNameTextView = convertView.findViewById(R.id.violationNameTextView);
        TextView guidanceNameTextView = convertView.findViewById(R.id.guidanceNameTextView);
        TextView studentNameTextView = convertView.findViewById(R.id.studentNameTextView);

        if (record != null) {
            // Set data to TextViews
            dateRecordedTextView.setText(record.getDateRecorded());
            remarksTextView.setText(record.getRemarks());
            statusTextView.setText(record.getStatus());
//            studentIdTextView.setText(String.valueOf(record.getStudentId()));
//            violationIdTextView.setText(String.valueOf(record.getViolationId()));
//            guidanceIdTextView.setText(String.valueOf(record.getGuidanceId()));
            violationNameTextView.setText(record.getViolationName());
            guidanceNameTextView.setText(record.getGuidanceName());
            studentNameTextView.setText(record.getStudentName());

        }

        return convertView;
    }
}
