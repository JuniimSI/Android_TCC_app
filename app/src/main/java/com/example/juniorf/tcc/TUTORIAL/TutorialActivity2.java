package com.example.juniorf.tcc.TUTORIAL;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.example.juniorf.tcc.MainActivity;
import com.example.juniorf.tcc.R;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class TutorialActivity2 extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance("Veja informações rápidas", "Ao clicar em um ponto, surgirá uma janela de informações contendo suas principais informações", R.drawable.veja_sua_janela_de_informacoes, Color.parseColor("#4378E1")));
        addSlide(AppIntroFragment.newInstance("Detalhes e Mensagens", "Ao dar um clique longo sobre a janela de informações, você é redirecionado para a tela de detalhes do ponto seleconado", R.drawable.veja_tela_detalhes, Color.parseColor("#4378E1")));
        addSlide(AppIntroFragment.newInstance("Calcule rotas", "Clique em um ponto de interesse, visualize a janela de informações e clique sobre a janela para gerar a rota", R.drawable.gere_sua_rota, Color.parseColor("#4378E1")));
        addSlide(AppIntroFragment.newInstance("Detalhes profundos","Para obter os detalhes mais a fundos de cada ponto de interesse, clique no botão com o olhinho", R.drawable.veja_seus_detalhes, Color.parseColor("#4378E1")));

        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(Color.parseColor("#3F51B5"));
        setSeparatorColor(Color.parseColor("#2196F3"));

    }

    @Override
    public void onSkipPressed() {

        Intent n = new Intent(this, TutorialActivity3.class);
        n.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(n);

    }

    @Override
    public void onSlideChanged() {
        super.onSlideChanged();
    }

    @Override
    public void onDonePressed() {
        Intent intent =new Intent(this,TutorialActivity3.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
        //android.widget.Toast.makeText(this, "Antes do finish", Toast.LENGTH_SHORT).show();
        finish();
    }
}
