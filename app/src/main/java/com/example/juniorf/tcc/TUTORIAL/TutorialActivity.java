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
        //addSlide(AppIntroFragment.newInstance("Iniciando a aplicação", "Para começar clique sobre a imagem do aplicativo e seja redirecionado para a tela de mapa", R.drawable.clique_para_iniciar, Color.parseColor("#5472AE")));
        //addSlide(AppIntroFragment.newInstance("Faça seu Login ", "Para realizar o login com o Google, basta clicar no botão e logar uma conta Google ", R.drawable.faca_seu_login, Color.parseColor("#5472AE")));
        addSlide(AppIntroFragment.newInstance("Crie seus pontos", "Para criar seu ponto de interesse, toque e segure o local onde seu ponto deve ser cadastrado e preencha o formulário", R.drawable.insira_seu_ponto, Color.parseColor("#5472AE")));
        addSlide(AppIntroFragment.newInstance("Edite seus pontos", "Para editar a localização de um ponto de interesse seu, basta clicar, segurar e arrastar", R.drawable.edite_seu_ponto, Color.parseColor("#5472AE")));
        //addSlide(AppIntroFragment.newInstance("Apague seus pontos", "Para apagar seu ponto de interesse, basta dar um clique no icone de lixeira na tela de detalhes de tal ponto", R.drawable.clique_para_apagar, Color.parseColor("#5472AE")));
        addSlide(AppIntroFragment.newInstance("Veja informações rápidas", "Ao clicar em um ponto, surgirá uma janela de informações contendo suas principais informações", R.drawable.veja_sua_janela_de_informacoes, Color.parseColor("#4378E1")));
        addSlide(AppIntroFragment.newInstance("Detalhes e Mensagens", "Ao dar um clique longo sobre a janela de informações, você é redirecionado para a tela de detalhes do ponto seleconado", R.drawable.veja_tela_detalhes, Color.parseColor("#4378E1")));
        addSlide(AppIntroFragment.newInstance("Calcule rotas", "Clique em um ponto de interesse, visualize a janela de informações e clique sobre a janela para gerar a rota", R.drawable.gere_sua_rota, Color.parseColor("#4378E1")));
       // addSlide(AppIntroFragment.newInstance("Detalhes profundos","Para obter os detalhes mais a fundos de cada ponto de interesse, clique no botão com o olhinho", R.drawable.veja_seus_detalhes, Color.parseColor("#4378E1")));
        addSlide(AppIntroFragment.newInstance("Crie seus tipos", "Para criar seu tipo, você deve estar inserindo um ponto de interesse e clicar no botão ao lado da lista de tipos e preencher o formulário", R.drawable.insira_seu_tipo, Color.parseColor("#5472AE")));
        addSlide(AppIntroFragment.newInstance("Pesquise por Tipo", "Ao clicar, você tem acesso a todos os tipos já cadastrados na aplicação e pode obter resultados filtrados pelos tais tipos", R.drawable.pesquise_por_tipo, Color.parseColor("#5472AE")));
        //addSlide(AppIntroFragment.newInstance("Ache-se", "Clique no botão para colocar a camera e o zoom em sua localização atual ", R.drawable.ache_seu_local, Color.parseColor("#5472AE")));
        addSlide(AppIntroFragment.newInstance("Troque mensagens", "Para enviar uma mensagem, basta estar na tela de detalhes de um ponto, clicar no botão embaixo e escrever e enviar sua mensagem", R.drawable.insira_sua_mensagem, Color.parseColor("#5472AE")));
        addSlide(AppIntroFragment.newInstance("Edite, Apague e Responda", "Ao clicar e segurar sobre uma mensagem, surgirá este menu, com estas três operações, edição, delete e resposta", R.drawable.mensagens_menu, Color.parseColor("#5472AE")));
        //addSlide(AppIntroFragment.newInstance("Menu lateral", "Utilize o menu para lhe levar aos componentes da aplicação", R.drawable.navegue_no_menu, Color.parseColor("#CBD7EE")));
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
