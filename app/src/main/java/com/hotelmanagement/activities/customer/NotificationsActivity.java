package com.hotelmanagement.activities.customer;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hotelmanagement.R;
import com.hotelmanagement.adapters.NotificationAdapter;
import com.hotelmanagement.models.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        ImageView btnBack = findViewById(R.id.btnBackNotif);
        RecyclerView rvNotifications = findViewById(R.id.rvNotifications);
        TextView tvReadAll = findViewById(R.id.tvReadAll);

        btnBack.setOnClickListener(v -> finish());

        // Thiết lập RecyclerView
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));

        // Dữ liệu giả lập thông báo với icon Emoji phù hợp hơn cho app khách sạn
        List<Notification> notificationList = new ArrayList<>();
        notificationList.add(new Notification(
                "Bạn nhận được ưu đãi VIP tháng này!",
                "Vừa xong",
                "👑",
                false
        ));
        notificationList.add(new Notification(
                "Giảm giá cực sốc 40% khi đặt phòng tại Vũng Tàu",
                "2 phút trước",
                "🔥",
                false
        ));
        notificationList.add(new Notification(
                "Chiến dịch \"Giờ Vàng\" đặt phòng vừa ra mắt",
                "1 giờ trước",
                "🕒",
                true
        ));
        notificationList.add(new Notification(
                "Yêu cầu đặt phòng #4821 của bạn đã được xác nhận",
                "3 giờ trước",
                "🏨",
                true
        ));
        notificationList.add(new Notification(
                "Quà tặng sinh nhật đặc biệt đang chờ bạn!",
                "1 ngày trước",
                "🎂",
                true
        ));
        notificationList.add(new Notification(
                "Chúc mừng! Bạn vừa đạt hạng Thành viên Vàng",
                "2 ngày trước",
                "🏆",
                true
        ));
        notificationList.add(new Notification(
                "Ưu đãi thành viên mới: Giảm 20% cho lần đặt đầu tiên",
                "3 ngày trước",
                "✨",
                true
        ));

        NotificationAdapter adapter = new NotificationAdapter(notificationList);
        rvNotifications.setAdapter(adapter);
    }
}
