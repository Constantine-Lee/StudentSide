package com.example.studentside;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;


import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class TimeSlotsInterface extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String UNIT_CODE = "UNIT_CODE";

    private SharedPreferences sharedPreferences;
    private RecyclerView mTimeSlotRecyclerView;
    private static TimeSlotAdapter mTimeSlotAdapter ;
    private TimeSlotManager mTimeSlotManager;
    private List<TimeSlot> mListOfTimeSlot;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private TextView mTextView_date;
    private BroadcastReceiver myBroadcastReceiver = new MyBroadcastReceiver();
    private static final int START_NEW_SESSION_SCREEN = 0;

    public static Intent newIntent(Context packageContext, String unitCode){
        Intent intent = new Intent (packageContext, TimeSlotsInterface.class);
        intent.putExtra(UNIT_CODE, unitCode);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitUI();
        LoadResource();
        updateUI();
        SetListener();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        final IntentFilter intentFilter = new IntentFilter("MY_ACTION");
        this.registerReceiver(myBroadcastReceiver, intentFilter);
    }

    private void InitUI(){
        setContentView(R.layout.main);

        mTextView_date = (TextView) findViewById(R.id.MAIN_TEXTVIEW_DATE);
        mTimeSlotRecyclerView = (RecyclerView) findViewById(R.id.RECYCLERVIEW_TIMESLOTS);
        mTimeSlotRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void LoadResource(){
        mTimeSlotManager = new TimeSlotManager(this, mUnitCode);
        mListOfTimeSlot = mTimeSlotManager.getTimeSlots();

        setupSharedPreferences();
        loadSizeFromPreference(sharedPreferences);

        mTimeSlotAdapter = new TimeSlotAdapter(mListOfTimeSlot);
        mTimeSlotRecyclerView.setAdapter(mTimeSlotAdapter);
    }

    private void SetListener(){
        mFirebaseDatabase.getReference("date").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                mTextView_date.setText(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public class ClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {

        }
    }

    private void updateUI(){
        mListOfTimeSlot = mTimeSlotManager.getTimeSlots();
        if(mTimeSlotAdapter == null){
            mTimeSlotAdapter = new TimeSlotAdapter(mListOfTimeSlot);
            mTimeSlotRecyclerView.setAdapter(mTimeSlotAdapter);
        }else{
            mTimeSlotAdapter.setListOfTimeSlot(mListOfTimeSlot);
            mTimeSlotAdapter.notifyDataSetChanged();
        }
    }

    private class TimeSlotHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TimeSlot mTimeSlot;
        private TextView mTime, mName;

        public TimeSlotHolder(View view){
            super(view);

            mTime = (TextView) view.findViewById(R.id.RECYCLERVIEW_TEXTVIEW_TIME);
            mName = (TextView) view.findViewById(R.id.RECYCLERVIEW_TEXTVIEW_NAME);
            //mStatus = (TextView) view.findViewById(R.id.RECYCLERVIEW_TEXTVIEW_STATUS);
            itemView.setOnClickListener(this);
        }

        private void bind(TimeSlot timeSlot){
            mTimeSlot = timeSlot;
            if(mTimeSlot!=null) {
                mTime.setText(mTimeSlot.getTime());
                mName.setText(mTimeSlot.getName());
                //mStatus.setText(mTimeSlot.getBook()? mTimeSlot.getDone() ? "Done" : "Waiting":"");
            }
        }

        @Override
        public void onClick(View view) {
            Boolean didUserBookOneSlot = false;
            int position = (int) view.getTag();
            String currentSlotOccupiedName = mTimeSlotManager.getTimeSlots().get(position).getName();
            String userEmail = firebaseUser.getEmail();
            TimeSlot timeSlot = mTimeSlotManager.getTimeSlots().get(position);

            //if the slot selected is the slot occupied by the user; then remove it.
            if(currentSlotOccupiedName.equals(userEmail)){
                timeSlot.setBook(false);
                mTimeSlotManager.removeName(mTimeSlot, position);
                mTimeSlotAdapter.notifyDataSetChanged();
            }

            //if current slot is not occupied
            else if(currentSlotOccupiedName.equals("")) {
                //loop through time slots in Time Slot Manager
                for(TimeSlot tS: mTimeSlotManager.getTimeSlots()){
                    //if the user email is one of the time slot in Time Slot Manager; Set the user had booked one slot.
                    if(tS.getName().equals(userEmail)){
                        didUserBookOneSlot = true;
                    }
                }
                //if the user had not book any slot
                if(didUserBookOneSlot == false) {

                    timeSlot.setBook(true);
                    timeSlot.setName(userEmail);
                    mTimeSlotManager.updateTimeSlot(mTimeSlot, position);
                    mTimeSlotAdapter.notifyDataSetChanged();
                    Toast.makeText(TimeSlotsInterface.this, "Successfully booked this time slot.Please be punctual.",
                            Toast.LENGTH_SHORT).show();
                }
                //Announce only slot available for one person
                else{
                    Toast.makeText(TimeSlotsInterface.this, "One user can only book one slot per session",
                            Toast.LENGTH_SHORT).show();
                }
            }
            else Toast.makeText(TimeSlotsInterface.this, "Slot Occupied, Please choose another time slot.", Toast.LENGTH_SHORT).show();
        }
    }
    public static void NotifyAdapterDataSetChanged(){
        mTimeSlotAdapter.notifyDataSetChanged();
    }

    public static void NotifyItemRemoved(int position){
        mTimeSlotAdapter.notifyItemRemoved(position);
    }

    public static void NotifyItemInserted(int position){
        mTimeSlotAdapter.notifyItemInserted(position);
    }
    private class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotHolder>{
        public List<TimeSlot> mListOfTimeSlot;

        public TimeSlotAdapter(List<TimeSlot> ListOfTimeSlot){
            mListOfTimeSlot = ListOfTimeSlot;
        }

        @Override
        public TimeSlotHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.time_slot_item_material_design, parent, false);
            TimeSlotHolder viewHolder = new TimeSlotHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(TimeSlotHolder holder, int position) {
            holder.bind(mListOfTimeSlot.get(position));
            holder.itemView.setTag(position);
        }

        public void setListOfTimeSlot(List<TimeSlot> listOfTimeSlot){
            mListOfTimeSlot = listOfTimeSlot;
        }

        @Override
        public int getItemCount() {
            return mListOfTimeSlot.size();
        }
    }

    private void setupSharedPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private void loadSizeFromPreference(SharedPreferences sharedPreferences) {
        //float minSize = Float.parseFloat(sharedPreferences.getString(getString(R.string.pref_size_key), "16.0"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(TimeSlotsInterface.this, SettingsActivity.class);
            startActivityForResult(intent, START_NEW_SESSION_SCREEN);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("size"))  {
            loadSizeFromPreference(sharedPreferences);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if(requestCode == START_NEW_SESSION_SCREEN){
            //GENERATE SLOT
        }


    }
}