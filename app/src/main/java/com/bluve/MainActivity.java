package com.bluve;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bluve.arch.repositories.TorneiraRepository;
import com.bluve.arch.viewmodels.TorneiraViewModel;
import com.bluve.models.Torneira;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.itangqi.waveloadingview.WaveLoadingView;

public class MainActivity extends AppCompatActivity  {

    @BindView(R.id.tv_consumo_porcentagem)
    public TextView tvConsumoPorcentagem;
    @BindView(R.id.ch_tempo_consumo)
    public Chronometer chTempoConsumo;
    @BindView(R.id.tv_consumo_atual)
    public TextView tvConsumo;
    @BindView(R.id.view_torneira_on)
    public FrameLayout mViewTorneiraOn;
    @BindView(R.id.view_torneira_off)
    public LinearLayout mViewTorneiraOff;
    @BindView(R.id.wv_wave_progress)
    public WaveLoadingView wvWaveProgresso;

    private MenuItem menuItem;
    private TorneiraViewModel vmTorneira;
    private boolean isTorneiraAberta;
    private long mConsumoTotal;
    private long mTempoTotal;
    private long mConsumoLimite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            mConsumoLimite = getConsumoLimite();
        }
        else {
            isTorneiraAberta =
                    savedInstanceState.getBoolean(Constants.KEY_TORNEIRA_ABERTA);

            if (isTorneiraAberta) {
                chTempoConsumo.setBase(
                        savedInstanceState.getLong(Constants.KEY_TEMPO_CRONOMETRO));
                chTempoConsumo.start();
            }
        }

        vmTorneira = ViewModelProviders
                .of(this)
                .get(TorneiraViewModel.class);

        vmTorneira.setRepository(new TorneiraRepository());
        vmTorneira.torneira().observe(this, torneira -> {
            if (torneira != null) {
                processaDadosTorneira(torneira);
            } else {
                Log.w("MainAcivity", "torneira nula");
            }
        });
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mConsumoTotal = savedInstanceState.getLong(Constants.KEY_CONSUMO_TOTAL);
        mTempoTotal = savedInstanceState.getLong(Constants.KEY_TEMPO_TOTAL);
        mConsumoLimite = savedInstanceState.getLong(Constants.KEY_CONSUMO_LIMITE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(Constants.KEY_CONSUMO_TOTAL, mConsumoTotal);
        outState.putLong(Constants.KEY_TEMPO_TOTAL, mTempoTotal);
        outState.putLong(Constants.KEY_CONSUMO_LIMITE, mConsumoLimite);
        outState.putBoolean(Constants.KEY_TORNEIRA_ABERTA, isTorneiraAberta);

        if (chTempoConsumo != null) {
            outState.putLong(Constants.KEY_TEMPO_CRONOMETRO, chTempoConsumo.getBase());
        }
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
            if (isTorneiraAberta) fechar();
            else abrir();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Método responsável por abrir a torneira. Quando é acionado, atualiza
     * a flag {@link Torneira#aberta} setando-a como true no Database.
     */
    private void abrir() {
        vmTorneira.abrir().observe(this, success -> {
            if (success != null) {
                if (success) {
                    isTorneiraAberta = true;
                    menuItem.setIcon(R.drawable.ic_drop_stop_24dp);
                }
                else {
                    Toast.makeText(this, "Não conseguimos abrir a torneira", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    /**
     * Método responsável por fechar a torneira. Quando é acionado, atualiza
     * algums dados da torneira e manda esse objeto para o Database com
     * a flag {@link Torneira#aberta} setada como false.
     */
    private void fechar() {
        Torneira torneira = new Torneira();
                 torneira.setAberta(false);
                 torneira.setConsumo(0);
                 torneira.setConsumoTotal(mConsumoTotal);
                 torneira.setTempoTotal(mTempoTotal);

        vmTorneira.fechar(torneira).observe(this, success -> {
            if (success != null) {
                if (success) {
                    isTorneiraAberta = false;
                    menuItem.setIcon(R.drawable.ic_drop_24dp);
                }
                else {
                    Toast.makeText(this, "Não conseguimos fechar a torneira", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    /**
     * Chamado no {@link android.arch.lifecycle.MutableLiveData#observe(LifecycleOwner, Observer)}
     * do @{@link android.arch.lifecycle.ViewModel}, ficando responsável por
     * processar as informações vindas do Database e atualizando a UI com base nessas
     * informações.
     * <p>
     * Se é detectado que a torneira está aberta, então atualizamos a UI mostrando
     * as {@link View} informativas na tela. Calculamos também a porcentagem do limite
     * que o usuário define pelo consumo da torneira. Aqui também é possível atualizar
     * as informações de consumo e tempo total da torneira.
     *
     * @param torneira Um objeto com todos os dados referentes à torneira
     */
    private void processaDadosTorneira(Torneira torneira) {
        if (torneira.isAberta()) {
            // Inicia o cronômetro
            chTempoConsumo.start();

            /*
              Essas variáveis servirão para quando o usuário fechar a torneira, nós
              sermos capazes de atualizar o tempo e o consumo total. Sem isso seria
              preciso passa o objeto torneira para o {@link Bundle} em
              {@link MainActivity#onSaveInstanceState(Bundle outState)},
              o que não é tão legal.

              Passando o objeto para o {@link Bundle} nos teriamos que serializar a
              classe do objeto o que poluiria nosso código, além de que dimuniríamos
              nossa performace podendo causar vazamentos de memória a crashar o app
              devido o consumo de processamento para serializar.

              Salvando apenas as variáveis fica fácil de trabalhamos, pois são apenas
              dados primitivos o que torna a passagem para o {@link Bundle} bem mais
              fácil. Isso será feito em {@link onSaveInstanceState(Bundle outState)}
              e depois recuperado em {@link MainActivity#onRestoreInstanceState(Bundle)}.

              Para atualizar o consumo e o tempo total no Database, nós apenas somamos
              o que já temos com que veio do Database enquanto a torneira estava
              ligada.

              @see {@link Bundle}
             */
            mConsumoTotal = mConsumoTotal + torneira.getConsumoTotal();
            /*
             Aqui o tempo total é igual ao que veio do Database mais o tempo do
             cronômetro (do tipo long). Salvando o tempo, nos padronizamos ele
             para o padrão timestampo (long), o que nos permite fazer cálculos
             mais fácilmente
             */
            mTempoTotal =  chTempoConsumo.getBase() + torneira.getTempoTotal();

            long progresso = calculaPorcentagem(torneira.getConsumo());

            wvWaveProgresso.setProgressValue((int) progresso);
            wvWaveProgresso.startAnimation();

            // Aqui nos mostramos o progresso
            tvConsumoPorcentagem.setText(progresso + "%");
            // Aqui nos mostramos o consumo
            tvConsumo.setText(torneira.getConsumo() + "L");

            if (mViewTorneiraOff.getVisibility() == View.VISIBLE) {
                // Mostramos a view de quando a torneira aberta
                mViewTorneiraOn.setVisibility(View.VISIBLE);
                // Escondemos a view de quando a torneira fechada
                mViewTorneiraOff.setVisibility(View.GONE);
                // Trocamos o ícone do menu p/ ícone da gota fechada
                menuItem.setIcon(R.drawable.ic_drop_stop_24dp);
            }
        } else {
            // Para a animação da onda
            wvWaveProgresso.cancelAnimation();
            // Para o cronômetro
            chTempoConsumo.stop();
            chTempoConsumo = null;
            mConsumoTotal = 0;

            if (mViewTorneiraOn.getVisibility() == View.VISIBLE) {
                // Mostramos a view de quando a torneira fechada
                mViewTorneiraOff.setVisibility(View.VISIBLE);
                // Escondemos a view de quando a torneira aberta
                mViewTorneiraOn.setVisibility(View.GONE);
                // Trocamos o ícone do menu p/ ícone da gota aberta
                menuItem.setIcon(R.drawable.ic_drop_24dp);
            }
        }
    }

    private long calculaPorcentagem(long consumo) {
        return (consumo * mConsumoLimite) / 100;
    }

    /**
     * Método utilitário para obtermos o limite de consumo definido pelo
     * usuário.
     * <p>
     * Esse limite é um número do tipo {@link Long} e está
     * salvo em {@link SharedPreferences}. Caso ainda não haja um limite
     * definido, isto e, {@link SharedPreferences#getLong(String, long)} retorne
     * null, utilizamos um valor padrão.
     *
     * @return um número do tipo long que represento o limite de consumo
     * @see     SharedPreferences
     */
    private long getConsumoLimite() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getLong(Constants.KEY_CONSUMO_LIMITE, Constants.CONSUMO_LIMITE_DEFAULT);
    }
}
