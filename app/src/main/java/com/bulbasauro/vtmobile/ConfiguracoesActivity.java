package com.bulbasauro.vtmobile;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import com.bulbasauro.abstracts.AbstractActivity;
import com.bulbasauro.misc.Comando;
import com.bulbasauro.misc.CustomOnItemSelectedListener;

public class ConfiguracoesActivity extends AbstractActivity {

    private Spinner spinnerTema;
    private Spinner spinnerToqueSimples;
    private Spinner spinnerToqueDuplo;
    private Switch switchEstilo;
    private Switch switchSalvarLoginSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.config));

        iniciarSpinnerTema();
        iniciarSpinnerToques();
        inicializarSwitchEstilo();
        inicializarSwitchSalvarLoginSenha();
    }

    @Override
    public void abrirPagina(int numeroPagina, Comando comando) {

    }

    private void iniciarSpinnerTema() {
        spinnerTema = (Spinner) findViewById(R.id.spinner_tema);
        spinnerTema.setOnItemSelectedListener(new CustomOnItemSelectedListener(ConfiguracoesActivity.this, getEditor(), getString(R.string.sp_tema)));

        ArrayAdapter<CharSequence> dataAdapterSimples = ArrayAdapter.createFromResource(this, R.array.spinner_config_tema, R.layout.spinner_toques);
        dataAdapterSimples.setDropDownViewResource(R.layout.spinner_toques);
        spinnerTema.setAdapter(dataAdapterSimples);

        int selected = getSharedPreferences().getInt(getString(R.string.sp_tema), 0);
        spinnerTema.setSelection(selected);
    }

    public void iniciarSpinnerToques() {
        spinnerToqueSimples = (Spinner) findViewById(R.id.spinner_toques_simples);
        spinnerToqueSimples.setOnItemSelectedListener(new CustomOnItemSelectedListener(ConfiguracoesActivity.this, getEditor(), getString(R.string.sp_toque_simples)));

        ArrayAdapter<CharSequence> dataAdapterSimples = ArrayAdapter.createFromResource(this, R.array.spinner_config_toques, R.layout.spinner_toques);
        dataAdapterSimples.setDropDownViewResource(R.layout.spinner_toques);
        spinnerToqueSimples.setAdapter(dataAdapterSimples);

        int selected = getSharedPreferences().getInt(getString(R.string.sp_toque_simples), 1);
        spinnerToqueSimples.setSelection(selected);


        spinnerToqueDuplo = (Spinner) findViewById(R.id.spinner_toques_duplo);
        spinnerToqueDuplo.setOnItemSelectedListener(new CustomOnItemSelectedListener(ConfiguracoesActivity.this, getEditor(), getString(R.string.sp_toque_duplo)));

        ArrayAdapter<CharSequence> dataAdapterDuplo = ArrayAdapter.createFromResource(this, R.array.spinner_config_toques2, R.layout.spinner_toques);
        dataAdapterDuplo.setDropDownViewResource(R.layout.spinner_toques);
        spinnerToqueDuplo.setAdapter(dataAdapterDuplo);

        int selectedDuplo = getSharedPreferences().getInt(getString(R.string.sp_toque_duplo), 0);
        spinnerToqueDuplo.setSelection(selectedDuplo);
    }

    public void inicializarSwitchEstilo() {
        switchEstilo = (Switch) findViewById(R.id.switch_config_estilo);

        switchEstilo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getEditor().putBoolean(getString(R.string.sp_post_estilo), isChecked);
                getEditor().commit();
            }
        });

        Boolean estiloSocial = getSharedPreferences().getBoolean(getString(R.string.sp_post_estilo), false);
        switchEstilo.setChecked(estiloSocial);
    }

    public void inicializarSwitchSalvarLoginSenha() {
        switchSalvarLoginSenha = (Switch) findViewById(R.id.switch_config_salvar);

        switchSalvarLoginSenha.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getEditor().putBoolean(getString(R.string.sp_salvar_login_senha), isChecked);
                getEditor().commit();
            }
        });

        Boolean salvar = getSharedPreferences().getBoolean(getString(R.string.sp_salvar_login_senha), false);
        switchSalvarLoginSenha.setChecked(salvar);
    }
}
