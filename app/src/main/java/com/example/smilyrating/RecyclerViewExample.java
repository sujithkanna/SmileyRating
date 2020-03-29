package com.example.smilyrating;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hsalf.smileyrating.SmileyRating;
import com.hsalf.smileyrating.helper.SmileyActiveIndicator;

import java.util.LinkedList;
import java.util.List;

public class RecyclerViewExample extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview_example);

        List<Integer> selection = new LinkedList<>();
        for (int i = 0; i < 30; i++) {
            selection.add(1);
        }

        final SmileyActiveIndicator smileyActiveIndicator = new SmileyActiveIndicator();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return !smileyActiveIndicator.isActive();
            }
        });
        recyclerView.setAdapter(new Adapter(selection, smileyActiveIndicator));
    }

    public static class Adapter extends RecyclerView.Adapter<Holder> {

        private List<Integer> mSelection = new LinkedList<>();
        private final SmileyActiveIndicator mSmileyActiveIndicator;

        public Adapter(List<Integer> data, SmileyActiveIndicator smileyActiveIndicator) {
            mSelection.addAll(data);
            mSmileyActiveIndicator = smileyActiveIndicator;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(new SmileyRating(parent.getContext()));
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, final int position) {
            SmileyRating rating = ((SmileyRating) holder.itemView);
            rating.setRating(SmileyRating.Type.GREAT);
            rating.setRating(mSelection.get(position));
            rating.setSmileySelectedListener(new SmileyRating.OnSmileySelectedListener() {
                @Override
                public void onSmileySelected(SmileyRating.Type type) {
                    mSelection.set(position, type.getRating());
                }
            });
            mSmileyActiveIndicator.bind(rating);
        }

        @Override
        public int getItemCount() {
            return mSelection.size();
        }
    }

    public static class Holder extends RecyclerView.ViewHolder {

        public Holder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
