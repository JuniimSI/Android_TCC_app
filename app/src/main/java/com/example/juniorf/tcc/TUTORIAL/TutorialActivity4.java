package com.example.juniorf.tcc.TUTORIAL;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.example.juniorf.tcc.MainActivity;
import com.example.juniorf.tcc.R;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class TutorialActivity4 extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        addSlide(AppIntroFragment.newInstance("Troque mensagens", "Para enviar uma mensagem, basta estar na tela de detalhes de um ponto, clicar no botão embaixo e escrever e enviar sua mensagem", R.drawable.insira_sua_mensagem, Color.parseColor("#5472AE")));
        addSlide(AppIntroFragment.newInstance("Edite, Apague e Responda", "Ao clicar e segurar sobre uma mensagem, surgirá este menu, com estas três operações, edição, delete e resposta", R.drawable.mensagens_menu, Color.parseColor("#5472AE")));


        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(Color.parseColor("#3F51B5"));
        setSeparatorColor(Color.parseColor("#2196F3"));

    }

    @Override
    public void onSkipPressed() {

        Intent n = new Intent(this, TutorialActivity5.class);
        n.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(n);

    }

    @Override
    public void onSlideChanged() {
        super.onSlideChanged();
    }

    @Override
    public void onDonePressed() {
        Intent intent =new Intent(this,TutorialActivity5.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
        finish();
    }
}
