package com.bulbasauro.async.misc;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import com.bulbasauro.misc.Quote;
import com.bulbasauro.vtmobile.R;
import com.bulbasauro.vtmobile.TopicoActivity;

import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 27/01/2016.
 */
public class Quotador extends AsyncTask<String, Void, Boolean> {

    private TopicoActivity activity;
    private ProgressDialog progressDialog;

    private final static String URL_QUOTAR = "http://forum.jogos.uol.com.br/dwr/call/plaincall/PostFunctions.getPost.dwr";
    private Map<String, String> cookies = new HashMap<String, String>();
    private String postNumero;
    private String mensagem;
    private String user;
    private Quote quote;

    public Quotador(TopicoActivity activity, String postNumero, Quote quote) {
        this.activity = activity;
        this.postNumero = postNumero;
        this.quote = quote;
        cookies.putAll(activity.getCookies());
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (Quote.EDIT.equals(quote) || Quote.TOPICO.equals(quote)) {
            progressDialog = ProgressDialog.show(activity, null, activity.getResources().getString(R.string.editando), true);
        } else {
            progressDialog = ProgressDialog.show(activity, null, activity.getResources().getString(R.string.quotando), true);
        }
        progressDialog.show();
    }

    @Override
    protected Boolean doInBackground(String... params) {
        try {
            if (Quote.AVATAR.equals(quote)) {
                mensagem = "[img]".concat(postNumero).concat("[/img]");
                return true;
            } else {
                Connection.Response re = Jsoup.connect("http://forum.jogos.uol.com.br/")
                        .cookies(cookies)
                        .timeout(6000)
                        .execute();
                cookies.putAll(re.cookies());

                Connection.Response resp = Jsoup.connect("http://forum.jogos.uol.com.br/vale-tudo_f_57")
                        .cookies(cookies)
                        .timeout(6000)
                        .execute();
                cookies.putAll(resp.cookies());

                Connection.Response respo = Jsoup.connect("http://forum.jogos.uol.com.br".concat(activity.getIntent().getExtras().getString("topicoURL")))
                        .cookies(cookies)
                        .timeout(6000)
                        .execute();
                cookies.putAll(respo.cookies());

                String jSess = cookies.get("JSESSIONID");
                Connection.Response res = Jsoup.connect(URL_QUOTAR)
                        .data("callCount", "1")
                        .data("page", activity.getIntent().getExtras().getString("topicoURL"))
                        .data("httpSessionId", jSess)
                        .data("scriptSessionId", "CC7D03789B49AD2D456266CAD89AC636828\n")
                        .data("c0-scriptName", "PostFunctions")
                        .data("c0-methodName", "getPost")
                        .data("c0-id", "0")
                        .data("c0-param0", "string:".concat(postNumero))
                        .data("batchId", "1")
                        .cookies(cookies)
                        .method(Connection.Method.POST)
                        .timeout(6000)
                        .execute();
                Document document = res.parse();
                String tx = document.body().text();
                String msg = tx.substring(tx.indexOf(":\\\""), tx.indexOf("error"));
                msg = msg.substring(3, msg.length() - 5);
                msg = StringEscapeUtils.unescapeJava(msg);
                msg = URLDecoder.decode(msg, "UTF-8");
                msg = msg.replace("\\\"", "\"");
                msg = msg.replace("\\\\", "\\");
                msg = msg.replace("\\/", "/");
                msg = msg.replace("\\\\\\", "");
                msg = msg.replace("\\n", " \n ");
                msg = msg.replace("\\r", "");
                mensagem = msg;
                String assinatura = "userName\\\":\\\"";
                int assinaturaPlace = tx.indexOf(assinatura);
                String autor = tx.substring(assinaturaPlace + assinatura.length(), tx.lastIndexOf("\\\"}\");"));
                autor = StringEscapeUtils.unescapeJava(autor);
                autor = URLDecoder.decode(autor, "UTF-8");
                autor = autor.replace("\\\\", "\\");
                autor = autor.replace("\\/", "/");
                user = autor;
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        progressDialog.dismiss();
        if (result) {
            switch (quote) {
                case TEXTO:
                    activity.enviarDadosResposta(user, mensagem, "texto", null);
                    break;
                case EDIT:
                    activity.enviarDadosResposta(user, mensagem, "edit", postNumero);
                    break;
                case TOPICO:
                    activity.enviarDadosResposta(user, mensagem, "topico", postNumero);
                    break;
                case AVATAR:
                    activity.enviarDadosResposta(null, mensagem, "avatar", null);
                    break;
                default:
                    Toast.makeText(activity, activity.getString(R.string.erro), Toast.LENGTH_SHORT).show();
                    break;
            }
        } else {
            Toast.makeText(activity, activity.getString(R.string.erro), Toast.LENGTH_SHORT).show();
        }
    }

}
