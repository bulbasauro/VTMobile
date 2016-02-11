package com.bulbasauro.async.jsoup;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.ListView;
import android.widget.Toast;

import com.bulbasauro.adapters.Topicos;
import com.bulbasauro.vtmobile.MeusTopicosActivity;
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
 */
public class JsoupMeusTopicos extends AsyncTask<String, Void, Boolean> {

    private static final String URL_1 = "http://forum.jogos.uol.com.br/";
    private static final String URL_2 = "http://forum.jogos.uol.com.br/userMessages.jbb";

    private ProgressDialog progressDialog;

    private MeusTopicosActivity activity;
    private Map<String, String> cookies;
    private String userNumber;
    private ListView listViewCriei;
    private ListView listViewParticipei;

    private ArrayList<HashMap<String, String>> arrayListCriei;
    private ArrayList<HashMap<String, String>> arrayListParticipei;

    private Document document = null;

    private String bemVindo = "";

    public JsoupMeusTopicos(MeusTopicosActivity activity, Map<String, String> cookies, String userNumber, ListView listViewCriei, ListView listViewParticipei) {
        this.activity = activity;
        this.cookies = cookies;
        this.userNumber = userNumber;
        this.listViewCriei = listViewCriei;
        this.listViewParticipei = listViewParticipei;
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
            Connection.Response r;
            if (!activity.getLogado()) {
                r = Jsoup.connect(URL_1)
                        .timeout(6000)
                        .execute();
                cookies = new HashMap<String, String>();
            } else {
                r = Jsoup.connect(URL_1)
                        .cookies(cookies)
                        .timeout(6000)
                        .execute();
            }
            cookies.putAll(r.cookies());

            // CRIEI
            String erroLoad = "Este usuário não participou do fórum.";
            do {
                Connection.Response re = Jsoup.connect(URL_2)
                        .cookies(cookies)
                        .data("message", "2")
                        .data("u", userNumber)
                        .method(Connection.Method.GET)
                        .timeout(6000)
                        .execute();
                cookies.putAll(re.cookies());
                document = re.parse();
            } while (document.html().contains("Este usuário não participou do fórum."));
            bemVindo = document.select("li[class=top-user]").text();
            arrayListCriei = new ArrayList<HashMap<String, String>>();
            montarLista(document, arrayListCriei);

            // PARTICIPEI
            Document d = null;
            do {
                Connection.Response res = Jsoup.connect(URL_2)
                        .cookies(cookies)
                        .data("u", userNumber)
                        .data("message", "1")
                        .method(Connection.Method.GET)
                        .timeout(6000)
                        .execute();
                cookies.putAll(res.cookies());
                d = res.parse();
            } while (d.html().contains("Este usuário não participou do fórum."));
            arrayListParticipei = new ArrayList<HashMap<String, String>>();
            montarLista(d, arrayListParticipei);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            Topicos topicosCriei = new Topicos(activity, arrayListCriei);
            listViewCriei.setAdapter(topicosCriei);

            Topicos topicosParticipei = new Topicos(activity, arrayListParticipei);
            listViewParticipei.setAdapter(topicosParticipei);

            // Define as info da página
            activity.setInfo(document);
        } else {
            Toast.makeText(activity, activity.getString(R.string.erro), Toast.LENGTH_SHORT).show();
        }

        if ("".equals(bemVindo)) {
            activity.getSupportActionBar().setTitle(activity.getString(R.string.app_name));
        } else {
            activity.getSupportActionBar().setTitle(bemVindo);
        }
        progressDialog.dismiss();
    }

    private void montarLista(Document document, ArrayList<HashMap<String, String>> arrayList) {
        // Pega a tabela do documento
        for (Element table : document.select("table[id=topics]")) {
            // Pega todas as linhas (tr) da tabela
            for (Element row : table.select("tbody > tr")) {
                HashMap<String, String> map = new HashMap<String, String>();

                // Pega todas as celulas (td) da linha (tr)
                Elements tds = row.select("td");

                Elements divTituloTopico = tds.get(1).select("a");
                String tituloTopico = divTituloTopico.attr("title");
                String urlTopico = divTituloTopico.attr("href");

                // Pega a quantidade de respostas
                String respostas = " - " + tds.get(3).text() + " respostas";

                String lastMessage = tds.get(4).text();
                lastMessage = lastMessage.replace("Por:", "|");
                //String lastUser = spanLastMessage.select("a").last().text();

                // Calcula quantidade de páginas
                String qtdRespostas = tds.get(3).text();
                qtdRespostas = qtdRespostas.replace(".", "");
                int numResp = Integer.parseInt(qtdRespostas);
                if (numResp == 0) {
                    numResp = 1;
                }
                int numPag = numResp / 20;
                if ((numResp % 20) > 0) {
                    numPag = numPag + 1;
                }
                String numeroDePaginas = Integer.toString(numPag);

                // Coloca no map
                map.put("topicoTitulo", tituloTopico);
                map.put("topicoURL", urlTopico);
                map.put("topicoAutor", tds.get(2).text());
                map.put("topicoRespostas", respostas);
                map.put("topicoLastMessage", lastMessage);
                if (numPag > 1) {
                    numeroDePaginas = numeroDePaginas + " páginas";
                } else {
                    numeroDePaginas = numeroDePaginas + " página";
                }
                map.put("numeroDePaginas", numeroDePaginas);
                arrayList.add(map);
            }
        }
    }

    public Document getDocument() {
        return document;
    }
}
