package com.example.z_platform;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.notificationHolder>{

    private Context context;
    private List<Notification> notifList;

    public NotificationAdapter(Context context, List<Notification> notifications){
        this.context = context;
        notifList = notifications;
    }

    @NonNull
    @Override
    public notificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_card, parent, false);
        return new notificationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull notificationHolder holder, int position) {
        Notification notification = notifList.get(position);
        holder.body.setText(notification.getBody());
    }

    @Override
    public int getItemCount() {
        return notifList.size();
    }

    public class notificationHolder extends RecyclerView.ViewHolder {
        TextView body;
        ConstraintLayout layout;

        public notificationHolder(@NonNull View itemView) {
            super(itemView);

            body = itemView.findViewById(R.id.body);
            layout = itemView.findViewById(R.id.main_layout);
        }
    }
}
