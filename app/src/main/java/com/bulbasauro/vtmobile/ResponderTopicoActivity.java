package com.bulbasauro.vtmobile;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bulbasauro.abstracts.AbstractPostar;
import com.bulbasauro.async.misc.Postador;
import com.bulbasauro.erros.ExceptionAntiflood;
import com.bulbasauro.erros.ExceptionMensagemPequena;
import com.bulbasauro.misc.Comando;
import com.bulbasauro.misc.RatingSelector;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ResponderTopicoActivity extends AbstractPostar {

    private String topicoURL = "";
    private String topicoNumero = "";
    private String mensagemEncoded = "";
    private String quote = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_responder_topico);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        topicoURL = getIntent().getExtras().getString("topicoURL");

        quote = getIntent().getExtras().getString("quote");
        String userQuotado = getIntent().getExtras().getString("userQuotado");
        String msg = getIntent().getExtras().getString("mensagemQuotada");

        if (msg != null) {
            if ("texto".equals(quote)) {
                if (userQuotado != null) {
                    msg = "[quote=\"".concat(userQuotado).concat("\"]").concat(msg).concat("[/quote]");
                }
            }
            getEditTextMensagem().setText(msg);
            getEditTextMensagem().setSelection(msg.length());
        }

        construirHeader();
    }

    @Override
    public void abrirPagina(int numeroPagina, Comando comando) {
        if ("edit".equals(quote)) {
            comando = Comando.EDITAR;
        } else {
            comando = Comando.RESPONDER;
        }
        Postador postador = new Postador(ResponderTopicoActivity.this, comando);
        postador.execute();
    }

    private void construirHeader() {
        TextView textViewTitulo = (TextView) findViewById(R.id.textView_responderTopico_titulo);
        TextView textViewDataHora = (TextView) findViewById(R.id.textView_responderTopico_dataHora);
        ImageView imageViewRating = (ImageView) findViewById(R.id.imageView_responderTopico_rating);

        textViewTitulo.setText(getIntent().getExtras().getString("topicoTitulo"));
        textViewDataHora.setText(getIntent().getExtras().getString("publishDate"));
        imageViewRating.setImageResource(RatingSelector.definirRating(getIntent().getExtras().getString("topicoRating")));
    }

    @Override
    public void enviarMensagem() throws ExceptionAntiflood, ExceptionMensagemPequena, UnsupportedEncodingException {
        long af = getSharedPreferences().getLong(getString(R.string.sp_antiflood), System.nanoTime());
        long t = System.nanoTime();
        long elapsed = t - af;
        double emSegundos = ((double) elapsed) / 1000000000; // 1000000000 = 1 segundo
        if (emSegundos > 30 || (elapsed < 500000000)) { // 500000000 = 0.5 segundo
            String mensagem = getEditTextMensagem().getText().toString();

            if (mensagem.length() < 2) {
                throw new ExceptionMensagemPequena(getString(R.string.mensagem_pequena));
            } else {
                mensagemEncoded = URLEncoder.encode(mensagem, "UTF-8");
                mensagemEncoded = mensagemEncoded.replace("+", "%20");

                if (quote.equals("edit")) {
                    topicoNumero = getIntent().getExtras().getString("postNumero");
                } else {
                    topicoNumero = "";
                    for (int i = topicoURL.length(); i > 0; i--) {
                        String caracter = String.valueOf(topicoURL.charAt(i - 1));
                        if ("_".equals(caracter)) {
                            topicoNumero = new StringBuilder(topicoNumero).reverse().toString();
                            break;
                        } else {
                            topicoNumero = topicoNumero + caracter;
                        }
                    }
                }
            }
        } else {
            throw new ExceptionAntiflood(getString(R.string.anti_flood_bloqueado));
        }
    }

    public String getTopicoURL() {
        return topicoURL;
    }

    public String getTopicoNumero() {
        return topicoNumero;
    }

    public String getMensagemEncoded() {
        return mensagemEncoded;
    }

}
