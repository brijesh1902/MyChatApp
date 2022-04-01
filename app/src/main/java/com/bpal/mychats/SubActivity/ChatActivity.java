package com.bpal.mychats.SubActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

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

    private byte encryptionkey[] = {9, 115, 51, 86, 105, 6, -31, -25, -68, 88, 17, 20, 3, -105, 119, -55};
    private Cipher cipher, decipher;
    private SecretKeySpec secretKeySpec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        try {
            cipher = Cipher.getInstance("AES");
            decipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }

        secretKeySpec = new SecretKeySpec(encryptionkey, "AES");

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
        ((LinearLayoutManager) layoutManager).setStackFromEnd(true);
        ((LinearLayoutManager) layoutManager).setReverseLayout(false);
        recyclerView.setLayoutManager(layoutManager);

        setMessageAdapter();

        send.setOnClickListener(view -> {
            Message = message.getText().toString();

            if (Message.isEmpty()) {
                Const.showToast(getApplicationContext(), "Please enter a message!", ChatActivity.this);
            } else {

                id = Sreference.child(time).push().getKey();

                Messages messages = new Messages(EncryptionMethod(Message), time, receiverID, senderID, id);

                Sreference.child(id).setValue(messages).addOnSuccessListener(unused -> {
                    Rreference.child(id).setValue(messages);
                    message.setText("");
                    Const.sendNotification(receiverID, currentUser.getName()+": "+Message, getApplicationContext());
                });
            }
        });



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
                        if (data != null) {
                            list.add(new Messages(DecryptionMethod(data.getMessage()), data.getTime(), data.getReceiverID(),
                                    data.getSenderID(), data.getId()));
                        }
                    }
                    adapter = new ChatAdapter(getApplicationContext(), list);
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String EncryptionMethod(String message) {
        String result = null;
        byte[] bytes = message.getBytes();
        byte[] encryptedByte = new byte[bytes.length];

        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            encryptedByte = cipher.doFinal(bytes);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }

        result = new String(encryptedByte, StandardCharsets.ISO_8859_1);

        return result;
    }

    private String DecryptionMethod(String message) {
        byte[] encryptedByte = message.getBytes(StandardCharsets.ISO_8859_1);
        byte[] decryption;
        String decryptedString = message;

        try {
            decipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            decryption = decipher.doFinal(encryptedByte);
            decryptedString = new String(decryption);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }

        return decryptedString;
    }
}