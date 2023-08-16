package com.example.exploreasy;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.List;


public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder> {
    private List<PlaceModel> dataList;

    public ItemListAdapter(List<PlaceModel> dataList) {
        this.dataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PlaceModel item = dataList.get(position);
        holder.tvTitle.setText(item.getName());
        holder.tvAddress.setText(item.getAddress());
        Picasso.get().load(item.getImage1()).into(holder.imageView);
        holder.ratingBar.setText(String.valueOf(item.getRating()));
        holder.ratingStar.setRating((float) item.getRating());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DetailActivity.class);
                PlaceModel data = new PlaceModel(
                        item.getName(),
                        item.getRating(),
                        item.getAddress(),
                        item.getReview(),
                        item.getImage1(),
                        item.getImage2(),
                        item.getImage3(),
                        item.getLatitude(),
                        item.getLongitude()
                );
                intent.putExtra("placeData", data);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tvTitle,tvAddress,ratingBar;
        CardView cardView;
        RatingBar ratingStar ;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_destination);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvAddress = itemView.findViewById(R.id.tv_address);
            ratingBar = itemView.findViewById(R.id.tv_rating);
            ratingStar = itemView.findViewById(R.id.ratings);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
