package com.example.juniorf.tcc.TUTORIAL;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.example.juniorf.tcc.MainActivity;
import com.example.juniorf.tcc.R;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class TutorialActivity3 extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance("Crie seus tipos", "Para criar seu tipo, você deve estar inserindo um ponto de interesse e clicar no botão ao lado da lista de tipos e preencher o formulário", R.drawable.insira_seu_tipo, Color.parseColor("#5472AE")));
        addSlide(AppIntroFragment.newInstance("Pesquise por Tipo", "Ao clicar, você tem acesso a todos os tipos já cadastrados na aplicação e pode obter resultados filtrados pelos tais tipos", R.drawable.pesquise_por_tipo, Color.parseColor("#5472AE")));

        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(Color.parseColor("#3F51B5"));
        setSeparatorColor(Color.parseColor("#2196F3"));

    }

    @Override
    public void onSkipPressed() {

        Intent n = new Intent(this, TutorialActivity4.class);
        n.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(n);

    }

    @Override
    public void onSlideChanged() {
        super.onSlideChanged();
    }

    @Override
    public void onDonePressed() {
        Intent intent =new Intent(this,TutorialActivity4.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
        //android.widget.Toast.makeText(this, "Antes do finish", Toast.LENGTH_SHORT).show();
        finish();
    }
}
