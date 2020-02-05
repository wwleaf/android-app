package me.aflak.leaf.main.view;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.aflak.leaf.R;
import me.aflak.leaf.main.dagger.DaggerMainComponent;
import me.aflak.leaf.main.dagger.MainModule;
import me.aflak.leaf.main.presenter.MainPresenter;

public class MainActivity extends AppCompatActivity implements MainView {
    @BindView(R.id.connection_parent) ConstraintLayout connectionLayout;
    @BindView(R.id.connection_usb_icon) ImageView icon;
    @BindView(R.id.connection_connect_button) Button connect;

    @Inject ChatFragment chatFragment;
    @Inject MainPresenter presenter;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        DaggerMainComponent.builder()
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
        presenter.onConnectClicked();
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
    public void showUsers(List<Integer> users) {

    }

    private ChatFragment.OnChatListener chatListener = (message) -> presenter.onMessage(message);
}
