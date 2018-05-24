package com.example.juniorf.tcc.TUTORIAL;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.example.juniorf.tcc.MainActivity;
import com.example.juniorf.tcc.R;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class TutorialActivity5 extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance("Ache-se", "Clique no botão para colocar a camera e o zoom em sua localização atual ", R.drawable.ache_seu_local, Color.parseColor("#5472AE")));
        addSlide(AppIntroFragment.newInstance("Menu lateral", "Utilize o menu para lhe levar aos componentes da aplicação", R.drawable.navegue_no_menu, Color.parseColor("#CBD7EE")));
        addSlide(AppIntroFragment.newInstance("Dúvida?", "Em todas as telas contamos com este icone, o mesmo o trará de volta para a tela de tutorial, lhe dando todo o suporte necessario para uso da aplicação", R.drawable.tutorial, Color.parseColor("#5472AE")));

        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(Color.parseColor("#3F51B5"));
        setSeparatorColor(Color.parseColor("#2196F3"));

    }

    @Override
    public void onSkipPressed() {

        Intent n = new Intent(this, MainActivity.class);
        n.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(n);

    }

    @Override
    public void onSlideChanged() {
        super.onSlideChanged();
    }

    @Override
    public void onDonePressed() {
        Intent intent =new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
        //android.widget.Toast.makeText(this, "Antes do finish", Toast.LENGTH_SHORT).show();
        finish();
    }
}
