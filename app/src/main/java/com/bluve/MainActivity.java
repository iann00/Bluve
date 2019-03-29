package com.bluve;

import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bluve.arch.repositories.TapRepository;
import com.bluve.arch.viewmodels.TapViewModel;
import com.bluve.models.Tap;
import com.scwang.wave.MultiWaveHeader;

public class MainActivity extends AppCompatActivity {
    private TextView tvUsage;
    private TextView tvTimeUsage;
    private TextView tvTotalUsage;
    private LinearLayout clTapOn;
    private LinearLayout clTapOff;
    private int usageLimit;
    private int activityHeight;
    private MultiWaveHeader waveHeader;
    private boolean aberta = false;
    private MenuItem menuItem;
    public TapViewModel tapViewModel;
    private Tap tap = new Tap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();

            display.getSize(size);
            activityHeight = size.y;

            inicializa();
            usageLimit = getUsageLimit();
        } else {
            activityHeight = savedInstanceState.getInt(Constants.KEY_ACTIVITY_HEIGHT);
            usageLimit      = savedInstanceState.getInt(Constants.KEY_USAGE_LIMIT);
            aberta          = savedInstanceState.getBoolean(Constants.KEY_FLAG_TAP_OPEN);
        }

        tapViewModel = ViewModelProviders
                .of(this)
                .get(TapViewModel.class);

        tapViewModel.setRepository(new TapRepository());

        tapViewModel.tap().observe(this, tap -> {
            if (tap.isOpen()) {
                aberta = true;

                int progress = (tap.getLastUsage() * usageLimit) / 100;

                tvUsage.setText(String.valueOf(progress) + "%");
                tvTotalUsage.setText(tap.getTotalUsage() + "L");

                waveHeader.start();
                waveHeader.setProgress(progress);

                switchView();
            } else {
                aberta = false;

                waveHeader.stop();
                switchView();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(Constants.KEY_ACTIVITY_HEIGHT, activityHeight);
        outState.putInt(Constants.KEY_USAGE_LIMIT, usageLimit);
        outState.putBoolean(Constants.KEY_FLAG_TAP_OPEN, aberta);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        menuItem = item;

        if (item.getItemId() == R.id.action_open_tap) {
            if (aberta) {
                fechar();
                item.setIcon(R.drawable.ic_drop_24dp);
            } else {
                abrir();
                item.setIcon(R.drawable.ic_drop_stop_24dp);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void inicializa() {
        tvUsage = (TextView) findViewById(R.id.tv_usage);
        tvTimeUsage = (TextView) findViewById(R.id.tv_time_usage);
        tvTotalUsage = (TextView) findViewById(R.id.tv_total_usage);
        waveHeader = (MultiWaveHeader) findViewById(R.id.wave_header);
        clTapOn = (LinearLayout) findViewById(R.id.ll_tap_on);
        clTapOff = (LinearLayout) findViewById(R.id.cl_tap_off);
    }

    private void abrir() {
        tapViewModel.open().observe(this, success -> {
            if (success) {
                aberta = true;
            }
        });
    }

    private void fechar() {
        tapViewModel.close().observe(this, success -> {
            if (success) {
                aberta = false;
            }
        });
    }

    private int getUsageLimit() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getInt(Constants.KEY_USAGE_LIMIT, Constants.USAGE_LIMIT_DEFAULT);
    }

    private void switchView() {
        if (aberta) {
            if (clTapOn.getVisibility() != View.VISIBLE) {
                clTapOn.setVisibility(View.VISIBLE);
                menuItem.setIcon(R.drawable.ic_drop_stop_24dp);
                clTapOff.setVisibility(View.GONE);
            }
        } else {
            if (clTapOff.getVisibility() != View.VISIBLE) {
                clTapOff.setVisibility(View.VISIBLE);
                menuItem.setIcon(R.drawable.ic_drop_24dp);
                clTapOn.setVisibility(View.GONE);
            }
        }
    }
}
