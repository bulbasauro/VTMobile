package com.bulbasauro.async.jsoup;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.ListView;
import android.widget.Toast;

import com.bulbasauro.adapters.Topicos;
import com.bulbasauro.misc.Comando;
import com.bulbasauro.vtmobile.MainActivity;
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
 * Created on 18/01/2016.
 */
public class JsoupTopicos extends AsyncTask<String, Void, Boolean> {

    private String url = "http://forum.jogos.uol.com.br/vale-tudo_f_57?page=";

    private ProgressDialog progressDialog;

    private MainActivity activity;
    private ListView listView;
    private String paginaAtual;
    private Comando comando;

    private Map<String, String> cookies;

    private int totalPaginas;
    private ArrayList<HashMap<String, String>> arrayListTopicos;

    private Document document;

    private String bemVindo = "";

    public JsoupTopicos(MainActivity activity, Map<String, String> cookies, ListView listView, int paginaAtual, Comando comando) {
        this.activity = activity;
        this.cookies = cookies;
        this.listView = listView;
        this.paginaAtual = Integer.toString(paginaAtual);
        this.comando = comando;

        url = url.concat(this.paginaAtual);
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
            Connection.Response res;
            if (activity.getLogado()) {
                res = Jsoup.connect(url)
                        .cookies(cookies)
                        .timeout(6000)
                        .execute();
            } else {
                res = Jsoup.connect(url)
                        .timeout(6000)
                        .execute();
            }

            document = res.parse();

            bemVindo = document.select("li[class=top-user]").text();
            String mps = document.select("a[id=private-messages]").attr("title");
            if (!"".equals(mps) && !"Mensagens Particulares(0)".equals(mps)) {
                activity.criarAvisoMP(mps);
            }

            arrayListTopicos = new ArrayList<HashMap<String, String>>();

            // Pega a div de paginação
            Elements divPaginacao = document.select("div[id=paginacao]");
            // Pega todos os links de paginação
            Elements aPaginas = divPaginacao.select("a[rel=nofollow]");
            // Pega o último link de paginação (última página) e transforma em string
            String stringUltimaPagina = aPaginas.last().text().toString();
            // Transforma em int a string de última página
            totalPaginas = Integer.parseInt(stringUltimaPagina);

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
            for (Element table : document.select("table[id=topics]")) {
                // Pega todas as linhas (tr) da tabela
                for (Element row : table.select("tbody > tr")) {
                    HashMap<String, String> map = new HashMap<String, String>();

                    // Pega todas as celulas (td) da linha (tr)
                    Elements tds = row.select("td");

                    Elements divTituloTopico = tds.get(1).select("a");
                    String tituloTopico = divTituloTopico.attr("title");
                    String urlTopico = divTituloTopico.attr("href");

                    Elements spanQuickPaging = tds.get(1).select("span[class=quickPaging]");
                    Elements paginas = spanQuickPaging.select("a");
                    String numeroDePaginas = "1";
                    int numPag = Integer.parseInt(numeroDePaginas);
                    if (paginas != null && !paginas.isEmpty() && paginas.size() != 0) {
                        numeroDePaginas = paginas.last().text();
                        numPag = Integer.parseInt(numeroDePaginas);
                    }

                    // Pega a quantidade de respostas
                    String respostas = " - " + tds.get(3).text() + " respostas";

                    Elements spanLastMessage = tds.get(6).select("span[class=lastmessage]");
                    String lastMessage = spanLastMessage.text().toString();
                    lastMessage = lastMessage.replace("Por:", "|");
                    String lastUser = spanLastMessage.select("a").last().text();

                    // Pega o rating
                    Element imgRating = tds.select("span[class=rating").first().select("img").first();
                    String stringRating = imgRating.attr("title").toString();
                    String sub = stringRating.substring(stringRating.length() - 3);

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
                    map.put("topicoRating", sub);
                    arrayListTopicos.add(map);
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
            Topicos topicos = new Topicos(activity, arrayListTopicos);
            listView.setAdapter(topicos);

            int pa = Integer.parseInt(paginaAtual);
            activity.setPaginaAtual(pa);
            activity.salvarPagina(pa);
            activity.atualizarToolbarNavegacao(totalPaginas);

            switch (comando) {
                case ABRIR:
                    break;
                case ATUALIZAR:
                    Toast.makeText(activity, activity.getString(R.string.refresh), Toast.LENGTH_SHORT).show();
                    break;
                case VALIDAR:
                    activity.setLogado(true);
                    break;
                case LOGIN:
                    activity.setLogado(true);
                    Toast.makeText(activity, activity.getString(R.string.welcome), Toast.LENGTH_SHORT).show();
                    break;
                case SAIR:
                    activity.setLogado(false);
                    if (activity.getSnackbar() != null) {
                        activity.getSnackbar().dismiss();
                    }
                    Toast.makeText(activity, activity.getString(R.string.bye), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(activity, activity.getString(R.string.erro), Toast.LENGTH_SHORT).show();
                    break;
            }
        } else {
            Toast.makeText(activity, activity.getString(R.string.erro), Toast.LENGTH_SHORT).show();
        }
        activity.atualizarToolbarMenu(bemVindo);
        progressDialog.dismiss();
    }

    public int getTotalPaginas() {
        return totalPaginas;
    }
}
