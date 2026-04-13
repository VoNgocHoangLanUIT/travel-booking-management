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

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    private final Context context;
    private final List<Room> rooms;

    public RoomAdapter(Context context, List<Room> rooms) {
        this.context = context;
        this.rooms = rooms;
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

        holder.imgRoom.setImageResource(room.getImageResId());
        holder.tvTitle.setText(room.getTitle());
        holder.tvMeta.setText(room.getMeta());
        holder.tvPrice.setText(room.getPrice());

        if (room.getBadge() != null && !room.getBadge().trim().isEmpty()) {
            holder.tvBadge.setVisibility(View.VISIBLE);
            holder.tvBadge.setText(room.getBadge());
        } else {
            holder.tvBadge.setVisibility(View.GONE);
        }

        // 1. Hiển thị trạng thái ban đầu dựa vào data
        if (room.isFavorite()) {
            holder.imgHeart.setImageResource(R.drawable.ic_heart_filled);
        } else {
            holder.imgHeart.setImageResource(R.drawable.ic_heart_outline);
        }

        // 2. BẮT SỰ KIỆN CLICK VÀO TRÁI TIM
        holder.imgHeart.setOnClickListener(v -> {
            boolean newState = !room.isFavorite();
            room.setFavorite(newState);
            if (newState) {
                holder.imgHeart.setImageResource(R.drawable.ic_heart_filled);
            } else {
                holder.imgHeart.setImageResource(R.drawable.ic_heart_outline);
            }
        });

        // 3. BẮT SỰ KIỆN CLICK VÀO TOÀN BỘ ITEM ĐỂ XEM CHI TIẾT
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RoomDetailActivity.class);
            // TRUYỀN DỮ LIỆU SANG CHI TIẾT
            intent.putExtra("ROOM_IMAGE", room.getImageResId());
            intent.putExtra("ROOM_TITLE", room.getTitle());
            intent.putExtra("ROOM_META", room.getMeta());
            intent.putExtra("ROOM_PRICE", room.getPrice());
            context.startActivity(intent);
        });
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
