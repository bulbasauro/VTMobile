package com.bulbasauro.vtmobile;

import android.os.Bundle;
import android.widget.EditText;

import com.bulbasauro.abstracts.AbstractPostarAssunto;
import com.bulbasauro.async.misc.Carteiro;
import com.bulbasauro.erros.ExceptionAntiflood;
import com.bulbasauro.erros.ExceptionMensagemPequena;
import com.bulbasauro.misc.Comando;
import com.bulbasauro.misc.Selo;

import java.io.UnsupportedEncodingException;

public class MandarMpActivity extends AbstractPostarAssunto {

    private EditText editTextDestinatario;
    private String destinatario;
    private String assunto;
    private String mensagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mandar_mp);

        destinatario = getIntent().getExtras().getString("destinatario");
        assunto = getIntent().getExtras().getString("assunto");
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        editTextDestinatario = (EditText) findViewById(R.id.editText_mp_destinatario);
        editTextDestinatario.setText(destinatario);

        getEditTextAssunto().setText(assunto);

        if ("".equals(getSupportActionBar().getTitle())) {
            getSupportActionBar().setTitle(getString(R.string.app_name));
        }
    }

    @Override
    public void abrirPagina(int numeroPagina, Comando comando) {
        Carteiro carteiro = new Carteiro(MandarMpActivity.this, Selo.RESPONDER);
        carteiro.execute();
    }

    @Override
    public void enviarMensagem() throws ExceptionAntiflood, ExceptionMensagemPequena, UnsupportedEncodingException {
        assunto = getEditTextAssunto().getText().toString();
        destinatario = editTextDestinatario.getText().toString();
        mensagem = getEditTextMensagem().getText().toString();
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }
}
