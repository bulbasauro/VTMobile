package com.bulbasauro.vtmobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import com.bulbasauro.misc.CustomOnItemSelectedListener;

public class ConfiguracoesActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private Spinner spinnerToqueSimples;
    private Spinner spinnerToqueDuplo;
    private Switch switchEstilo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.config));

        sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        inicializarSpinnerToques();
        inicializarSwitch();
    }

    public void inicializarSpinnerToques() {
        spinnerToqueSimples = (Spinner) findViewById(R.id.spinner_toques_simples);
        spinnerToqueSimples.setOnItemSelectedListener(new CustomOnItemSelectedListener(editor, getString(R.string.sp_toque_simples)));

        ArrayAdapter<CharSequence> dataAdapterSimples = ArrayAdapter.createFromResource(this, R.array.spinner_config_toques, R.layout.spinner_toques);
        dataAdapterSimples.setDropDownViewResource(R.layout.spinner_toques);
        spinnerToqueSimples.setAdapter(dataAdapterSimples);

        int selected = sharedPreferences.getInt(getString(R.string.sp_toque_simples), 1);
        spinnerToqueSimples.setSelection(selected);


        spinnerToqueDuplo = (Spinner) findViewById(R.id.spinner_toques_duplo);
        spinnerToqueDuplo.setOnItemSelectedListener(new CustomOnItemSelectedListener(editor, getString(R.string.sp_toque_duplo)));

        ArrayAdapter<CharSequence> dataAdapterDuplo = ArrayAdapter.createFromResource(this, R.array.spinner_config_toques2, R.layout.spinner_toques);
        dataAdapterDuplo.setDropDownViewResource(R.layout.spinner_toques);
        spinnerToqueDuplo.setAdapter(dataAdapterDuplo);

        int selectedDuplo = sharedPreferences.getInt(getString(R.string.sp_toque_duplo), 0);
        spinnerToqueDuplo.setSelection(selectedDuplo);
    }

    public void inicializarSwitch() {
        switchEstilo = (Switch) findViewById(R.id.switch_config_estilo);

        switchEstilo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(getString(R.string.sp_post_estilo), isChecked);
                editor.commit();
            }
        });

        Boolean estiloSocial = sharedPreferences.getBoolean(getString(R.string.sp_post_estilo), false);
        switchEstilo.setChecked(estiloSocial);
    }
}
