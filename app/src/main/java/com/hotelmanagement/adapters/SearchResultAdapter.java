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
import com.hotelmanagement.database.entities.FavoriteEntity;
import com.hotelmanagement.database.entities.UserEntity;
import com.hotelmanagement.services.ReviewService;
import com.hotelmanagement.services.FavoriteService;
import com.hotelmanagement.services.UserService;
import com.hotelmanagement.models.Room;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {

    private final Context context;
    private final List<Room> rooms;
    private final FavoriteService favoriteService;
    private final ReviewService reviewService;
    private final Map<Long, String> ratingTextCache = new HashMap<>();
    private final long userId;
    private final int guestCount;
    private final String checkInText;
    private final String checkOutText;
    private final long checkInMillis;
    private final long checkOutMillis;

    public SearchResultAdapter(Context context, List<Room> rooms) {
        this(context, rooms, 1);
    }

    public SearchResultAdapter(Context context, List<Room> rooms, int guestCount) {
        this(context, rooms, guestCount, null, null, -1L, -1L);
    }

    public SearchResultAdapter(Context context, List<Room> rooms, int guestCount, String checkInText, String checkOutText, long checkInMillis, long checkOutMillis) {
        this.context = context;
        this.rooms = rooms;
        this.guestCount = Math.max(1, guestCount);
        this.checkInText = checkInText;
        this.checkOutText = checkOutText;
        this.checkInMillis = checkInMillis;
        this.checkOutMillis = checkOutMillis;
        this.favoriteService = new FavoriteService(context);
        this.reviewService = new ReviewService(context);
        UserService userService = new UserService(context);
        UserEntity currentUser = userService.getCurrentUser();
        this.userId = currentUser == null ? 0 : currentUser.id;
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

        if (room.getBadge() != null && !room.getBadge().trim().isEmpty()) {
            holder.tvBadge.setVisibility(View.VISIBLE);
            holder.tvBadge.setText(room.getBadge());
        } else {
            holder.tvBadge.setVisibility(View.GONE);
        }

        if (room.getId() > 0 && userId > 0) {
            boolean isFavorite = favoriteService.getFavorite(userId, room.getId()) != null;
            room.setFavorite(isFavorite);
        }

        holder.imgHeart.setImageResource(
                room.isFavorite() ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline
        );

        holder.imgHeart.setOnClickListener(v -> {
            if (room.getId() <= 0 || userId <= 0) {
                return;
            }

            boolean newState = !room.isFavorite();
            room.setFavorite(newState);
            holder.imgHeart.setImageResource(
                    newState ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline
            );
            if (newState) {
                if (favoriteService.getFavorite(userId, room.getId()) == null) {
                    FavoriteEntity favorite = new FavoriteEntity();
                    favorite.userId = userId;
                    favorite.roomId = room.getId();
                    favorite.createdAt = new java.util.Date();
                    favoriteService.addFavorite(favorite);
                }
            } else {
                FavoriteEntity favorite = favoriteService.getFavorite(userId, room.getId());
                if (favorite != null) {
                    favoriteService.removeFavorite(favorite);
                }
            }
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RoomDetailActivity.class);
            intent.putExtra("ROOM_ID", room.getId());
            intent.putExtra("ROOM_IMAGE", room.getImageResId());
            intent.putExtra("ROOM_IMAGE_URI", room.getImageUri());
            intent.putExtra("ROOM_TITLE", room.getTitle());
            intent.putExtra("ROOM_META", room.getMeta());
            intent.putExtra("ROOM_PRICE", room.getPrice());
            intent.putExtra("GUEST_COUNT", guestCount);
            intent.putExtra("CHECK_IN_TEXT", checkInText);
            intent.putExtra("CHECK_OUT_TEXT", checkOutText);
            intent.putExtra("CHECK_IN_MILLIS", checkInMillis);
            intent.putExtra("CHECK_OUT_MILLIS", checkOutMillis);
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
