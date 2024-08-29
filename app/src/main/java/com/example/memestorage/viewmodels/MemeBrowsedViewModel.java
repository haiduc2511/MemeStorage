package com.example.memestorage.viewmodels;

import androidx.lifecycle.ViewModel;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.core.Observable;
import androidx.lifecycle.ViewModel;

import com.example.memestorage.models.MemeBrowsedModel;
import com.example.memestorage.services.MemeBrowsedApiService;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MemeBrowsedViewModel extends ViewModel {

    private MemeBrowsedApiService memeApiService;

    public MemeBrowsedViewModel() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.memegen.link/") // Replace with your base URL
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        memeApiService = retrofit.create(MemeBrowsedApiService.class);
    }

    public Observable<List<MemeBrowsedModel>> getMemes() {
        return memeApiService.getMemes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
