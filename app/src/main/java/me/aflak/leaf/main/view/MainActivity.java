package me.aflak.leaf.main.view;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.aflak.leaf.R;
import me.aflak.leaf.app.MyApplication;
import me.aflak.leaf.graph.GraphModule;
import me.aflak.leaf.main.dagger.DaggerMainComponent;
import me.aflak.leaf.main.dagger.MainModule;
import me.aflak.leaf.main.presenter.MainPresenter;
import me.aflak.leaf.main.entities.Destination;

public class MainActivity extends AppCompatActivity implements MainView {
    @BindView(R.id.connection_parent) LinearLayout connectionLayout;
    @BindView(R.id.connection_connect_layout) LinearLayout connectArduinoLayout;
    @BindView(R.id.connection_id) EditText userId;
    @BindView(R.id.connection_connect_text) TextView textInfo;

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
        FragmentTransaction transaction = fragmentManager.beginTransaction().add(R.id.activity_main_chat_fragment, chatFragment);
        transaction.runOnCommit(() -> presenter.onCreate(MainActivity.this));
        transaction.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @OnClick(R.id.connection_connect_button)
    void onConnect() {
        String id = userId.getText().toString();
        presenter.onConnectClicked(id);
    }

    @Override
    public void showConnectedIcon() {
        textInfo.setText(R.string.connection_leaf_plugged);
        textInfo.setTextColor(ContextCompat.getColor(this, R.color.connection_leaf_plugged));
    }

    @Override
    public void showDisconnectedIcon() {
        textInfo.setText(R.string.connection_leaf_unplugged);
        textInfo.setTextColor(ContextCompat.getColor(this, R.color.connection_leaf_unplugged));
    }

    @Override
    public void showConnectButton() {
        connectArduinoLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideConnectButton() {
        connectArduinoLayout.setVisibility(View.INVISIBLE);
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
        runOnUiThread(() -> chatFragment.appendMessage(message));
    }

    @Override
    public void clearInput() {
        runOnUiThread(() -> chatFragment.clearInput());
    }

    @Override
    public void showUsers(List<Pair<String, Byte>> users) {
        List<Destination> destinations = new ArrayList<>();
        for (Pair<String, Byte> p : users) {
            destinations.add(new Destination(p.first, p.second));
        }
        runOnUiThread(() -> chatFragment.showDestinations(destinations));
    }

    @Override
    public void showId(byte id) {
        userId.setText(String.valueOf(id));
    }

    @Override
    public void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show());
    }
}
