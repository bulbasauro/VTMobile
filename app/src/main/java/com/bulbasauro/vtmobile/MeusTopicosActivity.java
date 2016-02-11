package com.bulbasauro.vtmobile;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bulbasauro.abstracts.AbstractFragment;
import com.bulbasauro.adapters.MeusTopicosFragment;
import com.bulbasauro.async.jsoup.JsoupMeusTopicos;
import com.bulbasauro.misc.Comando;
import com.bumptech.glide.Glide;

import org.jsoup.nodes.Document;

public class MeusTopicosActivity extends AbstractFragment {

    private ListView listViewCriei;
    private ListView listViewParticipei;
    private String userNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_topicos);

        userNumber = getIntent().getExtras().getString("userNumber");
        setLogado(getIntent().getExtras().getBoolean("logado"));
        setViewPager((ViewPager) findViewById(R.id.viewPager_pessoal));
        setTabLayout((TabLayout) findViewById(R.id.tablayout_pessoal_secao));
        setFragmentPagerAdapter(new MeusTopicosFragment(getSupportFragmentManager()));

        final View rootView = getWindow().getDecorView().getRootView();
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        // only want to do this once
                        rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        if (getLogado()) {
                            iniciarLoginSharedPreferences(getPaginaAtual());
                        } else {
                            abrirPagina(getPaginaAtual(), Comando.ABRIR);
                        }

                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void setInfo(Document document) {
        ImageView imageViewAvatar = (ImageView) findViewById(R.id.imageView_pessoal_avatar);
        TextView textViewUsername = (TextView) findViewById(R.id.textView_pessoal_username);
        TextView textViewMensagens = (TextView) findViewById(R.id.textView_pessoal_mensagens);
        TextView textViewTopicos = (TextView) findViewById(R.id.textView_pessoal_topicos);

        String avatar = document.select("img[id=avatarImg]").attr("src");
        avatar = avatar.replace(" ", "%20");
        String username = document.select("h1[class=userName]").text();
        String mensagens = document.select("li[class=row2 first]").first().text();
        String topicos = document.select("li[class=row2]").first().text();

        Glide.with(MeusTopicosActivity.this)
                .load(avatar)
                .placeholder(R.drawable.nopic)
                .error(R.drawable.nopic)
                .fitCenter()
                .into(imageViewAvatar);
        textViewUsername.setText(username);
        textViewMensagens.setText(mensagens);
        textViewTopicos.setText(topicos);
    }

    @Override
    public void abrirHome() {
        setResult(2);
        finish();
    }

    @Override
    public void abrirPagina(int numeroPagina, Comando comando) {
        listViewCriei = (ListView) getViewPager().findViewById(R.id.listView_pessoal_criei);
        listViewParticipei = (ListView) getViewPager().findViewById(R.id.listView_pessoal_participei);

        JsoupMeusTopicos meusTopicos = new JsoupMeusTopicos(MeusTopicosActivity.this, getCookies(), userNumber, listViewCriei, listViewParticipei);
        meusTopicos.execute();
    }

}

