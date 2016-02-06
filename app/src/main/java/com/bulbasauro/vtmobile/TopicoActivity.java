package com.bulbasauro.vtmobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bulbasauro.abstracts.AbstractNavegacao;
import com.bulbasauro.async.jsoup.JsoupPosts;
import com.bulbasauro.misc.Comando;
import com.bulbasauro.misc.RatingSelector;

public class TopicoActivity extends AbstractNavegacao {

    private JsoupPosts posts;

    private String url = "http://forum.jogos.uol.com.br";
    private String rating = "0.0";
    private String topicoTitulo = "";
    private String topicoNumero = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topico);

        ListView listViewTopicos = (ListView) findViewById(R.id.listView_topico_posts);
        setListView(listViewTopicos);

        url = url.concat(getIntent().getExtras().getString("topicoURL")).concat("?page=");
        rating = getIntent().getExtras().getString("topicoRating");
        Boolean lg = getIntent().getExtras().getBoolean("logado");
        setLogado(lg);

        int pag = getIntent().getExtras().getInt("pagina");
        setPaginaAtual(pag);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (getLogado()) {
            iniciarLoginSharedPreferences(getPaginaAtual());
        } else {
            abrirPagina(getPaginaAtual(), Comando.ABRIR);
        }
        String topicoURL = getIntent().getExtras().getString("topicoURL");
        topicoNumero = "";
        for (int i = topicoURL.length(); i > 0; i--) {
            String caracter = String.valueOf(topicoURL.charAt(i - 1));
            if ("_".equals(caracter)) {
                topicoNumero = new StringBuilder(topicoNumero).reverse().toString();
                break;
            } else {
                topicoNumero = topicoNumero + caracter;
            }
        }
    }

    public void inserirHeaderTopico(String topicoTitulo, String dataPublished) {

        this.topicoTitulo = topicoTitulo;

        View h = getListView().findViewById(R.id.relativeLayout_posts_header);
        if (h == null) {
            View header = getLayoutInflater().inflate(R.layout.listview_posts_header, getListView(), false);

            // Titulo
            TextView textViewTitulo = (TextView) header.findViewById(R.id.textView_posts_header_titulo);
            textViewTitulo.setText(topicoTitulo);
            // Data e hora publicada
            TextView textViewData = (TextView) header.findViewById(R.id.textView_posts_header_postDate);
            textViewData.setText(dataPublished);
            // Rating
            ImageView imageViewRatingTopico = (ImageView) header.findViewById(R.id.imageView_posts_header_rating);
            imageViewRatingTopico.setImageResource(RatingSelector.definirRating(rating));

            getListView().addHeaderView(header);
        }
    }

    @Override
    public void abrirPagina(int numeroPagina, Comando comando) {
        posts = new JsoupPosts(TopicoActivity.this, getListView(), numeroPagina, comando, url, rating);
        posts.execute();
    }

    public void iniciarBotaoResponder() {
        // RESPONDER TÓPICO
        ImageButton imageButtonResponderTopico = (ImageButton) getToolbarNavegacao().findViewById(R.id.imageButton_topico_answer);
        imageButtonResponderTopico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getLogado()) {
                    enviarDadosResposta(null, null, "texto", null);
                } else {
                    Toast.makeText(TopicoActivity.this, getString(R.string.nao_logado), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void enviarDadosResposta(String userQuotado, String mensagemQuotada, String quote, String postNumero) {
        Intent intent;
        if (!"topico".equals(quote)) {
            intent = new Intent(TopicoActivity.this, ResponderTopicoActivity.class);
        } else {
            intent = new Intent(TopicoActivity.this, NovoTopicoActivity.class);
            intent.putExtra("titulo", topicoTitulo);
            intent.putExtra("topicoNumero", topicoNumero);
        }
        intent.putExtra("quote", quote);
        intent.putExtra("postNumero", postNumero);
        intent.putExtra("quote", quote);
        intent.putExtra("topicoRating", rating);
        intent.putExtra("topicoTitulo", posts.getTopicoTitulo());
        intent.putExtra("publishDate", posts.getDataPublished());
        intent.putExtra("userName", getSupportActionBar().getTitle());
        intent.putExtra("topicoURL", getIntent().getExtras().getString("topicoURL"));

        intent.putExtra("userQuotado", userQuotado);
        intent.putExtra("mensagemQuotada", mensagemQuotada);

        if (!"topico".equals(quote)) {
            startActivityForResult(intent, 2);
        } else {
            startActivityForResult(intent, 9);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                if (getLogado()) {
                    iniciarLoginSharedPreferences(getPaginaAtual());
                } else {
                    abrirPagina(getPaginaAtual(), Comando.ABRIR);
                }
            }
        } else if (requestCode == 4) {
            if (resultCode == RESULT_FIRST_USER) {
                Boolean lg = data.getExtras().getBoolean("logado");
                setLogado(lg);
                abrirHome();
            }
        } else if (requestCode == 9) { // Edição de tópico
            if (resultCode == RESULT_OK) {
                iniciarLoginSharedPreferences(getPaginaAtual());
            }
        }
    }

    @Override
    public void abrirHome() {
        Intent intent = new Intent();
        intent.putExtra("logado", getLogado());
        setResult(RESULT_FIRST_USER, intent);
        finish();
    }

    @Override
    public int getTotalPaginas() {
        return posts.getTotalPaginas();
    }

    public String getTopicoTitulo() {
        return topicoTitulo;
    }

    public void setTopicoTitulo(String topicoTitulo) {
        this.topicoTitulo = topicoTitulo;
    }

    public String getTopicoNumero() {
        return topicoNumero;
    }

    public void setTopicoNumero(String topicoNumero) {
        this.topicoNumero = topicoNumero;
    }
}
