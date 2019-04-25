package com.bluve.arch.viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.bluve.arch.repositories.TorneiraRepository;
import com.bluve.models.Torneira;

public class TorneiraViewModel extends ViewModel {
    private TorneiraRepository repo;
    private MutableLiveData<Boolean> abrir;
    private MutableLiveData<Boolean> fechar;
    private MutableLiveData<Torneira> tap;

    public TorneiraViewModel() {
    }

    public void setRepository(TorneiraRepository repo) {
        this.repo = repo;
    }

    public MutableLiveData<Boolean> abrir() {
        if (abrir == null) {
            abrir = repo.abrir();
        }

        return abrir;
    }

    public MutableLiveData<Boolean> fechar(Torneira torneira) {
        if (fechar == null) {
            fechar = repo.fechar(torneira);
        }

        return fechar;
    }

    public MutableLiveData<Torneira> torneira() {
        if (tap == null) {
            tap = repo.chechar();
        }

        return tap;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repo.release();
    }
}
