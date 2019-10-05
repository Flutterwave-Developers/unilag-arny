package com.yorubadev.arny.data.network;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class FirebaseQueryLiveData extends LiveData<DataSnapshot> {

    static final String ONE_TIME_QUERY = "one_time_query";
    static final String RECURRING_QUERY = "recurring_query";
    private final MyValueEventListener mListener = new MyValueEventListener();

    private Query mQuery;
    private String mQueryFrequency;

    public FirebaseQueryLiveData(Query query, String queryFrequency) {
        this.mQuery = query;
        this.mQueryFrequency = queryFrequency;
    }

    FirebaseQueryLiveData(DatabaseReference ref, String queryFrequency) {
        this.mQuery = ref;
        this.mQueryFrequency = queryFrequency;
    }

    public FirebaseQueryLiveData(String queryFrequency) {
        this.mQueryFrequency = queryFrequency;
        this.mQuery = null;
    }

    public FirebaseQueryLiveData() {
        this.mQuery = null;
        this.mQueryFrequency = "";
    }

    public void setQueryFrequency(String queryFrequency) {
        this.mQueryFrequency = queryFrequency;
    }

    public void setQuery(Query query) {
        this.mQuery = query;
    }

    public void setQuery(DatabaseReference ref) {
        this.mQuery = ref;
    }

    @Override
    protected void onActive() {
        switch (mQueryFrequency) {
            case ONE_TIME_QUERY:
                mQuery.addListenerForSingleValueEvent(mListener);
                break;
            case RECURRING_QUERY:
                mQuery.addValueEventListener(mListener);
                break;
            default:
                Crashlytics.logException(new IllegalArgumentException("Incorrect query type passed"));
        }
    }

    @Override
    protected void onInactive() {
        if (mQueryFrequency.equals(RECURRING_QUERY))
            mQuery.removeEventListener(mListener);
    }

    private class MyValueEventListener implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            setValue(dataSnapshot);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    }

}
