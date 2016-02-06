package com.bulbasauro.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bulbasauro.async.misc.Carteiro;
import com.bulbasauro.misc.Selo;
import com.bulbasauro.vtmobile.DetalheMpActivity;
import com.bulbasauro.vtmobile.MensagemParticularActivity;
import com.bulbasauro.vtmobile.R;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created on 29/01/2016.
 */
public class MensagensParticularesInbox extends BaseSwipeAdapter {

    private MensagemParticularActivity activity;
    private ArrayList<HashMap<String, String>> data;
    private HashMap<String, String> resultp = new HashMap<String, String>();

    public MensagensParticularesInbox(MensagemParticularActivity activity, ArrayList<HashMap<String, String>> data) {
        this.activity = activity;
        this.data = data;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipeLayout_mp_inbox;
    }

    @Override
    public View generateView(int position, ViewGroup parent) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.listview_mp_inbox, null);
        return itemView;
    }

    @Override
    public void fillValues(final int position, View view) {
        resultp = data.get(position);

        TextView textViewAssunto = (TextView) view.findViewById(R.id.textView_mp_assunto);
        TextView textViewAutor = (TextView) view.findViewById(R.id.textView_mp_autor);
        TextView textViewDataHora = (TextView) view.findViewById(R.id.textView_mp_dataHoraEnviado);
        ImageButton imageButtonConfirmarApagar = (ImageButton) view.findViewById(R.id.imageButton_mp_confirmarApagar);

        String novo = resultp.get("novo");
        if ("sim".equals(novo)) {
            view.setBackgroundColor(0xffffffff);
        } else {
            view.setBackgroundColor(0xffcccccc);
        }

        textViewAssunto.setText(resultp.get("assunto"));
        textViewAutor.setText(resultp.get("autor"));
        textViewDataHora.setText(resultp.get("dataEnvio"));

        final String mensagemURL = resultp.get("URL");

        imageButtonConfirmarApagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idPm = mensagemURL.replace("readIn.jbb?pm.id=", "");
                Carteiro carteiro = new Carteiro(activity, Selo.APAGAR);
                carteiro.setOut(false);
                carteiro.setIdPm(idPm);
                carteiro.execute();
            }
        });

        SwipeLayout swipeLayout = (SwipeLayout) view.findViewById(getSwipeLayoutResourceId(position));
        swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(activity, DetalheMpActivity.class);
                intent.putExtra("userName", activity.getSupportActionBar().getTitle());
                intent.putExtra("URL", mensagemURL);
                intent.putExtra("assunto", data.get(position).get("assunto"));
                intent.putExtra("autor", data.get(position).get("autor"));
                intent.putExtra("dataEnvio", data.get(position).get("dataEnvio"));
                intent.putExtra("out", false);
                activity.startActivityForResult(intent, 66);
            }
        });

        swipeLayout.getDragEdgeMap().clear();
        swipeLayout.addDrag(SwipeLayout.DragEdge.Left, swipeLayout.findViewById(R.id.bottom_wrapper_topicos));
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
