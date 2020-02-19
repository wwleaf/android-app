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
import me.aflak.leaf.main.presenter.MainPresenter;

public class ChatFragment extends Fragment {
    private MainPresenter presenter;

    @BindView(R.id.chat_text) TextView chat;
    @BindView(R.id.chat_input) EditText input;
    @BindView(R.id.chat_destination) EditText destination;

    public ChatFragment(MainPresenter presenter) {
        this.presenter = presenter;
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat, container, false);
        ButterKnife.bind(this, view);
        chat.setMovementMethod(new ScrollingMovementMethod());
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
//        presenter.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
//        presenter.onViewStateRestored(savedInstanceState);
    }

    @OnCheckedChanged(R.id.chat_toggle_debug)
    void onToggled(CompoundButton button, boolean isChecked) {
        presenter.onToggle(isChecked);
    }

    @OnClick(R.id.chat_send)
    void onSendMessage() {
        String message = input.getText().toString();
        String id = destination.getText().toString();
        presenter.onMessage(message, id);
    }

    void appendMessage(String message) {
        final String text = chat.getText().toString() + "\n" + message;
        Objects.requireNonNull(getActivity()).runOnUiThread(() -> chat.setText(text));
    }

    void clearInput() {
        Objects.requireNonNull(getActivity()).runOnUiThread(() -> input.setText(""));
    }

    String getChatMessage() {
        return chat.getText().toString();
    }
}
