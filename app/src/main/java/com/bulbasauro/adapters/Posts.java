package com.bulbasauro.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bulbasauro.async.misc.Quotador;
import com.bulbasauro.misc.Quote;
import com.bulbasauro.utils.NumberSelector;
import com.bulbasauro.vtmobile.MandarMpActivity;
import com.bulbasauro.vtmobile.MeusTopicosActivity;
import com.bulbasauro.vtmobile.R;
import com.bulbasauro.vtmobile.TopicoActivity;
import com.bumptech.glide.Glide;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created on 18/01/2016.
 */
public class Posts extends BaseSwipeAdapter {

    private TopicoActivity activity;
    private ArrayList<HashMap<String, String>> data;
    private HashMap<String, String> resultp = new HashMap<String, String>();

    private String userNameLogado;
    private String topicoTitulo;
    private Boolean estilo;

    private int[] colors = new int[]{0xffffffff, 0xffcccccc};

    public Posts(TopicoActivity activity, ArrayList<HashMap<String, String>> data, String userNameLogado, Boolean estilo, String topicoTitulo) {
        this.activity = activity;
        this.data = data;
        this.userNameLogado = userNameLogado;
        this.topicoTitulo = topicoTitulo;
        this.estilo = estilo;
    }

    @Override
    public int getSwipeLayoutResourceId(int i) {
        return R.id.swipeLayout_topico_posts;
    }

    @Override
    public View generateView(final int position, ViewGroup parent) {
        View itemView;
        resultp = data.get(position);
        int tipo = getItemViewType(position);
        switch (tipo) {
            case 1:
                itemView = LayoutInflater.from(activity).inflate(R.layout.listview_posts_social, null);
                break;
            case 2:
                itemView = LayoutInflater.from(activity).inflate(R.layout.listview_posts_autor, null);
                break;
            default:
                itemView = LayoutInflater.from(activity).inflate(R.layout.listview_posts_default, null);
                break;
        }
        return itemView;
    }

    @Override
    public int getItemViewType(int position) {
        int layout = 0;
        resultp = data.get(position);
        String postNumber = resultp.get("postCount");
        String userMenuCompleto = activity.getSupportActionBar().getTitle().toString();
        String userMenu = userMenuCompleto.replace("Olá, ", "");
        String autor = resultp.get("autor");

        if (estilo && userNameLogado.equals(userMenuCompleto) && autor.equals(userMenu)) {
            layout = 1;
        } else {
            if (userNameLogado.equals(userMenuCompleto) && autor.equals(userMenu)) {
                layout = 2;
            } else {
                layout = 0;
            }
        }

        return layout;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public void fillValues(int position, View view) {
        ImageView avatar = (ImageView) view.findViewById(R.id.imageView_topico_avatar);
        TextView userName = (TextView) view.findViewById(R.id.textView_topico_userName);
        TextView postCount = (TextView) view.findViewById(R.id.textView_topico_postNumber);
        WebView post = (WebView) view.findViewById(R.id.webView_topico_texto);
        TextView dataHora = (TextView) view.findViewById(R.id.textView_topico_publishDate);
        TextView avaliacao = (TextView) view.findViewById(R.id.textView_topico_postAvaliacao);

        resultp = data.get(position);
        String urlAvatar = resultp.get("avatar");
        urlAvatar = urlAvatar.replace(" ", "%20");

        // User Info
        ImageView wrapperAvatar = (ImageView) view.findViewById(R.id.imageView_wrapper_posts_avatar);
        TextView wrapperMensagens = (TextView) view.findViewById(R.id.textView_wrapper_posts_mensagens);
        TextView wrapperCadastro = (TextView) view.findViewById(R.id.textView_wrapper_posts_cadastro);
        TextView wrapperNivel = (TextView) view.findViewById(R.id.textView_wrapper_posts_nivel);
        TextView wrapperUserName = (TextView) view.findViewById(R.id.textView_wrapper_posts_userName);
        WebView wrapperAssinatura = (WebView) view.findViewById(R.id.webView_wrapper_posts_assinatura);
        Glide.with(activity)
                .load(urlAvatar)
                .placeholder(R.drawable.nopic)
                .error(R.drawable.nopic)
                .fitCenter()
                .into(wrapperAvatar);
        wrapperMensagens.setText(resultp.get("mensagens"));
        wrapperCadastro.setText(resultp.get("cadastro"));
        String userLevel = resultp.get("userLevel");
        wrapperNivel.setText(userLevel);
        wrapperUserName.setText(resultp.get("autor"));
        String assinatura = resultp.get("assinatura");

        String votingResult = resultp.get("votingResult");
        avaliacao.setText(votingResult);
        if ("+".equals(votingResult.substring(0, 1))) {
            avaliacao.setTextColor(activity.getResources().getColor(R.color.colorPositivo));
        } else {
            avaliacao.setTextColor(activity.getResources().getColor(R.color.colorNegativo));
        }

        userName.setText(resultp.get("autor"));
        Glide.with(activity)
                .load(urlAvatar)
                .placeholder(R.drawable.nopic)
                .error(R.drawable.nopic)
                .fitCenter()
                .into(avatar);
        String txtPost = resultp.get("postTexto");
        String urlTopico = resultp.get("urlTopico");

        String css;
        switch (activity.getTema()) {
            case 0:
                css = "style.css";
                break;
            case 1:
                css = "style_laranja.css";
                break;
            default:
                css = "style.css";
                break;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<HTML><HEAD><LINK href=\"" + css + "\" type=\"text/css\" rel=\"stylesheet\"/></HEAD><body>");
        sb.append(txtPost.toString());
        sb.append("</body></HTML>");
        String postagem = sb.toString();

        if (postagem.contains("value=\"http://www.youtube.com")) {
            String ID = Jsoup.parse(postagem)
                    .select("center > object > param")
                    .attr("abs:value")
                    .replace("http://www.youtube.com/v/", "")
                    .replace("&hl=en&fs=1", "");

            postagem = postagem.replace(postagem.substring(postagem.indexOf("<center>"), postagem.lastIndexOf("</center>")), "<center><a href=\"https://www.youtube.com/watch?v=" + ID + "\"><img src=\"http://img.youtube.com/vi/" + ID + "/0.jpg\"></a></center>");
        }

        if (postagem.contains("http://forum.jogos.uol.com.br/")) {

        }

        post.getSettings().setJavaScriptEnabled(true);
        //JavaScript javaScript = new JavaScript(activity);
        //post.addJavascriptInterface(javaScript, "JavaScript");
        post.loadDataWithBaseURL("file:///android_asset/", postagem, "txt/html", "utf-8", "");
        postCount.setText(resultp.get("postCount"));

        StringBuilder sbAss = new StringBuilder();
        sbAss.append("<HTML><HEAD><LINK href=\"" + css + "\" type=\"text/css\" rel=\"stylesheet\"/></HEAD><body>");
        sbAss.append(assinatura.toString());
        sbAss.append("</body></HTML>");
        String ass = sbAss.toString();
        wrapperAssinatura.getSettings().setJavaScriptEnabled(true);
        WebSettings ws = wrapperAssinatura.getSettings();
        ws.setDefaultFontSize(11);
        wrapperAssinatura.setBackgroundColor(activity.getResources().getColor(R.color.grey));
        wrapperAssinatura.loadDataWithBaseURL("file:///android_asset/", ass, "txt/html", "utf-8", "");

        dataHora.setText(resultp.get("publishDate"));

        registrarMeusTopicos(view, position);
        registrarBotaoQuote(view, position);
        registrarBotaoAvatar(view, position);
        registrarBotaoMP(view, position);
        registrarBotaoEditar(view, position);

    }

    private void registrarMeusTopicos(View view, final int position) {
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView_wrapper_posts_avatar);
        if (imageView != null) {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, MeusTopicosActivity.class);
                    intent.putExtra("logado", activity.getLogado());
                    intent.putExtra("userNumber", NumberSelector.numeroUsuario(data.get(position).get("urlPerfil")));
                    activity.startActivityForResult(intent, 8);
                }
            });
        }

    }

    private void registrarBotaoMP(View view, final int position) {
        ImageButton imageButtonMP = (ImageButton) view.findViewById(R.id.imageButton_topico_mp);
        if (imageButtonMP != null) {
            imageButtonMP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (activity.getLogado()) {
                        Intent intent = new Intent(activity, MandarMpActivity.class);
                        String destinatario = data.get(position).get("autor");
                        String assunto = topicoTitulo;
                        intent.putExtra("destinatario", destinatario);
                        intent.putExtra("assunto", assunto);
                        intent.putExtra("userName", userNameLogado);
                        activity.startActivity(intent);
                    } else {
                        Toast.makeText(activity, activity.getString(R.string.nao_logado), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void registrarBotaoQuote(View view, final int position) {
        ImageButton imageButtonQuote = (ImageButton) view.findViewById(R.id.imageButton_topico_quoteUser);
        if (imageButtonQuote != null) {
            imageButtonQuote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (activity.getLogado()) {
                        String postNumero = data.get(position).get("postNumero");
                        Quotador quotador = new Quotador(activity, postNumero, Quote.TEXTO);
                        quotador.execute();
                    } else {
                        Toast.makeText(activity, activity.getString(R.string.nao_logado), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void registrarBotaoAvatar(View view, final int position) {
        ImageButton imageButtonAvatar = (ImageButton) view.findViewById(R.id.imageButton_topico_quoteAvatar);
        if (imageButtonAvatar != null) {
            imageButtonAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (activity.getLogado()) {
                        String urlAvatar = data.get(position).get("avatar");
                        urlAvatar = urlAvatar.replace(" ", "%20");
                        Quotador quotador = new Quotador(activity, urlAvatar, Quote.AVATAR);
                        quotador.execute();
                    } else {
                        Toast.makeText(activity, activity.getString(R.string.nao_logado), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void registrarBotaoEditar(View view, final int position) {
        ImageButton imageButtonEditar = (ImageButton) view.findViewById(R.id.imageButton_topico_edit);
        if (imageButtonEditar != null) {
            imageButtonEditar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String postCount = data.get(position).get("postCount");
                    if ("#1".equals(postCount)) {
                        String postNumero = data.get(position).get("postNumero");
                        Quotador quotador = new Quotador(activity, postNumero, Quote.TOPICO);
                        quotador.execute();
                    } else {
                        String postNumero = data.get(position).get("postNumero");
                        Quotador quotador = new Quotador(activity, postNumero, Quote.EDIT);
                        quotador.execute();
                    }
                }
            });
        }
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
