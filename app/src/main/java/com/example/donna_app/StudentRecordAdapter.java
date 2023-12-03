package com.example.donna_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class StudentRecordAdapter extends ArrayAdapter<StudentRecord> implements Filterable {

    private List<StudentRecord> originalData;
    private List<StudentRecord> filteredData;

    public StudentRecordAdapter(Context context, List<StudentRecord> records) {
        super(context, 0, records);
        this.originalData = new ArrayList<>(records);
        this.filteredData = new ArrayList<>(records);
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
        TextView violationNameTextView = convertView.findViewById(R.id.violationNameTextView);
        TextView guidanceNameTextView = convertView.findViewById(R.id.guidanceNameTextView);
        TextView studentNameTextView = convertView.findViewById(R.id.studentNameTextView);

        if (record != null) {
            dateRecordedTextView.setText(record.getDateRecorded());
            remarksTextView.setText(record.getRemarks());
            statusTextView.setText(record.getStatus());
            violationNameTextView.setText(record.getViolationName());
            guidanceNameTextView.setText(record.getGuidanceName());
            studentNameTextView.setText(record.getStudentName());
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Nullable
    @Override
    public StudentRecord getItem(int position) {
        return filteredData.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<StudentRecord> filteredList = new ArrayList<>();

                if (originalData == null) {
                    originalData = new ArrayList<>(filteredData);
                }

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(originalData);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (StudentRecord record : originalData) {
                        if (record.getStudentName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(record);
                        }
                    }
                }

                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredData = (List<StudentRecord>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
