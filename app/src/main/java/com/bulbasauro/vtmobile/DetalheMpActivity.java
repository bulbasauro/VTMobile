package com.bulbasauro.vtmobile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bulbasauro.abstracts.AbstractActivity;
import com.bulbasauro.async.jsoup.JsoupMpDetalhe;
import com.bulbasauro.async.misc.Carteiro;
import com.bulbasauro.misc.Comando;
import com.bulbasauro.misc.Selo;
import com.bumptech.glide.Glide;

import java.util.Map;

public class DetalheMpActivity extends AbstractActivity {

    private String nomeUser;
    private String urlMensagem;
    private String autor;
    private String assunto;
    private Boolean out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        nomeUser = getIntent().getExtras().getString("userName");

        out = getIntent().getExtras().getBoolean("out", false);
        Boolean estilo = getSharedPreferences().getBoolean(getString(R.string.sp_post_estilo), false);
        if (estilo && out) {
            setContentView(R.layout.activity_detalhe_mp_social);
        } else {
            setContentView(R.layout.activity_detalhe_mp_default);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(nomeUser);

        iniciarViews();
        iniciarBotoes();

        iniciarLoginSharedPreferences(1);
    }

    private void iniciarBotoes() {
        iniciarBotaoApagar();
        iniciarBotaoResponder();
    }

    private void iniciarBotaoApagar() {
        TextView textViewApagar = (TextView) findViewById(R.id.textView_toolbar_mp_detalhe_apagar);
        textViewApagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirDialogApagar();
            }
        });
    }

    private void abrirDialogApagar() {
        View view = getLayoutInflater().inflate(R.layout.dialog_apagar_mp, null);

        AlertDialog.Builder dialog = new AlertDialog.Builder(DetalheMpActivity.this);
        dialog.setView(view);
        dialog.setPositiveButton(R.string.diag_confirmar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                Carteiro carteiro = new Carteiro(DetalheMpActivity.this, Selo.APAGAR);
                carteiro.setOut(out);
                String idPm;
                if (out) {
                    idPm = urlMensagem.replace("readOut.jbb?pm.id=", "");
                } else {
                    idPm = urlMensagem.replace("readIn.jbb?pm.id=", "");
                }
                carteiro.setIdPm(idPm);
                carteiro.execute();
            }
        });
        dialog.setNegativeButton(R.string.diag_cancelar, null);
        dialog.show();
    }

    private void iniciarBotaoResponder() {
        TextView textViewResponder = (TextView) findViewById(R.id.textView_toolbar_mp_detalhe_responder);
        textViewResponder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String respostaAssunto = "RE: ".concat(assunto);

                Intent intent = new Intent(DetalheMpActivity.this, MandarMpActivity.class);
                intent.putExtra("assunto", respostaAssunto);
                intent.putExtra("destinatario", autor);
                intent.putExtra("userName", nomeUser);
                startActivity(intent);

                finish();
            }
        });
    }

    private void iniciarViews() {
        TextView textViewAssunto = (TextView) findViewById(R.id.textView_mp_detalhe_assunto);
        TextView textViewAutor = (TextView) findViewById(R.id.textView_mp_detalhe_autor);
        TextView textViewDataEnvio = (TextView) findViewById(R.id.textView_mp_detalhe_dataEnvio);

        urlMensagem = getIntent().getExtras().getString("URL");
        assunto = getIntent().getExtras().getString("assunto");
        autor = getIntent().getExtras().getString("autor");
        String dataEnvio = getIntent().getExtras().getString("dataEnvio");

        textViewAssunto.setText(assunto);
        textViewAutor.setText(autor);
        textViewDataEnvio.setText(dataEnvio);
    }

    @Override
    public void abrirPagina(int numeroPagina, Comando comando) {
        JsoupMpDetalhe mpDetalhe = new JsoupMpDetalhe(DetalheMpActivity.this, getCookies(), urlMensagem);
        mpDetalhe.execute();
    }

    public void setMensagem(Map<String, String> dados) {
        String userLogado = dados.get("bemVindo");
        String autor = dados.get("nomeAutor");
        String urlAvatar = dados.get("urlAvatar");
        String mensagem = dados.get("mensagem");

        ImageView imageView = (ImageView) findViewById(R.id.imageView_mp_detalhe_avatar);
        Glide.with(DetalheMpActivity.this)
                .load(urlAvatar)
                .placeholder(R.drawable.nopic)
                .error(R.drawable.nopic)
                .fitCenter()
                .into(imageView);

        TextView textView = (TextView) findViewById(R.id.textView_mp_detalhe_corpo_autor);
        textView.setText(autor);

        WebView webView = (WebView) findViewById(R.id.webView_mp_detalhe_corpo);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadDataWithBaseURL("file:///android_asset/", mensagem, "txt/html", "utf-8", "");
    }
}
