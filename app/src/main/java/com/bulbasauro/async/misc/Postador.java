package com.bulbasauro.async.misc;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import com.bulbasauro.abstracts.AbstractPostar;
import com.bulbasauro.misc.AntiFlood;
import com.bulbasauro.misc.Comando;
import com.bulbasauro.vtmobile.NovoTopicoActivity;
import com.bulbasauro.vtmobile.R;
import com.bulbasauro.vtmobile.ResponderTopicoActivity;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 23/01/2016.
 */
public class Postador extends AsyncTask<String, Void, Boolean> {

    private AbstractPostar activity;
    private ProgressDialog progressDialog;

    private final static String URL_RESPONDER = "http://forum.jogos.uol.com.br/dwr/call/plaincall/PostFunctions.insertPost.dwr";
    private final static String URL_CRIAR = "http://forum.jogos.uol.com.br/dwr/call/plaincall/PostFunctions.insertTopic.dwr";
    private final static String URL_EDITAR = "http://forum.jogos.uol.com.br/dwr/call/plaincall/PostFunctions.editPost.dwr";
    private final static String URL_EDITAR_TOPICO = "http://forum.jogos.uol.com.br/edit_topic.jbb";
    private Map<String, String> cookies = new HashMap<String, String>();
    private Comando comando;

    public Postador(AbstractPostar activity, Comando comando) {
        this.activity = activity;
        cookies.putAll(activity.getCookies());
        this.comando = comando;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(activity, null, activity.getResources().getString(R.string.postando), true);
        progressDialog.show();
    }

    @Override
    protected Boolean doInBackground(String... params) {
        try {
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

            String url2 = "";
            if (comando.equals(Comando.CRIAR) || comando.equals(Comando.EDITAR_TOPICO)) {
                url2 = "http://forum.jogos.uol.com.br/new_topic.jbb?forum.id=57";
            } else {/* if (comando.equals(Comando.RESPONDER)) {*/
                url2 = "http://forum.jogos.uol.com.br".concat(((ResponderTopicoActivity) activity).getTopicoURL());
                Connection.Response respo = Jsoup.connect(url2)
                        .cookies(cookies)
                        .timeout(6000)
                        .execute();
                cookies.putAll(respo.cookies());
            }

            String jSess = cookies.get("JSESSIONID");
            if (comando.equals(Comando.CRIAR)) {
                Connection.Response res = Jsoup.connect(URL_CRIAR)
                        .data("callCount", "1")
                        .data("page", "/new_topic.jbb?forum.id=57")
                        .data("httpSessionId", jSess)
                        .data("scriptSessionId", "124B764F0DFA2F5599C58174B8362432344")
                        .data("c0-scriptName", "PostFunctions")
                        .data("c0-methodName", "insertTopic")
                        .data("c0-id", "0")
                        .data("c0-param0", "number:57")
                        .data("c0-param1", "string:".concat(((NovoTopicoActivity) activity).getAssuntoEncoded()))
                        .data("c0-param2", "string:".concat(((NovoTopicoActivity) activity).getMensagemEncoded()))
                        .data("batchId", "0")
                        .cookies(cookies)
                        .method(Connection.Method.POST)
                        .timeout(6000)
                        .execute();
                Document document = res.parse();
                return true;
            } else if (comando.equals(Comando.RESPONDER)) {
            /*
            // Gera ScriptSessionId
            String body = Jsoup.connect("http://forum.jogos.uol.com.br/dwr/engine.js").execute().body();
            String prefix = "dwr.engine._origScriptSessionId = \"";
            String postprefix = "\";";
            int preIndex = body.indexOf(prefix);
            int postIndex = body.indexOf(postprefix);
            String origid = body.substring(preIndex + prefix.length(), postIndex);
            String scriptSessionId = origid + String.valueOf((int) (Math.random() * 1000));
            */

                String tURL = ((ResponderTopicoActivity) activity).getTopicoURL();
                String tNum = ((ResponderTopicoActivity) activity).getTopicoNumero();
                String mEnc = ((ResponderTopicoActivity) activity).getMensagemEncoded();

                Connection.Response res = Jsoup.connect(URL_RESPONDER)
                        .data("callCount", "1")
                        .data("page", tURL)
                        .data("httpSessionId", jSess)
                        .data("scriptSessionId", "A1FD9210C91B303C85837C9AD9C21D078")
                        .data("c0-scriptName", "PostFunctions")
                        .data("c0-methodName", "insertPost")
                        .data("c0-id", "0")
                        .data("c0-param0", "number:".concat(tNum))
                        .data("c0-param1", "string:".concat(mEnc))
                        .data("batchId", "0")
                        .cookies(cookies)
                        .method(Connection.Method.POST)
                        .timeout(6000)
                        .execute();
                Document document = res.parse();
                return true;
            } else if (comando.equals(Comando.EDITAR)) {
                String tURL = ((ResponderTopicoActivity) activity).getTopicoURL();
                String tNum = ((ResponderTopicoActivity) activity).getTopicoNumero();
                String mEnc = ((ResponderTopicoActivity) activity).getMensagemEncoded();

                Connection.Response res = Jsoup.connect(URL_EDITAR)
                        .data("callCount", "1")
                        .data("page", tURL)
                        .data("httpSessionId", jSess)
                        .data("scriptSessionId", "EE10D1BC75C8161DEA11ABEDBEF9A535959")
                        .data("c0-scriptName", "PostFunctions")
                        .data("c0-methodName", "editPost")
                        .data("c0-id", "0")
                        .data("c0-param0", "string:".concat(tNum))
                        .data("c0-param1", "string:".concat(mEnc))
                        .data("batchId", "1")
                        .cookies(cookies)
                        .method(Connection.Method.POST)
                        .timeout(6000)
                        .execute();
                Document document = res.parse();
                return true;
            } else if (comando.equals(Comando.EDITAR_TOPICO)) {
                String tNum = ((NovoTopicoActivity) activity).getTopicoNumero();
                String pNum = ((NovoTopicoActivity) activity).getPostNumero();
                String assu = ((NovoTopicoActivity) activity).getAssuntoEncoded();
                String men = ((NovoTopicoActivity) activity).getMensagemEncoded();

                Connection.Response res = Jsoup.connect(URL_EDITAR_TOPICO)
                        .data("post.topic.idTopic", tNum)
                        .data("url", "topic")
                        .data("subject", assu)
                        .data("post.idPost", pNum)
                        .data("pm.text", "")
                        .data("token", "7706150769524931765")
                        .data("addbbcode20", "12")
                        .data("addbbcode18", "#444444")
                        .data("message", men)
                        .cookies(cookies)
                        .method(Connection.Method.POST)
                        .timeout(6000)
                        .execute();
                Document document = res.parse();
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
            new AntiFlood(activity, 30000, 1000).start();
            activity.getEditor().putLong(activity.getString(R.string.sp_antiflood), System.nanoTime());
            activity.getEditor().commit();
            activity.setResult(Activity.RESULT_OK);
            activity.finish();
            Toast.makeText(activity, activity.getString(R.string.sucesso), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(activity, activity.getString(R.string.erro), Toast.LENGTH_SHORT).show();
        }
    }

}
