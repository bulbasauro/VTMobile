package com.bulbasauro.async.jsoup;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.ListView;
import android.widget.Toast;

import com.bulbasauro.adapters.MensagensParticularesInbox;
import com.bulbasauro.vtmobile.MensagemParticularActivity;
import com.bulbasauro.vtmobile.R;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 28/01/2016.
 */
public class JsoupMpInbox extends AsyncTask<String, Void, Boolean> {

    private static final String URL_1 = "http://forum.jogos.uol.com.br/";
    private static final String URL_2 = "http://forum.jogos.uol.com.br/inbox.jbb";

    private ProgressDialog progressDialog;

    private MensagemParticularActivity activity;
    private ListView listView;

    private Map<String, String> cookies;

    private ArrayList<HashMap<String, String>> arrayListMensagens;

    private Document document;

    private String bemVindo = "";

    public JsoupMpInbox(MensagemParticularActivity activity, Map<String, String> cookies, ListView listView) {
        this.activity = activity;
        this.cookies = cookies;
        this.listView = listView;
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

            document = resp.parse();

            if (document.html().contains("Aguarde a aprovação do apelido para usar a caixa de Mensagem Particular")) {
                activity.setAprovado(false);
                return false;
            }

            bemVindo = document.select("li[class=top-user]").text();

            arrayListMensagens = new ArrayList<HashMap<String, String>>();

            // Pega painel de informação
            String info = document.select("div[id=mps-content]").html();

            String urlPerfil = document.select("a[class=menu-profile menu-buttons]").attr("href");
            String userNumber = "";
            for (int i = urlPerfil.length(); i > 0; i--) {
                String caracter = String.valueOf(urlPerfil.charAt(i - 1));
                if ("_".equals(caracter)) {
                    userNumber = new StringBuilder(userNumber).reverse().toString();
                    break;
                } else {
                    userNumber = userNumber + caracter;
                }
            }
            activity.setUserNumber(userNumber);

            // Pega a tabela do documento
            for (Element table : document.select("table[id=pmsList]")) {
                // Pega todas as linhas (tr) da tabela
                for (Element row : table.select("tbody > tr:gt(0)")) {
                    HashMap<String, String> map = new HashMap<String, String>();

                    // Pega todas as celulas (td) da linha (tr)
                    Elements tds = row.select("td");

                    // Pega identificador da mensagem para ver se é nova ou não
                    Elements divIdentificador = tds.get(0).select("img");
                    String classe = divIdentificador.attr("class");
                    String novo = "nao";
                    if ("master-sprite sprite-folder-new".equals(classe)) {
                        novo = "sim";
                    }

                    // Pega assunto da mensagem
                    Elements divAssuntoMensagem = tds.get(1).select("a[class=topictitle]");
                    String assuntoMensagem = divAssuntoMensagem.text();
                    String urlMensagem = divAssuntoMensagem.attr("href");

                    // Pega autor da mensagem
                    Elements divAutor = tds.get(2).select("a[class=name]");
                    String nomeAutor = divAutor.text();

                    // Pega data de envio
                    Elements divDataEnvio = tds.get(3).select("span[class=postdetails]");
                    String dataEnvio = divDataEnvio.text();

                    // Coloca no map
                    map.put("novo", novo);
                    map.put("assunto", assuntoMensagem);
                    map.put("URL", urlMensagem);
                    map.put("autor", nomeAutor);
                    map.put("dataEnvio", dataEnvio);
                    arrayListMensagens.add(map);
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            MensagensParticularesInbox mensagens = new MensagensParticularesInbox(activity, arrayListMensagens);
            listView.setAdapter(mensagens);
            activity.setInfo(document);
            activity.setLogado(true);
        } else {
            if (!activity.getAprovado()) {
                bemVindo = activity.getSupportActionBar().getTitle().toString();
                Toast.makeText(activity, activity.getString(R.string.apelido_aprovado), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, activity.getString(R.string.erro), Toast.LENGTH_SHORT).show();
                activity.setLogado(false);
            }
        }
        activity.atualizarToolbarMenu(bemVindo);
        progressDialog.dismiss();
    }

    public Document getDocument() {
        return document;
    }
}
