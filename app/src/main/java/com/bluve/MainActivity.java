package com.bluve;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bluve.arch.repositories.TapRepository;
import com.bluve.arch.viewmodels.TapViewModel;
import com.bluve.models.Tap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tvMensagem;
    private Button btnAbrir;
    private boolean aberta = false;
    public TapViewModel tapViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (tapViewModel == null) {
            tapViewModel = new TapViewModel(new TapRepository());
        }

        tapViewModel.tap().observe(this, tap -> {
            if (tap.isOpen()) {
                tvMensagem.setText("Torneira aberta");
                btnAbrir.setText(getString(R.string.tap_close));

                aberta = true;
            } else {
                tvMensagem.setText("Torneira fechada");
                btnAbrir.setText(getString(R.string.tap_open));

                aberta = false;
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_abrir:

                if (aberta) {
                    fechar();
                } else {
                    abrir();
                }

                break;
        }
    }

    private void inicializa() {
        tvMensagem = (TextView) findViewById(R.id.tv_mensagem);
        btnAbrir   = (Button) findViewById(R.id.btn_abrir);
        btnAbrir.setOnClickListener(this);
    }

    private void abrir() {
        tapViewModel.open().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Aberta", Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    private void fechar() {
        tapViewModel.close().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Fechar", Toast.LENGTH_LONG)
                        .show();
            }
        });
    }
}
