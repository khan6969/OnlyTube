package com.example.onlytube;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class SuggestionViewModel extends ViewModel {

    private String query;

    public LiveData<String> getLiveData() {
        return SuggestionRepository.getInstance().getSuggestions(query);
    }

    public void setQuery(String query) {
        this.query = query;
        getLiveData();
    }
}
