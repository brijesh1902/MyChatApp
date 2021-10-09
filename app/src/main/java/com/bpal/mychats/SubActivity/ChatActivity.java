package com.bpal.mychats.SubActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bpal.mychats.Adapters.ChatAdapter;
import com.bpal.mychats.Models.Messages;
import com.bpal.mychats.Models.User;
import com.bpal.mychats.R;
import com.bpal.mychats.Utils.Const;
import com.bpal.mychats.Utils.ConstFire;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    TextView name;
    EditText message;
    ImageButton send;
    User chatUser = Const.ChatUser;
    User currentUser = Const.currentUser;
    RecyclerView recyclerView;
    ChatAdapter adapter;
    ArrayList<Messages> list;
    DatabaseReference Sreference, Rreference;
    String senderID, receiverID, Message, id, time = String.valueOf(System.currentTimeMillis());
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        name = findViewById(R.id.username);
        message = findViewById(R.id.ed_msg);
        send = findViewById(R.id.send);
        recyclerView = findViewById(R.id.rv_chat);

        name.setText(chatUser.getName());

        senderID = currentUser.getUid();
        receiverID = chatUser.getUid();

        Sreference = Const.rootChatRef.child(senderID).child(ConstFire.MESSAGES);
        Rreference = Const.rootChatRef.child(receiverID).child(ConstFire.MESSAGES);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(layoutManager);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message = message.getText().toString();

                if (Message.isEmpty()) {
                    Const.showToast(getApplicationContext(), "Please enter a message!");
                } else {

                    id = Sreference.child(time).push().getKey();

                    Messages messages = new Messages(Message, time, receiverID, senderID, id);

                    Sreference.child(id).setValue(messages).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Rreference.child(id).setValue(messages);
                            message.setText("");
                            Const.sendNotification(receiverID, currentUser.getName()+": "+Message, getApplicationContext());
                        }
                    });
                }
            }
        });

        setMessageAdapter();

    }

    private void setMessageAdapter() {

        Const.rootChatRef.child(senderID).child(ConstFire.MESSAGES)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    list = new ArrayList<>();
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        Messages data = snap.getValue(Messages.class);
                        list.add(data);
                    }
                    adapter = new ChatAdapter(getApplicationContext(), list);
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);

                    if (adapter.getItemCount() <= 10) {
                        Log.d("===VALUE====L", "TRUE");
                        ((LinearLayoutManager) layoutManager).setReverseLayout(false);
                        ((LinearLayoutManager) layoutManager).setStackFromEnd(false);
                    } else {
                        Log.d("===VALUE====", "TRUE");
                        ((LinearLayoutManager) layoutManager).setReverseLayout(false);
                        ((LinearLayoutManager) layoutManager).setStackFromEnd(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}