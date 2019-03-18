package com.bluve.arch.repositories;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.bluve.models.Tap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TapRepository {
    private FirebaseDatabase database   = FirebaseDatabase.getInstance();
    private DatabaseReference reference = database.getReference();
    private DatabaseReference tap = reference.child("/tap/s4JoDSWqBuX6cQLR");
    private ValueEventListener valueEventListener;

    private MutableLiveData<Boolean> open;
    private MutableLiveData<Boolean> close;
    private MutableLiveData<Tap> tapState;

    public TapRepository() { }

    public MutableLiveData<Boolean> open() {
        if (open == null) {
            open = new MutableLiveData<>();

            OnCompleteListener listener = task -> {
                open.postValue(task.isSuccessful());
            };

            tap.setValue(new Tap(true))
                    .addOnCompleteListener(listener);
        }

        return open;
    }

    public MutableLiveData<Boolean> close() {
        if (close == null) {
            close = new MutableLiveData<>();

            OnCompleteListener listener = task -> {
                close.postValue(task.isSuccessful());
            };

            tap.setValue(new Tap(false))
                    .addOnCompleteListener(listener);
        }

        return close;
    }

    public MutableLiveData<Tap> tap() {
        if (tapState == null) {
            tapState = new MutableLiveData<>();

            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Tap obj = dataSnapshot.getValue(Tap.class);
                        tapState.postValue(obj);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    tapState.postValue(null);
                }
            };

            tap.addValueEventListener(valueEventListener);
        }

        return tapState;
    }

    public void release() {
        if (valueEventListener != null) {
            tap.removeEventListener(valueEventListener);
        }
    }
}
