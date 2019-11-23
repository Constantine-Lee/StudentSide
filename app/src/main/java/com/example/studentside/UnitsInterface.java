package com.example.studentside;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class UnitsInterface extends AppCompatActivity {



    private RecyclerView mUnitsInterface_recyclerView;
    private UnitAdapter mUnitsAdapter;

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();

    private String userIdentityNo = mFirebaseUser.getEmail().substring(0, mFirebaseUser.getEmail().indexOf("@"));
    private List<Unit> mUnitList = new ArrayList<>();



    private void SetUpRecyclerView(){
        mUnitsInterface_recyclerView = (RecyclerView) findViewById(R.id.Recyclerview_unitSelection);
        mUnitsInterface_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUnitsAdapter = new UnitAdapter(mUnitList);
        mUnitsInterface_recyclerView.setAdapter(mUnitsAdapter);
    }

    private void SetUpFirebaseListener(){
        mFirebaseDatabase.getReference("students").child(userIdentityNo).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                mUnitList.add((Unit)dataSnapshot.getValue(Unit.class));
                mUnitsAdapter.notifyItemInserted(0);
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.units_interface);
        getSupportActionBar().setTitle("Unit Available");
        SetUpRecyclerView();
        SetUpFirebaseListener();
    }

    private class UnitHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private Unit mUnit;
        private TextView mTextView_unitCode, mTextView_unitName;

        public UnitHolder(View view){
            super(view);
            mTextView_unitCode = (TextView) view.findViewById(R.id.textView_unitCode);
            mTextView_unitName = (TextView) view.findViewById(R.id.textView_unitName);

            itemView.setOnClickListener(this);
        }

        private void bind(Unit unit){
            mUnit = unit;
            mTextView_unitCode.setText(mUnit.getUnitCode());
            mTextView_unitName.setText(mUnit.getUnitName());
        }

        @Override
        public void onClick(View view){
            Intent intent = TimeSlotsInterface.newIntent(UnitsInterface.this, mUnit.getUnitCode());
            startActivity(intent);
        }
    }

    private class UnitAdapter extends RecyclerView.Adapter<UnitHolder>{
        private List<Unit> mUnitList;

        public UnitAdapter(List<Unit> unitList){
            mUnitList = unitList;
        }

        @Override
        public UnitHolder onCreateViewHolder(ViewGroup parent, int viewType){
            View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.unit_item, parent, false);
            UnitHolder unitHolder = new UnitHolder(v);
            return unitHolder;
        }

        @Override
        public void onBindViewHolder(UnitHolder holder, int position){
            holder.bind(mUnitList.get(position));
            holder.itemView.setTag(position);
        }

        public void setUnitList(List<Unit> unitList){
            mUnitList = unitList;
        }

        @Override
        public int getItemCount(){
            return mUnitList.size();
        }
    }
}
