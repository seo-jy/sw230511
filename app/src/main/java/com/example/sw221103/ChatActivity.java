package com.example.sw221103;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sw221103.databinding.ItemChatBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {

    private String CHAT_NAME;
    private String USER_NAME;

    private RecyclerView chat_view;
    private EditText chat_edit;
    private Button chat_send;
    private Button chat_image;
    private Uri selectedImageUri;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private StorageReference storageReference;

    private final ChatAdapter adapter = new ChatAdapter();

    private static final int REQUEST_IMAGE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // 위젯 ID 참조
        chat_view = (RecyclerView) findViewById(R.id.chat_view);
        chat_edit = (EditText) findViewById(R.id.chat_edit);
        chat_send = (Button) findViewById(R.id.chat_sent);
        chat_image = (Button) findViewById(R.id.chat_image);

        // 로그인 화면에서 받아온 채팅방 이름, 유저 이름 저장
        Intent intent = getIntent();
        CHAT_NAME = intent.getStringExtra("chatName");
        USER_NAME = intent.getStringExtra("userName");

        // 채팅 방 입장
        openChat(CHAT_NAME);

        // 이미지 선택 버튼 클릭 이벤트 처리
        chat_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE);
            }
        });

        // 메시지 전송 버튼에 대한 클릭 리스너 지정
        chat_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chat_edit.getText().toString().equals(""))
                    return;

                ChatDTO chat = new ChatDTO(USER_NAME, chat_edit.getText().toString()); //ChatDTO를 이용하여 데이터를 묶는다.
                databaseReference.child("chat").child(CHAT_NAME).push().setValue(chat); // 데이터 푸쉬
                chat_edit.setText(""); //입력창 초기화

            }
        });

        Button image_sent = (Button) findViewById(R.id.image_sent);
        image_sent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this, DrawActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();

            DatabaseReference ref = databaseReference.child("chat").child(CHAT_NAME).push();

            ChatDTO chat = new ChatDTO(USER_NAME, null, selectedImageUri.toString());
            chat.setId(ref.getKey());

            ArrayList<ChatDTO> messages = new ArrayList(adapter.getCurrentList());
            messages.add(chat);
            adapter.submitList(messages);

            // 선택된 이미지를 Firebase Storage에 업로드하고, 업로드된 이미지의 다운로드 URL을 가져오는 코드
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images");
            StorageReference imageRef = storageRef.child(selectedImageUri.getLastPathSegment());
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("chat_images/" + UUID.randomUUID().toString());
            storageReference.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // 업로드된 이미지의 다운로드 URL을 가져오면 채팅 메시지와 함께 Firebase Realtime Database에 업로드하는 코드
                            chat.setImageUrl(uri.toString());
                            ref.setValue(chat);
                        }
                    });
                }
            });
        }
    }

    private void openChat(String chatName) {
        chat_view.setAdapter(adapter);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                try {
                    chat_view.scrollToPosition(positionStart + itemCount - 1);
                } catch (Exception ignore) {
                }
            }
        });

        databaseReference.child("chat").child(chatName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<ChatDTO> messages = new ArrayList<>();

                for (DataSnapshot child : snapshot.getChildren()) {
                    ChatDTO message = child.getValue(ChatDTO.class);

                    message.setId(child.getKey());

                    String sender = message.getUserName();
                    String text = message.getMessage();
                    String chat = sender + " : " + text;
                    message.setMessage(chat);

                    messages.add(message);
                }

                adapter.submitList(messages);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private static class ChatAdapter extends ListAdapter<ChatDTO, ChatAdapter.ChatItemViewHolder> {

        public ChatAdapter() {
            super(new DiffUtil.ItemCallback<ChatDTO>() {
                @Override
                public boolean areItemsTheSame(@NonNull ChatDTO oldItem, @NonNull ChatDTO newItem) {
                    return TextUtils.equals(oldItem.getId(), newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull ChatDTO oldItem, @NonNull ChatDTO newItem) {
                    return TextUtils.equals(oldItem.getUserName(), newItem.getUserName()) &&
                            TextUtils.equals(oldItem.getMessage(), newItem.getMessage());
                }
            });
        }

        @Override
        public int getItemViewType(int position) {
            if (position == RecyclerView.NO_POSITION) return 0;

            if (!TextUtils.isEmpty(getItem(position).getImageUrl())) return 1;
            return 0;
        }

        @NonNull
        @Override
        public ChatItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemChatBinding binding = ItemChatBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            if (viewType == 0) {
                binding.imageView.setVisibility(View.GONE);
            } else {
                binding.textView.setVisibility(View.GONE);
            }

            return new ChatItemViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ChatItemViewHolder holder, int position) {
            ChatDTO data = getItem(position);

            if (data.getImageUrl() != null) {
                Glide.with(holder.binding.imageView)
                        .load(data.getImageUrl())
                        .into(holder.binding.imageView);
            } else {
                holder.binding.textView.setText(data.getMessage());
            }
        }

        static class ChatItemViewHolder extends RecyclerView.ViewHolder {
            public final ItemChatBinding binding;

            public ChatItemViewHolder(ItemChatBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}
