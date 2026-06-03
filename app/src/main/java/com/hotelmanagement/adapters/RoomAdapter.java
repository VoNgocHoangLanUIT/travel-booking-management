package com.hotelmanagement.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.hotelmanagement.models.Room;
import com.hotelmanagement.services.FavoriteService;
import com.hotelmanagement.services.UserService;

import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    private final Context context;
    private final List<Room> rooms;
    private final FavoriteService favoriteService;
    private final long userId;

    public RoomAdapter(Context context, List<Room> rooms) {
        this.context = context;
        this.rooms = rooms;
        this.favoriteService = new FavoriteService(context);

        UserService userService = new UserService(context);
        UserEntity currentUser = userService.getCurrentUser();
        this.userId = currentUser == null ? 0 : currentUser.id;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_room_card, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = rooms.get(position);

        bindRoomImage(holder.imgRoom, room);

        holder.tvTitle.setText(room.getTitle());
        holder.tvMeta.setText(room.getMeta());
        holder.tvPrice.setText(room.getPrice());

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

        updateHeartIcon(holder, room);

        holder.imgHeart.setOnClickListener(v -> {
            if (room.getId() <= 0 || userId <= 0) {
                return;
            }

            boolean newState = !room.isFavorite();
            room.setFavorite(newState);
            updateHeartIcon(holder, room);

            if (newState) {
                if (favoriteService.getFavorite(userId, room.getId()) == null) {
                    FavoriteEntity favorite = new FavoriteEntity();
                    favorite.userId = userId;
                    favorite.roomId = room.getId();
                    favorite.createdAt = new java.util.Date();
                    favoriteService.addFavorite(favorite);
                }
            } else {
                FavoriteEntity existingFavorite = favoriteService.getFavorite(userId, room.getId());
                if (existingFavorite != null) {
                    favoriteService.removeFavorite(existingFavorite);
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
            intent.putExtra("GUEST_COUNT", 1);
            context.startActivity(intent);
        });
    }

    private void bindRoomImage(ImageView imageView, Room room) {
        if (room.getImageUri() != null && !room.getImageUri().trim().isEmpty()) {
            imageView.setImageURI(Uri.parse(room.getImageUri()));
            return;
        }

        if (room.getImageResId() != 0) {
            imageView.setImageResource(room.getImageResId());
        } else {
            imageView.setImageResource(R.drawable.vungtau_1);
        }
    }

    private void updateHeartIcon(RoomViewHolder holder, Room room) {
        if (room.isFavorite()) {
            holder.imgHeart.setImageResource(R.drawable.ic_heart_filled);
        } else {
            holder.imgHeart.setImageResource(R.drawable.ic_heart_outline);
        }
    }

    @Override
    public int getItemCount() {
        return rooms == null ? 0 : rooms.size();
    }

    static class RoomViewHolder extends RecyclerView.ViewHolder {
        ImageView imgRoom, imgHeart;
        TextView tvBadge, tvTitle, tvMeta, tvPrice;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            imgRoom = itemView.findViewById(R.id.imgRoom);
            imgHeart = itemView.findViewById(R.id.imgHeart);
            tvBadge = itemView.findViewById(R.id.tvBadge);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvMeta = itemView.findViewById(R.id.tvMeta);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}
