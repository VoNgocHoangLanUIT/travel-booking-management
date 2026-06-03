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
import com.hotelmanagement.services.ReviewService;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {

    private final Context context;
    private final List<Room> rooms;
    private final ReviewService reviewService;
    private final Map<Long, String> ratingTextCache = new HashMap<>();

    public WishlistAdapter(Context context, List<Room> rooms) {
        this.context = context;
        this.rooms = rooms;
        this.reviewService = new ReviewService(context);
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

        if (room.getImageUri() != null && !room.getImageUri().trim().isEmpty()) {
            holder.imgRoom.setImageURI(android.net.Uri.parse(room.getImageUri()));
        } else {
            holder.imgRoom.setImageResource(room.getImageResId());
        }
        holder.tvTitle.setText(room.getTitle());
        holder.tvMeta.setText(room.getMeta());
        holder.tvPrice.setText(room.getPrice() + " cho 1 đêm");
        holder.tvRating.setText(getRatingText(room.getId()));

        // Đồng bộ trái tim: dùng ic_heart_filled và KHÔNG dùng ColorFilter (giống Home)
        holder.imgHeart.setImageResource(R.drawable.ic_heart_filled);

        if (room.getBadge() != null && !room.getBadge().trim().isEmpty()) {
            holder.tvBadge.setVisibility(View.VISIBLE);
            holder.tvBadge.setText(room.getBadge());
        } else {
            holder.tvBadge.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RoomDetailActivity.class);
            intent.putExtra("ROOM_ID", room.getId());
            intent.putExtra("ROOM_IMAGE", room.getImageResId());
            intent.putExtra("ROOM_IMAGE_URI", room.getImageUri());
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

    private String getRatingText(long roomId) {
        if (roomId <= 0) {
            return "★ 0,0 (0)";
        }

        String cachedText = ratingTextCache.get(roomId);
        if (cachedText != null) {
            return cachedText;
        }

        int reviewCount = reviewService.countReviewsByRoom(roomId);
        double averageRating = reviewCount > 0 ? reviewService.getAverageRatingByRoom(roomId, 0D) : 0D;
        String ratingText = String.format(new Locale("vi", "VN"), "★ %.1f (%d)", averageRating, reviewCount);
        ratingTextCache.put(roomId, ratingText);
        return ratingText;
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
