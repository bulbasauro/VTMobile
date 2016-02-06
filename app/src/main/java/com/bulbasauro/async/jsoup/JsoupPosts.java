package com.bulbasauro.async.jsoup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.ListView;
import android.widget.Toast;

import com.bulbasauro.adapters.Posts;
import com.bulbasauro.misc.Comando;
import com.bulbasauro.vtmobile.R;
import com.bulbasauro.vtmobile.TopicoActivity;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created on 18/01/2016.
 */
public class JsoupPosts extends AsyncTask<String, Void, Boolean> {

    private ProgressDialog progressDialog;

    private TopicoActivity activity;
    private ListView listView;
    private String paginaAtual;
    private Comando comando;

    private int totalPaginas;
    private ArrayList<HashMap<String, String>> arrayListPosts;

    private Document document;

    private String bemVindo = "";
    private String url;
    private String topicoTitulo;
    private String dataPublished;

    public JsoupPosts(TopicoActivity activity, ListView listView, int paginaAtual, Comando comando, String url, String rating) {
        this.activity = activity;
        this.listView = listView;
        this.paginaAtual = Integer.toString(paginaAtual);
        this.comando = comando;
        this.url = url.concat(this.paginaAtual);
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
                        .cookies(activity.getCookies())
                        .timeout(6000)
                        .execute();
                activity.getCookies().put("JSESSIONID", res.cookie("JSESSIONID"));
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

            arrayListPosts = new ArrayList<HashMap<String, String>>();

            dataPublished = document.select("div[class=topic-date]").text();
            topicoTitulo = document.select("div[class=titleTopic]").select("h1").text();
            topicoTitulo = topicoTitulo.replace("\"", "").trim();

            String logedUser = document.select("li[class=top-user]").text();

            Elements divPaginacao = document.select("div[id=paginacao]");
            Elements aPaginas = divPaginacao.select("a:not(:has(img))");
            Element aUltimaPagina = aPaginas.last();
            String stringUltimaPagina = "1";
            if (aUltimaPagina != null) {
                stringUltimaPagina = aUltimaPagina.text().toString();
            }
            totalPaginas = Integer.parseInt(stringUltimaPagina);

            if (totalPaginas < Integer.parseInt(paginaAtual)) {
                totalPaginas++;
            }

            // Identifica o primeiro post
            for (Element posts : document.select("div[class=autoClear  topicRow  post]")) {
                Elements divUserDetails = posts.select("div[class=left post-user]");
                String userName = divUserDetails.select("p[class=userNickname").select("a").text();
                String avatarSrc = divUserDetails.select("img[id=avatarImg]").attr("src");
                String userLevel = divUserDetails.select("p[class=userLevel").text().trim();

                String mensagensTemp = posts.select("p[class=descricao]").text().toString();
                String cadastro = posts.select("span[class=data-cadastro]").text().toString();
                String mensagens = mensagensTemp.replace(cadastro, "");
                mensagens = mensagens.trim();

                String assinatura = posts.select("div[class=post-assinatura]").html();

                Elements divPost = posts.select("div[class=right post-buttons]");
                String postCount = divPost.select("div[class=left postCount]").select("a").text();
                String postNumber = divPost.select("div[class=left postCount]").select("a").attr("href");

                String postNumero = "";
                for (int i = postNumber.length(); i > 0; i--) {
                    String caracter = String.valueOf(postNumber.charAt(i - 1));
                    if ("#".equals(caracter)) {
                        postNumero = new StringBuilder(postNumero).reverse().toString();
                        break;
                    } else {
                        postNumero = postNumero + caracter;
                    }
                }

                String postTexto = divPost.select("div[class=texto]").html();

                HashMap<String, String> mapPrincipal = new HashMap<String, String>();
                mapPrincipal.put("postCount", postCount);
                mapPrincipal.put("postNumero", postNumero);
                mapPrincipal.put("postTexto", postTexto);
                mapPrincipal.put("autor", userName);
                mapPrincipal.put("avatar", avatarSrc);
                mapPrincipal.put("urlTopico", url);
                mapPrincipal.put("publishDate", dataPublished);
                mapPrincipal.put("assinatura", assinatura);
                mapPrincipal.put("votingResult", " ");
                mapPrincipal.put("userLevel", userLevel);
                mapPrincipal.put("cadastro", cadastro);
                mapPrincipal.put("mensagens", mensagens);
                this.arrayListPosts.add(mapPrincipal);
            }

            // Identifica os demais posts
            Elements elements = document.select("div[class=autoClear  postRow  post");
            for (Element resposta : elements) {
                Elements divUserDetails = resposta.select("div[class=left post-user]");
                String userName = divUserDetails.select("p[class=userNickname").select("a").text();
                String avatarSrc = divUserDetails.select("img[id=avatarImg]").attr("src");
                String userLevel = divUserDetails.select("p[class=userLevel").text().trim();

                String mensagensTemp = resposta.select("p[class=descricao]").text().toString();
                String cadastro = resposta.select("span[class=data-cadastro]").text().toString();
                String mensagens = mensagensTemp.replace(cadastro, "");
                mensagens = mensagens.trim();

                Elements divPost = resposta.select("div[class=right post-buttons]");
                String postCount = divPost.select("div[class=left postCount]").select("a").text();
                String postNumber = divPost.select("div[class=left postCount]").select("a").attr("href");

                String postNumero = "";
                for (int i = postNumber.length(); i > 0; i--) {
                    String caracter = String.valueOf(postNumber.charAt(i - 1));
                    if ("#".equals(caracter)) {
                        postNumero = new StringBuilder(postNumero).reverse().toString();
                        break;
                    } else {
                        postNumero = postNumero + caracter;
                    }
                }

                String postTexto = divPost.select("div[class=texto]").html();

                String publishDate = resposta.select("div[class=left publishDate]").text().toString();
                publishDate = publishDate.trim();
                publishDate = publishDate.replace("Mensagem publicada em ", "");

                Elements voting = resposta.select("li[class=votingResult]").select("span");
                String votingResult = voting.text().toString().trim();
                if ("".equals(votingResult)) {
                    votingResult = " ";
                }

                String assinatura = resposta.select("div[class=post-assinatura]").html();

                HashMap<String, String> mapPosts = new HashMap<String, String>();
                mapPosts.put("postCount", postCount);
                mapPosts.put("postNumero", postNumero);
                mapPosts.put("postTexto", postTexto);
                mapPosts.put("autor", userName);
                mapPosts.put("avatar", avatarSrc);
                mapPosts.put("urlTopico", url);
                mapPosts.put("publishDate", publishDate);
                mapPosts.put("assinatura", assinatura);
                mapPosts.put("votingResult", votingResult);
                mapPosts.put("userLevel", userLevel);
                mapPosts.put("cadastro", cadastro);
                mapPosts.put("mensagens", mensagens);
                this.arrayListPosts.add(mapPosts);
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
            Boolean estilo = activity.getSharedPreferences().getBoolean(activity.getString(R.string.sp_post_estilo), false);
            Posts posts = new Posts(activity, arrayListPosts, bemVindo, estilo, topicoTitulo);
            listView.setAdapter(posts);

            activity.setPaginaAtual(Integer.parseInt(paginaAtual));
            activity.atualizarToolbarNavegacao(totalPaginas);

            activity.inserirHeaderTopico(topicoTitulo, dataPublished);
            activity.iniciarBotaoResponder();

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
                    definirInterfaceLogado();
                    Toast.makeText(activity, activity.getString(R.string.welcome), Toast.LENGTH_SHORT).show();
                    break;
                case SAIR:
                    activity.setLogado(false);
                    if (activity.getSnackbar() != null) {
                        activity.getSnackbar().dismiss();
                    }
                    definirInterfaceLogado();
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

    private void definirInterfaceLogado() {
        Intent i = new Intent();
        i.putExtra("logado", activity.getLogado());
        i.putExtra("bemVindo", bemVindo);
        activity.setResult(Activity.RESULT_OK, i);
    }

    public int getTotalPaginas() {
        return totalPaginas;
    }

    public String getTopicoTitulo() {
        return topicoTitulo;
    }

    public String getDataPublished() {
        return dataPublished;
    }

    public String getBemVindo() {
        return bemVindo;
    }
}
