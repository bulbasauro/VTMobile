package com.bulbasauro.async.jsoup;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import com.bulbasauro.vtmobile.DetalheMpActivity;
import com.bulbasauro.vtmobile.R;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 30/01/2016.
 */
public class JsoupMpDetalhe extends AsyncTask<String, Void, Boolean> {

    private static final String URL_1 = "http://forum.jogos.uol.com.br/";
    private static final String URL_2 = "http://forum.jogos.uol.com.br/inbox.jbb";
    private String URL_FINAL = "http://forum.jogos.uol.com.br/";

    private ProgressDialog progressDialog;

    private DetalheMpActivity activity;

    private Map<String, String> cookies;
    private Map<String, String> dados;

    private Document document;
    private String bemVindo = "";

    public JsoupMpDetalhe(DetalheMpActivity activity, Map<String, String> cookies, String urlMensagem) {
        this.activity = activity;
        this.cookies = cookies;
        URL_FINAL = URL_FINAL.concat(urlMensagem);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(activity.getResources().getString(R.string.carregando));
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    @Override
    protected Boolean doInBackground(String... params) {
        try {
            Connection.Response res = Jsoup.connect(URL_1)
                    .cookies(cookies)
                    .timeout(6000)
                    .execute();
            cookies.putAll(res.cookies());

            Connection.Response resp = Jsoup.connect(URL_2)
                    .cookies(cookies)
                    .timeout(6000)
                    .execute();
            cookies.putAll(resp.cookies());

            Connection.Response respo = Jsoup.connect(URL_FINAL)
                    .cookies(cookies)
                    .timeout(6000)
                    .execute();
            cookies.putAll(respo.cookies());

            document = respo.parse();

            bemVindo = document.select("li[class=top-user]").text();

            dados = new HashMap<String, String>();

            //Pega o nome do autor da mensagem
            String nomeAutor = document.select("p[class=userNickname").select("a").text();
            // Pega o avatar do autor da mensagem
            Elements avatares = document.select("img[id=avatarImg");
            Element avatar = avatares.last();
            String urlAvatar = avatar.attr("src");
            // Pega a mensagem da MP
            String texto = document.select("div[class=texto]").html();

            StringBuilder sbMensagem = new StringBuilder();
            sbMensagem.append("<HTML><HEAD><LINK href=\"style.css\" type=\"text/css\" rel=\"stylesheet\"/></HEAD><body>");
            sbMensagem.append(texto.toString());
            sbMensagem.append("</body></HTML>");
            String mensagem = sbMensagem.toString();

            dados.put("bemVindo", bemVindo);
            dados.put("nomeAutor", nomeAutor);
            dados.put("urlAvatar", urlAvatar);
            dados.put("mensagem", mensagem);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            activity.setMensagem(dados);
            activity.setLogado(true);
        } else {
            Toast.makeText(activity, activity.getString(R.string.erro), Toast.LENGTH_SHORT).show();
            activity.setLogado(false);
        }
        progressDialog.dismiss();
    }

    public Document getDocument() {
        return document;
    }
}
