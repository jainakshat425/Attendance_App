package com.example.android.attendance.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.android.attendance.R;
import com.example.android.attendance.pojos.Attendance;

import java.util.List;

/**
 * Created by Akshat Jain on 28-Dec-18.
 */
public class TakeAttendAdapter extends RecyclerView.Adapter<TakeAttendAdapter.TakeAttendViewHolder> {

    private static List<Attendance> mAttendanceList;
    private Context mContext;

    public TakeAttendAdapter(Context mContext, List<Attendance> attendanceList) {
        this.mContext = mContext;
        mAttendanceList = attendanceList;

    }

    public static Attendance[] getmAttendanceList() {
        return mAttendanceList.toArray(new Attendance[0]);
    }

    @NonNull
    @Override
    public TakeAttendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.take_attendance_list_item,
                parent, false);
        return new TakeAttendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TakeAttendViewHolder holder, int position) {

        Attendance attendance = mAttendanceList.get(position);

        String name = attendance.getStdName();
        String rollNo = attendance.getStdRollNo();
        int attendState = attendance.getAttendanceState();

        holder.nameTv.setText(name);
        holder.rollNoTv.setText(rollNo);
        holder.serialTv.setText(String.valueOf(position + 1));
        if (attendState == 1) {
            holder.presentCheckbox.setChecked(true);
        } else {
            holder.presentCheckbox.setChecked(false);
        }

        //set tag on check box so as to identify which checkbox belongs to which view
        holder.presentCheckbox.setTag(position);

        holder.presentCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = Integer.parseInt(String.valueOf(v.getTag()));
                int attendState = mAttendanceList.get(position).getAttendanceState();
                if (attendState == 1) {
                    mAttendanceList.get(position).setAttendanceState(0);
                } else {
                    mAttendanceList.get(position).setAttendanceState(1);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        if (mAttendanceList == null)
            return 0;
        else
            return mAttendanceList.size();
    }

    public void swapList(List<Attendance> records) {
        mAttendanceList = records;
        this.notifyDataSetChanged();
    }


    public class TakeAttendViewHolder extends RecyclerView.ViewHolder {

        TextView nameTv;
        TextView rollNoTv;
        TextView serialTv;
        CheckBox presentCheckbox;

        public TakeAttendViewHolder(View view) {
            super(view);

            nameTv = view.findViewById(R.id.tv_name_rep);
            rollNoTv = view.findViewById(R.id.tv_roll_no_rep);
            serialTv = view.findViewById(R.id.tv_serial_no_rep);
            presentCheckbox = view.findViewById(R.id.present_checkbox);
        }
    }

    public void checkAll() {
        for (Attendance a : mAttendanceList) {
            a.setAttendanceState(1);
        }
        this.notifyDataSetChanged();
    }

    public void unCheckAll() {
        for (Attendance a : mAttendanceList) {
            a.setAttendanceState(0);
        }
        this.notifyDataSetChanged();
    }


}
