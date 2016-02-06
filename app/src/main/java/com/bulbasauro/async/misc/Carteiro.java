package com.bulbasauro.async.misc;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import com.bulbasauro.abstracts.AbstractActivity;
import com.bulbasauro.misc.Selo;
import com.bulbasauro.vtmobile.DetalheMpActivity;
import com.bulbasauro.vtmobile.MandarMpActivity;
import com.bulbasauro.vtmobile.R;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 01/02/2016.
 */
public class Carteiro extends AsyncTask<String, Void, Boolean> {

    private AbstractActivity activity;
    private ProgressDialog progressDialog;

    private Map<String, String> cookies = new HashMap<String, String>();
    private Selo selo;
    private Boolean out = false;
    private String idPm;

    private Boolean apelidoAprovado = true;

    public Carteiro(AbstractActivity activity, Selo selo) {
        this.activity = activity;
        this.selo = selo;
        cookies.putAll(activity.getCookies());
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        String msg;
        switch (selo) {
            case RESPONDER:
                msg = activity.getResources().getString(R.string.enviando);
                break;
            case APAGAR:
                msg = activity.getResources().getString(R.string.apagando);
                break;
            default:
                msg = activity.getResources().getString(R.string.carregando);
                break;
        }
        progressDialog = ProgressDialog.show(activity, null, msg, true);
        progressDialog.show();
    }

    @Override
    protected Boolean doInBackground(String... params) {
        try {
            Connection.Response r = Jsoup.connect("http://forum.jogos.uol.com.br/")
                    .cookies(cookies)
                    .timeout(6000)
                    .execute();
            cookies.putAll(r.cookies());

            Connection.Response re = Jsoup.connect("http://forum.jogos.uol.com.br/vale-tudo_f_57")
                    .cookies(cookies)
                    .timeout(6000)
                    .execute();
            cookies.putAll(re.cookies());

            Connection.Response res = Jsoup.connect("http://forum.jogos.uol.com.br/inbox.jbb")
                    .cookies(cookies)
                    .timeout(6000)
                    .execute();

            Document docVer = res.parse();
            if (docVer.html().contains("Aguarde a aprovação do apelido para usar a caixa de Mensagem Particular")) {
                apelidoAprovado = false;
                return false;
            }

            if (selo.equals(Selo.APAGAR)) {
                String urlApagarEntrada;
                String urlApagar;
                if (out) {
                    urlApagarEntrada = "http://forum.jogos.uol.com.br/outbox.jbb";
                    urlApagar = "http://forum.jogos.uol.com.br/deleteSelectedOutbox.jbb";
                } else {
                    urlApagarEntrada = "http://forum.jogos.uol.com.br/inbox.jbb";
                    urlApagar = "http://forum.jogos.uol.com.br/deleteSelectedInbox.jbb";
                }
                Connection.Response resp = Jsoup.connect(urlApagarEntrada)
                        .cookies(cookies)
                        .timeout(6000)
                        .execute();
                cookies.putAll(resp.cookies());
                Document doc = resp.parse();
                String token = doc.select("input[name=token]").attr("value");

                Connection.Response resposta = Jsoup.connect(urlApagar)
                        .data("idPm", idPm)
                        .data("token", token)
                        .cookies(cookies)
                        .method(Connection.Method.POST)
                        .timeout(6000)
                        .execute();
                cookies.putAll(resposta.cookies());
                Document documento = resposta.parse();

                return true;
            } else if (selo.equals(Selo.RESPONDER)) {
                Connection.Response resp = Jsoup.connect("http://forum.jogos.uol.com.br/new.jbb")
                        .cookies(cookies)
                        .timeout(6000)
                        .execute();
                cookies.putAll(resp.cookies());
                Document doc = resp.parse();
                String token = doc.select("input[name=token]").attr("value");

                String destinatario = ((MandarMpActivity) activity).getDestinatario();
                String assunto = ((MandarMpActivity) activity).getAssunto();
                String mensagem = ((MandarMpActivity) activity).getMensagem();

                Connection.Response resposta = Jsoup.connect("http://forum.jogos.uol.com.br/send.jbb")
                        .data("username", destinatario)
                        .data("pm.topic", assunto)
                        .data("message", mensagem)
                        .data("token", token)
                        .data("userId", "")
                        .data("pm.text", "")
                        .data("addbbcode20", "12")
                        .data("addbbcode18", "#444444")
                        .cookies(cookies)
                        .method(Connection.Method.POST)
                        .timeout(6000)
                        .execute();
                cookies.putAll(resposta.cookies());
                Document documento = resposta.parse();

                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        progressDialog.dismiss();
        if (result) {
            activity.setResult(Activity.RESULT_OK);
            if (selo.equals(Selo.APAGAR)) {
                if (activity instanceof DetalheMpActivity) {
                    activity.finish();
                } else {
                    activity.iniciarLoginSharedPreferences(1);
                }
            } else {
                activity.finish();
            }
            Toast.makeText(activity, activity.getString(R.string.sucesso), Toast.LENGTH_SHORT).show();
        } else {
            if (apelidoAprovado) {
                Toast.makeText(activity, activity.getString(R.string.erro), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, activity.getString(R.string.apelido_aprovado), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Boolean getOut() {
        return out;
    }

    public void setOut(Boolean out) {
        this.out = out;
    }

    public String getIdPm() {
        return idPm;
    }

    public void setIdPm(String idPm) {
        this.idPm = idPm;
    }
}
