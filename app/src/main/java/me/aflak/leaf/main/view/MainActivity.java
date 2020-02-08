package me.aflak.leaf.main.view;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.aflak.leaf.R;
import me.aflak.leaf.app.MyApplication;
import me.aflak.leaf.graph.GraphModule;
import me.aflak.leaf.main.dagger.DaggerMainComponent;
import me.aflak.leaf.main.dagger.MainModule;
import me.aflak.leaf.main.presenter.MainPresenter;

public class MainActivity extends AppCompatActivity implements MainView {
    @BindView(R.id.connection_parent) ConstraintLayout connectionLayout;
    @BindView(R.id.connection_usb_icon) ImageView icon;
    @BindView(R.id.connection_connect_button) Button connect;
    @BindView(R.id.connection_id) EditText userId;

    @Inject ChatFragment chatFragment;
    @Inject MainPresenter presenter;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        DaggerMainComponent.builder()
                .graphModule(new GraphModule())
                .appModule(MyApplication.getApp().getAppModule())
                .mainModule(new MainModule(this))
                .build().inject(this);

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.activity_main_chat_fragment, chatFragment).commit();
        chatFragment.setListener(chatListener);
        presenter.onCreate(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @OnClick(R.id.connection_connect_button)
    void onConnect() {
        int id = Integer.parseInt(userId.getText().toString());
        presenter.onConnectClicked(id);
    }

    @Override
    public void showConnectedIcon() {
        icon.setColorFilter(ContextCompat.getColor(this, R.color.connection_usb_icon_connected));
    }

    @Override
    public void showDisconnectedIcon() {
        icon.setColorFilter(ContextCompat.getColor(this, R.color.connection_usb_icon_disconnected));
    }

    @Override
    public void showConnectButton() {
        connect.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideConnectButton() {
        connect.setVisibility(View.GONE);
    }

    @Override
    public void showMessage(String message) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .show();
    }

    @Override
    public void showChat() {
        connectionLayout.setVisibility(View.INVISIBLE);
        fragmentManager.beginTransaction()
            .show(chatFragment)
            .commit();
    }

    @Override
    public void hideChat() {
        connectionLayout.setVisibility(View.VISIBLE);
        fragmentManager.beginTransaction()
            .hide(chatFragment)
            .commit();
    }

    @Override
    public void appendChatMessage(String message) {
        chatFragment.appendMessage(message);
    }

    @Override
    public void clearInput() {
        chatFragment.clearInput();
    }

    @Override
    public void showUsers(List<Integer> users) {
        StringBuilder builder = new StringBuilder();
        for (int i=0 ; i<users.size() ; i++) {
            builder.append("[");
            builder.append(users.get(i));
            builder.append("] detected");
            if (i < users.size() - 1) {
                builder.append("\n");
            }
        }
        chatFragment.appendMessage(builder.toString());
    }

    private ChatFragment.OnChatListener chatListener = (message, destId) -> presenter.onMessage(message, destId);
}
