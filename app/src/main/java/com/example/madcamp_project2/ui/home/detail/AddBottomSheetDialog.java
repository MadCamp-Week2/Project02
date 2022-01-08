package com.example.madcamp_project2.ui.home.detail;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.madcamp_project2.R;
import com.example.madcamp_project2.ui.Place;
import com.example.madcamp_project2.ui.Schedule;
import com.example.madcamp_project2.ui.TripPlan;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.DecimalFormat;
import java.util.Calendar;

import nl.joery.timerangepicker.TimeRangePicker;

public class AddBottomSheetDialog extends BottomSheetDialogFragment {

    TimePicker timePicker;
    TimeRangePicker timeRangePicker;
    TripPlan tripPlan;
    Button startTimeSelect, endTimeSelect, registerBtn;
    Context context;

    EditText moneyEditText, memoEditText, titleEditText, addressEditText;
    int day;

    public AddBottomSheetDialog(Context context, TripPlan tripPlan, int day) {
        this.context = context;
        this.tripPlan = tripPlan;
        this.day = day;
    }

    String getTimeString(int hour, int minute) {
        String text = "";

        if (hour < 10) text += "0" + hour;
        else text += hour + "";

        text += " : ";
        if (minute < 19) text += "0" + minute;
        else text += minute;

        return text;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_bottom_sheet_dialog_layout, container, false);

        Schedule schedule = new Schedule();

        startTimeSelect = view.findViewById(R.id.addStartTimeSelect);
        endTimeSelect = view.findViewById(R.id.addEndTimeSelect);
        titleEditText = view.findViewById(R.id.addTitleTextView);
        addressEditText = view.findViewById(R.id.addAddressTextView);
        moneyEditText = view.findViewById(R.id.addMoney);
        registerBtn = view.findViewById(R.id.addRegisterBtn);
        memoEditText = view.findViewById(R.id.addMemo);

        String timeString = getTimeString(schedule.getStart_time().getHour(), schedule.getStart_time().getMinute());
        startTimeSelect.setText(timeString);
        timeString = getTimeString(schedule.getEnd_time().getHour(), schedule.getEnd_time().getMinute());
        endTimeSelect.setText(timeString);

        DecimalFormat df = new DecimalFormat("###,###,####");
        moneyEditText.addTextChangedListener(new NumberTextWatcher(moneyEditText));

        startTimeSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mCalendar = Calendar.getInstance();
                int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
                int minute = mCalendar.get(Calendar.MINUTE);


                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        int hour = timePicker.getHour();
                        int minute = timePicker.getMinute();

                        int end_hour = schedule.getEnd_time().getHour();
                        int end_minute = schedule.getEnd_time().getMinute();

                        if (end_hour < hour || (end_hour == hour && end_minute < minute)) {
                            Toast.makeText(getContext(), "시작 시간이 종료 시간보다 늦습니다. 다시 선택해주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String timeString = getTimeString(hour, minute);
                        startTimeSelect.setText(timeString);
                        schedule.setStart_time(new Schedule.TimeFormat(hour, minute));
                    }
                }, hour, minute, DateFormat.is24HourFormat(getContext()));

                timePickerDialog.updateTime(schedule.getStart_time().getHour(), schedule.getStart_time().getMinute());
                timePickerDialog.show();
            }
        });

        endTimeSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mCalendar = Calendar.getInstance();
                int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
                int minute = mCalendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        int hour = timePicker.getHour();
                        int minute = timePicker.getMinute();

                        int start_hour = schedule.getStart_time().getHour();
                        int start_minute = schedule.getStart_time().getMinute();

                        if (start_hour > hour || (start_hour == hour && start_minute > minute)) {
                            Toast.makeText(getContext(), "종료 시간이 시작 시간보다 빠릅니다. 다시 선택해주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String timeString = getTimeString(hour, minute);
                        endTimeSelect.setText(timeString);
                        schedule.setEnd_time(new Schedule.TimeFormat(hour, minute));
                    }
                }, hour, minute, DateFormat.is24HourFormat(getContext()));

                timePickerDialog.updateTime(schedule.getEnd_time().getHour(), schedule.getEnd_time().getMinute());
                timePickerDialog.show();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (memoEditText.getText().toString().equals("")) {
                    memoEditText.setText("메모가 없습니다");
                }
                schedule.setMemo(memoEditText.getText().toString());
                String moneyText = moneyEditText.getText().toString();
                if (moneyText.equals("")) moneyText = "0";

                String title = titleEditText.getText().toString();
                String address = addressEditText.getText().toString();

                schedule.setMoney(moneyText);
                schedule.setPlace(new Place(title, address));

                // TODO DB에도 추가 필요
                tripPlan.getSchedule(day).add(schedule);

                ViewPagerAdapter.scheduleAdapters[day].setSchedules(tripPlan.getSchedule(day));
                ViewPagerAdapter.scheduleAdapters[day].notifyDataSetChanged();

                dismiss();
            }
        });

        return view;
    }
}