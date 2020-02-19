package me.aflak.leaf.main.view;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import me.aflak.leaf.R;

public class ChatFragment extends Fragment {
    private OnChatListener listener;

    @BindView(R.id.chat_text) TextView chat;
    @BindView(R.id.chat_input) EditText input;
    @BindView(R.id.chat_destination) EditText destination;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat, container, false);
        ButterKnife.bind(this, view);
        chat.setMovementMethod(new ScrollingMovementMethod());
        return view;
    }

    @OnCheckedChanged(R.id.chat_toggle_debug)
    void onToggled(CompoundButton button, boolean isChecked) {
        if (listener != null) {
            listener.onToggle(isChecked);
        }
    }

    @OnClick(R.id.chat_send)
    void onSendMessage() {
        if (listener != null) {
            String message = input.getText().toString();
            int destId = Integer.parseInt(destination.getText().toString());
            listener.onMessage(message, destId);
        }
    }

    void appendMessage(String message) {
        final String text = chat.getText().toString() + "\n" + message;
        Objects.requireNonNull(getActivity()).runOnUiThread(() -> chat.setText(text));
    }

    void clearInput() {
        Objects.requireNonNull(getActivity()).runOnUiThread(() -> input.setText(""));
    }

    void setListener(OnChatListener listener) {
        this.listener = listener;
    }

    interface OnChatListener {
        void onMessage(String message, int destId);
        void onToggle(boolean isChecked);
    }
}
