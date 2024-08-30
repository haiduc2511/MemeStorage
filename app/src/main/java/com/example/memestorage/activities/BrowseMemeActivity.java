package com.example.memestorage.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.memestorage.R;
import com.example.memestorage.adapters.BrowseMemeAdapter;
import com.example.memestorage.databinding.ActivityBrowseMemeBinding;
import com.example.memestorage.models.MemeBrowsedModel;
import com.example.memestorage.viewmodels.MemeBrowsedViewModel;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;

public class BrowseMemeActivity extends AppCompatActivity {

    private MemeBrowsedViewModel memeViewModel;
    private BrowseMemeAdapter memeSearchedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityBrowseMemeBinding binding = ActivityBrowseMemeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        memeViewModel = new ViewModelProvider(this).get(MemeBrowsedViewModel.class);

        memeSearchedAdapter = new BrowseMemeAdapter(this);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        binding.recyclerView.setAdapter(memeSearchedAdapter);

        memeViewModel.getMemes()
                .flatMap(new Function<List<MemeBrowsedModel>, Observable<MemeBrowsedModel>>() {
                    @Override
                    public Observable<MemeBrowsedModel> apply(List<MemeBrowsedModel> memes) throws Throwable {
                        return Observable.fromIterable(memes);
                    }
                }).subscribe(new Observer<MemeBrowsedModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(MemeBrowsedModel memeBrowsedModel) {
                        memeSearchedAdapter.addMemeBrowsed(memeBrowsedModel);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {}
                });
    }
}