package com.example.exploreasy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private TextView tvTitle,tvAddress,ratingBar;
    private ImageView ivImage;

    private RecyclerView recyclerView;
    private ReviewItemListAdapter adapter;
    private List<String> dataList;
    private RatingBar ratingStar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        PlaceModel data = getIntent().getParcelableExtra("placeData"); // Mengambil data dengan kunci "placeData"

        String name = data.getName();
        double rating = data.getRating();
        String address = data.getAddress();
        String review = data.getReview();
        String image2 = data.getImage2();

        tvTitle = findViewById(R.id.textViewTitle);
        tvTitle.setText(name);

        tvAddress = findViewById(R.id.textViewAddress);
        tvAddress.setText(address);

        ivImage = findViewById(R.id.imageViewPoster);
        Picasso.get().load(image2).into(ivImage);

        String[] splitStrings = review.split(" - ");
        List<String> stringList = Arrays.asList(splitStrings);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dataList = new ArrayList<>();
        dataList.addAll(stringList);

        ratingBar = findViewById(R.id.tv_rating);
        ratingStar = findViewById(R.id.ratings);
        ratingBar.setText(String.valueOf(rating));
        ratingStar.setRating((float) rating);

        adapter = new ReviewItemListAdapter(dataList);
        recyclerView.setAdapter(adapter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}