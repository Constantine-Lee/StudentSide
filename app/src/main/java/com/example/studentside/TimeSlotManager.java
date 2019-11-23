package com.example.studentside;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class TimeSlotManager {

    private List<TimeSlot> mTimeSlots;
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();

    private List<String> keyList = new ArrayList<String>();
    private Context mContext;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

    private String userIdentityNo = firebaseUser.getEmail().substring(0, firebaseUser.getEmail().indexOf("@"));
    private String mPath;
    private UpdateInfo mUpdateInfo;

    public interface UpdateInfo{
        void UpdateDate(String date);
        void NotifyDataInserted(int position);
        void NotifyDataRemoved(int position);
    }

    public TimeSlotManager(Context context, String path) {
        mPath = path;

        mContext = context.getApplicationContext();
        if(context instanceof UpdateInfo){
            mUpdateInfo = (UpdateInfo) context;
        }

        userIdentityNo = firebaseUser.getEmail();
        mTimeSlots = new ArrayList<>();

        mFirebaseDatabase.getReference("units").child(mPath).child("timeslots").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                mTimeSlots.add((TimeSlot) dataSnapshot.getValue(TimeSlot.class));
                keyList.add(dataSnapshot.getKey());
                int position = keyList.indexOf(dataSnapshot.getKey());
                TimeSlotsInterface.NotifyItemInserted(position);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                int position = keyList.indexOf(dataSnapshot.getKey());
                mTimeSlots.set(position, (TimeSlot) dataSnapshot.getValue(TimeSlot.class));
                TimeSlotsInterface.NotifyAdapterDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                int position = keyList.indexOf(dataSnapshot.getKey());
                mTimeSlots.remove(position);
                keyList.remove(dataSnapshot.getKey());
                TimeSlotsInterface.NotifyItemRemoved(position);

                String email = dataSnapshot.getValue(TimeSlot.class).getName();
                email = email.substring(0, email.indexOf("@"));

                if(email.equals(userIdentityNo)) {
                    Intent intent2 = new Intent("MY_ACTION");
                    intent2.putExtra("data", "Hello World!");
                    mContext.sendBroadcast(intent2);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    public void updateTimeSlot(TimeSlot c, int position){
        mFirebaseDatabase.getReference("units").child(mPath).child("timeslots").child(keyList.get(position)).setValue(c);
    }

    public void removeName(TimeSlot c, int position){
        c.setName("");
        mFirebaseDatabase.getReference("units").child(mPath).child("timeslots").child(keyList.get(position)).setValue(c);

    }

    public List<TimeSlot> getTimeSlots() {
        return mTimeSlots;
    }
}

