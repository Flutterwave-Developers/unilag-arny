package com.yorubadev.arny.data.network;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class FirebaseChildQueryLiveData extends LiveData<DataSnapshot> {
    static final String RECURRING_QUERY = "recurring_query";
    private static final String ONE_TIME_QUERY = "one_time_query";
    private final MyChildEventListener mListener = new MyChildEventListener();
    private Query mQuery;
    private String mQueryFrequency;

    public FirebaseChildQueryLiveData(Query query, String queryFrequency) {
        this.mQuery = query;
        this.mQueryFrequency = queryFrequency;
    }

    FirebaseChildQueryLiveData(DatabaseReference ref, String queryFrequency) {
        this.mQuery = ref;
        this.mQueryFrequency = queryFrequency;
    }

    @Override
    protected void onActive() {
        mQuery.addChildEventListener(mListener);
    }

    @Override
    protected void onInactive() {
        if (mQueryFrequency.equals(RECURRING_QUERY))
            mQuery.removeEventListener(mListener);
    }

    private class MyChildEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            setValue(dataSnapshot);
            if (mQueryFrequency.equals(ONE_TIME_QUERY)) {
                mQuery.removeEventListener(mListener);
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    }

}
