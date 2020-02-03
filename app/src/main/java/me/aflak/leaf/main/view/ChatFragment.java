package me.aflak.leaf.main.view;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.aflak.leaf.R;

public class ChatFragment extends Fragment {
    private OnChatListener listener;

    @BindView(R.id.chat_text) TextView chat;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat, container, false);
        ButterKnife.bind(this, view);
        chat.setMovementMethod(new ScrollingMovementMethod());
        return view;
    }

    @OnClick(R.id.chat_hello)
    public void onHelloWorld() {
        if (listener != null) {
            listener.onHelloWorld();
        }
    }

    public void appendMessage(String message) {
        final String text = chat.getText().toString() + "\n" + message;
        Objects.requireNonNull(getActivity()).runOnUiThread(() -> chat.setText(text));
    }

    public void setListener(OnChatListener listener) {
        this.listener = listener;
    }

    interface OnChatListener {
        void onHelloWorld();
    }
}
