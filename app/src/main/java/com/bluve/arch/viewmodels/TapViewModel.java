package com.bluve.arch.viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.bluve.MainActivity;
import com.bluve.arch.repositories.TapRepository;
import com.bluve.models.Tap;

public class TapViewModel extends ViewModel {
    private TapRepository repo;
    private MutableLiveData<Boolean> open;
    private MutableLiveData<Boolean> close;
    private MutableLiveData<Tap> tap;

    public TapViewModel(TapRepository repo) {
        this.repo = repo;
    }

    public MutableLiveData<Boolean> open() {
        if (open == null) {
            open = repo.open();
        }

        return open;
    }

    public MutableLiveData<Boolean> close() {
        if (close == null) {
            close = repo.close();
        }

        return close;
    }

    public MutableLiveData<Tap> tap() {
        if (tap == null) {
            tap = repo.tap();
        }

        return tap;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repo.release();
    }
}
