package com.bulbasauro.vtmobile;

import android.os.Bundle;
import android.widget.Toast;

import com.bulbasauro.abstracts.AbstractPostarAssunto;
import com.bulbasauro.async.misc.Postador;
import com.bulbasauro.erros.ExceptionAntiflood;
import com.bulbasauro.erros.ExceptionMensagemPequena;
import com.bulbasauro.misc.Comando;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class NovoTopicoActivity extends AbstractPostarAssunto {

    private String mensagemEncoded;
    private String assuntoEncoded;

    private String quote;
    private String topicoNumero;
    private String postNumero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_topico);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getEditTextAssunto().setText(getIntent().getExtras().getString("titulo"));
        getEditTextMensagem().setText(getIntent().getExtras().getString("mensagemQuotada"));

        topicoNumero = getIntent().getExtras().getString("topicoNumero");
        postNumero = getIntent().getExtras().getString("postNumero");

        quote = getIntent().getExtras().getString("quote");
    }

    @Override
    public void abrirPagina(int numeroPagina, Comando comando) {
        Postador postador;
        if ("topico".equals(quote)) {
            postador = new Postador(NovoTopicoActivity.this, Comando.EDITAR_TOPICO);
        } else {
            postador = new Postador(NovoTopicoActivity.this, Comando.CRIAR);
        }
        postador.execute();
    }

    @Override
    public void enviarMensagem() throws ExceptionAntiflood, ExceptionMensagemPequena, UnsupportedEncodingException {
        long af = getSharedPreferences().getLong(getString(R.string.sp_antiflood), System.nanoTime());
        long t = System.nanoTime();
        long elapsed = t - af;
        double emSegundos = ((double) elapsed) / 1000000000; // 1000000000 = 1 segundo
        if ("topico".equals(quote)) {
            String assunto = getEditTextAssunto().getText().toString();
            String mensagem = getEditTextMensagem().getText().toString();

            verificarTextos(assunto, mensagem);

            assuntoEncoded = assunto;
            mensagemEncoded = mensagem;
        } else {
            if (emSegundos > 30 || (elapsed < 500000000)) { // 500000000 = 0.5 segundo
                String assunto = getEditTextAssunto().getText().toString();
                String mensagem = getEditTextMensagem().getText().toString();

                verificarTextos(assunto, mensagem);

                mensagemEncoded = URLEncoder.encode(mensagem, "UTF-8");
                mensagemEncoded = mensagemEncoded.replace("+", "%20");

                assuntoEncoded = URLEncoder.encode(assunto, "UTF-8");
                assuntoEncoded = assuntoEncoded.replace("+", "%20");
            } else {
                throw new ExceptionAntiflood(getString(R.string.anti_flood_bloqueado));
            }
        }
    }

    private void verificarTextos(String assunto, String mensagem) throws ExceptionMensagemPequena {
        if (assunto.length() < 2) {
            Toast.makeText(NovoTopicoActivity.this, getString(R.string.assunto_pequeno), Toast.LENGTH_SHORT).show();
            throw new ExceptionMensagemPequena(getString(R.string.mensagem_pequena));
        }
        if (mensagem.length() < 2) {
            Toast.makeText(NovoTopicoActivity.this, getString(R.string.mensagem_pequena), Toast.LENGTH_SHORT).show();
            throw new ExceptionMensagemPequena(getString(R.string.mensagem_pequena));
        }
    }

    public String getAssuntoEncoded() {
        return assuntoEncoded;
    }

    public void setAssuntoEncoded(String assuntoEncoded) {
        this.assuntoEncoded = assuntoEncoded;
    }

    public String getMensagemEncoded() {
        return mensagemEncoded;
    }

    public void setMensagemEncoded(String mensagemEncoded) {
        this.mensagemEncoded = mensagemEncoded;
    }

    public String getTopicoNumero() {
        return topicoNumero;
    }

    public void setTopicoNumero(String topicoNumero) {
        this.topicoNumero = topicoNumero;
    }

    public String getPostNumero() {
        return postNumero;
    }

    public void setPostNumero(String postNumero) {
        this.postNumero = postNumero;
    }
}
