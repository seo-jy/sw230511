package com.example.sw221103;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class UserAdapter extends ListAdapter<User1, UserAdapter.UserVH> {

    private final Context context;

    public UserAdapter(Context context) {
        super(new DiffUtil.ItemCallback<User1>() {
            @Override
            public boolean areItemsTheSame(@NonNull User1 oldItem, @NonNull User1 newItem) {
                return TextUtils.equals(oldItem.getId(), newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull User1 oldItem, @NonNull User1 newItem) {
                return TextUtils.equals(oldItem.getUid(), newItem.getUid()) &&
                        TextUtils.equals(oldItem.getUser_key(), newItem.getUser_key()) &&
                        TextUtils.equals(oldItem.getUser_name(), newItem.getUser_name()) &&
                        TextUtils.equals(oldItem.getUser_date(), newItem.getUser_date()) &&
                        TextUtils.equals(oldItem.getUser_order_date(), newItem.getUser_order_date()) &&
                        TextUtils.equals(oldItem.getUser_password(), newItem.getUser_password());
            }

            @Override
            public Object getChangePayload(@NonNull User1 oldItem, @NonNull User1 newItem) {
                return new Object();
            }
        });

        this.context = context;
    }

    @NonNull
    @Override
    public UserVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new UserVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserVH holder, int position) {
        User1 user1 = getItem(position);

        holder.nameText.setText(user1.getUser_name());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UpdateActivity.class);
                intent.putExtra("key", user1.getUser_key());
                intent.putExtra("name", user1.getUser_name());
                //intent.putExtra("content", user1.getUser_content());
                intent.putExtra("date", user1.getUser_date());
                intent.putExtra("orderdate", user1.getUser_order_date());
                context.startActivity(intent);
            }
        });
    }

    static class UserVH extends RecyclerView.ViewHolder {
        TextView nameText;
        CardView cardView;

        public UserVH(@NonNull View itemView) {
            super(itemView);

            nameText = itemView.findViewById(R.id.name_text);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }
}