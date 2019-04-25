package com.bluve.arch.repositories;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.bluve.models.Torneira;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class TorneiraRepository {
    private FirebaseDatabase database   = FirebaseDatabase.getInstance();
    private DatabaseReference reference = database.getReference();
    private DatabaseReference torneira = reference.child("/torneira/s4JoDSWqBuX6cQLR");
    private ValueEventListener valueEventListener;

    private MutableLiveData<Boolean> abrir;
    private MutableLiveData<Boolean> fechar;
    private MutableLiveData<Torneira> checar;

    public TorneiraRepository() { }

    public MutableLiveData<Boolean> abrir() {
        if (abrir == null) {
            abrir = new MutableLiveData<>();

            OnCompleteListener listener = task -> abrir.postValue(task.isSuccessful());

            torneira.setValue(new Torneira(true))
                    .addOnCompleteListener(listener);
        }

        return abrir;
    }

    public MutableLiveData<Boolean> fechar(Torneira torn) {
        if (fechar == null) {
            fechar = new MutableLiveData<>();

            OnCompleteListener listener = task -> fechar.postValue(task.isSuccessful());

            Map<String, Object> mudancas = new HashMap<>();
                                mudancas.put("aberta", false);
                                mudancas.put("consumoTotal", torn.getConsumoTotal());
                                mudancas.put("consumo", 0);
                                mudancas.put("tempoTotal", torn.getTempoTotal());

            torneira.updateChildren(mudancas)
                    .addOnCompleteListener(listener);
        }

        return fechar;
    }

    public MutableLiveData<Torneira> chechar() {
        if (checar == null) {
            checar = new MutableLiveData<>();

            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Torneira obj = dataSnapshot.getValue(Torneira.class);
                        checar.postValue(obj);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    checar.postValue(null);
                }
            };

            torneira.addValueEventListener(valueEventListener);
        }

        return checar;
    }

    public void release() {
        if (valueEventListener != null) {
            torneira.removeEventListener(valueEventListener);
        }
    }
}
