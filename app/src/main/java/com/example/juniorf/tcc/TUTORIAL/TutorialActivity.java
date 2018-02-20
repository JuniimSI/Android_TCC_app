package com.example.juniorf.tcc.TUTORIAL;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.example.juniorf.tcc.MainActivity;
import com.example.juniorf.tcc.R;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class TutorialActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Just set a title, description, background and image. AppIntro will do the rest.
        addSlide(AppIntroFragment.newInstance("Crie seus pontos", "Para criar seu ponto de interesse, toque e segure o local onde seu ponto deve ser cadastrado e preencha o formulário", R.drawable.screencriaponto, Color.parseColor("#1F51B5")));
        addSlide(AppIntroFragment.newInstance("Calcule rotas", "DESCRIPTION2", R.drawable.screencriaponto, Color.parseColor("#2F51B5")));
        addSlide(AppIntroFragment.newInstance("Troque mensagens", "DESCRIPTION3", R.drawable.screencriaponto, Color.parseColor("#3F51B5")));
        addSlide(AppIntroFragment.newInstance("Dúvida?", "DESCRIPTION4", R.drawable.screencriaponto, Color.parseColor("#4F51B5")));
        addSlide(AppIntroFragment.newInstance("Pesquise por Tipo", "DESCRIPTION5", R.drawable.screencriaponto, Color.parseColor("#5F51B5")));

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
        //Toast.makeText(this, "changed", Toast.LENGTH_SHORT).show();

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
