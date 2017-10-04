package com.example.smilyrating;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hsalf.smilerating.SmileRating;

import java.util.LinkedList;
import java.util.List;

import static com.hsalf.smilerating.BaseRating.NONE;

/**
 * Created by sujith on 5/10/17.
 */

public class SmileyListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_list);
        mRecyclerView = (RecyclerView) findViewById(R.id.smiley_list);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new SmileyAdapter(prepareData());
        mRecyclerView.setAdapter(mAdapter);
    }

    private List<SmileyData> prepareData() {
        List<SmileyData> data = new LinkedList<>();
        for (int i = 0; i < 50; i++) {
            SmileyData smileyData = new SmileyData();
            smileyData.rating = NONE;
            smileyData.title = "Smiley " + (i + 1);
            data.add(smileyData);
        }
        return data;
    }

    private static class SmileyAdapter extends RecyclerView.Adapter<SmileyHolder> {


        private List<SmileyData> mDataSet;

        public SmileyAdapter(List<SmileyData> dataSet) {
            mDataSet = dataSet;
        }

        @Override
        public SmileyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SmileyHolder(View.inflate(parent.getContext(),
                    R.layout.inflater_smiley_item, null));
        }

        @Override
        public void onBindViewHolder(SmileyHolder holder, int position) {
            final SmileyData data = mDataSet.get(position);
            holder.title.setText(data.title);
            holder.sRating.setSelectedSmile(data.rating);

            holder.sRating.setOnSmileySelectionListener(new SmileRating.OnSmileySelectionListener() {
                @Override
                public void onSmileySelected(int smiley, boolean reselected) {
                    data.rating = smiley;
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mDataSet.size();
        }

    }

    private static class SmileyHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public SmileRating sRating;

        public SmileyHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.rating_title);
            sRating = (SmileRating) itemView.findViewById(R.id.smile_rating);
        }
    }

    private static class SmileyData {
        /**
         * The value should be only within (-1 to 4)
         * -1 NONE
         * 0 TERRIBLE
         * 1 BAD
         * 2 OKAY
         * 3 GOOD
         * 4 GREAT
         */
        public int rating;
        public String title;
    }
}
