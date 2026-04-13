package com.hotelmanagement.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hotelmanagement.R;
import com.hotelmanagement.activities.customer.RoomDetailActivity;
import com.hotelmanagement.models.Room;

import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {

    private final Context context;
    private final List<Room> rooms;

    public SearchResultAdapter(Context context, List<Room> rooms) {
        this.context = context;
        this.rooms = rooms;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Room room = rooms.get(position);

        holder.imgRoom.setImageResource(room.getImageResId());
        holder.tvTitle.setText(room.getTitle());
        holder.tvMeta.setText(room.getMeta());
        holder.tvPrice.setText(room.getPrice() + " cho 1 đêm");

        if (room.getBadge() != null && !room.getBadge().trim().isEmpty()) {
            holder.tvBadge.setVisibility(View.VISIBLE);
            holder.tvBadge.setText(room.getBadge());
        } else {
            holder.tvBadge.setVisibility(View.GONE);
        }

        // Mặc định rating giả lập nếu meta chứa rating
        // Ở đây ta dùng meta từ Room model cho đơn giản
        
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RoomDetailActivity.class);
            intent.putExtra("ROOM_IMAGE", room.getImageResId());
            intent.putExtra("ROOM_TITLE", room.getTitle());
            intent.putExtra("ROOM_META", room.getMeta());
            intent.putExtra("ROOM_PRICE", room.getPrice());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgRoom, imgHeart;
        TextView tvBadge, tvTitle, tvMeta, tvPrice, tvRating, tvDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgRoom = itemView.findViewById(R.id.imgRoomResult);
            imgHeart = itemView.findViewById(R.id.imgHeartResult);
            tvBadge = itemView.findViewById(R.id.tvBadgeResult);
            tvTitle = itemView.findViewById(R.id.tvTitleResult);
            tvMeta = itemView.findViewById(R.id.tvMetaResult);
            tvPrice = itemView.findViewById(R.id.tvPriceResult);
            tvRating = itemView.findViewById(R.id.tvRatingResult);
            tvDetails = itemView.findViewById(R.id.tvDetailsResult);
        }
    }
}
