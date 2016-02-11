package com.bulbasauro.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bulbasauro.abstracts.AbstractActivity;
import com.bulbasauro.misc.CustomOnTouchListener;
import com.bulbasauro.utils.RatingSelector;
import com.bulbasauro.vtmobile.R;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created on 18/01/2016.
 */
public class Topicos extends BaseSwipeAdapter {

    private AbstractActivity activity;
    private ArrayList<HashMap<String, String>> data;
    private HashMap<String, String> resultp = new HashMap<String, String>();

    private int[] colors = new int[]{0xffffffff, 0xffcccccc};

    public Topicos(AbstractActivity activity, ArrayList<HashMap<String, String>> data) {
        this.activity = activity;
        this.data = data;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipeLayout_main_topicos;
    }

    @Override
    public View generateView(int position, ViewGroup parent) {
        View itemView;
        switch (parent.getId()) {
            case R.id.listView_pessoal_criei:
                itemView = LayoutInflater.from(activity).inflate(R.layout.listview_pessoal_criei, null);
                break;
            case R.id.listView_pessoal_participei:
                itemView = LayoutInflater.from(activity).inflate(R.layout.listview_pessoal_participei, null);
                break;
            default:
                itemView = LayoutInflater.from(activity).inflate(R.layout.listview_topicos, null);
                break;
        }
        return itemView;
    }

    @Override
    public void fillValues(final int position, View view) {
        TextView tituloTopico;
        TextView autorTopico;
        TextView respostas;
        TextView qtdPaginas;
        TextView lastPost;
        ImageView imageViewRating;

        int colorPos = position % colors.length;
        int cor = colors[colorPos];
        view.setBackgroundColor(cor);

        resultp = data.get(position);

        tituloTopico = (TextView) view.findViewById(R.id.textView_main_topico_titulo);
        autorTopico = (TextView) view.findViewById(R.id.textView_main_topico_autor);
        respostas = (TextView) view.findViewById(R.id.textView_main_topico_resposta);
        qtdPaginas = (TextView) view.findViewById(R.id.textView_main_topico_qtdPagina);
        lastPost = (TextView) view.findViewById(R.id.textView_main_topico_lastPost);
        imageViewRating = (ImageView) view.findViewById(R.id.imageView_main_topico_rating);

        tituloTopico.setText(resultp.get("topicoTitulo"));
        autorTopico.setText(resultp.get("topicoAutor"));
        respostas.setText(resultp.get("topicoRespostas"));
        qtdPaginas.setText(resultp.get("numeroDePaginas"));
        lastPost.setText(resultp.get("topicoLastMessage"));
        if (imageViewRating != null) {
            imageViewRating.setImageResource(RatingSelector.definirRating(resultp.get("topicoRating")));
        }

        SwipeLayout swipeLayout = (SwipeLayout) view.findViewById(getSwipeLayoutResourceId(position));
        swipeLayout.getSurfaceView().setOnTouchListener(new CustomOnTouchListener(activity, data, position));

        registrarBotaoFlag(view, position);
        registrarBotaoFavorito(view, position);

    }

    private void registrarBotaoFlag(View view, int position) {
        ImageButton imageButtonFlag = (ImageButton) view.findViewById(R.id.imageButton_main_topicos_flag_vazia);
        if (imageButtonFlag != null) {
            imageButtonFlag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(activity, activity.getString(R.string.implementar), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void registrarBotaoFavorito(View view, int position) {
        ImageButton imageButtonFavorito = (ImageButton) view.findViewById(R.id.imageButton_main_topicos_favorito);
        if (imageButtonFavorito != null) {
            imageButtonFavorito.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(activity, activity.getString(R.string.implementar), Toast.LENGTH_SHORT).show();
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
