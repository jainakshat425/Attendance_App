package com.example.android.attendance.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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

    private static List<Attendance> attendanceList;
    private Context mContext;

    public TakeAttendAdapter(Context mContext, List<Attendance> attendanceList) {
        this.mContext = mContext;
        attendanceList = attendanceList;

    }

    public static Attendance[] getAttendanceList() {
        return attendanceList.toArray(new Attendance[0]);
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

        Attendance attendance = attendanceList.get(position);

        String name = attendance.getStdName();
        String rollNo = attendance.getStdRollNo();
        int attendState = attendance.getAttendanceState();

        int serialNo = position;

        holder.nameTv.setText(name);
        holder.rollNoTv.setText(rollNo);
        holder.serialTv.setText(String.valueOf(serialNo + 1));
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
                int attendState = attendanceList.get(position).getAttendanceState();
                if (attendState == 1) {
                    attendanceList.get(position).setAttendanceState(0);
                } else {
                    attendanceList.get(position).setAttendanceState(1);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        if (attendanceList == null)
            return 0;
        else
            return attendanceList.size();
    }

    public void swapList(List<Attendance> records) {
        attendanceList = records;
        this.notifyDataSetChanged();
    }


    public class TakeAttendViewHolder extends RecyclerView.ViewHolder {

        TextView nameTv;
        TextView rollNoTv;
        TextView serialTv;
        CheckBox presentCheckbox;

        public TakeAttendViewHolder(View view) {
            super(view);

            nameTv = view.findViewById(R.id.tv_name);
            rollNoTv = view.findViewById(R.id.tv_roll_no);
            serialTv = view.findViewById(R.id.tv_serial_no);
            presentCheckbox = view.findViewById(R.id.present_checkbox);
        }
    }


}
