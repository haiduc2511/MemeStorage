package com.example.memestorage.services;

import com.example.memestorage.models.MemeBrowsedModel;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;

public interface MemeBrowsedApiService {
    @GET("images")
    Observable<List<MemeBrowsedModel>> getMemes();

}
