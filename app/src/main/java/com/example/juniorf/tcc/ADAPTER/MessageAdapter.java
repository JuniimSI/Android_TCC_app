package com.example.juniorf.tcc.ADAPTER;

import android.content.Context;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.juniorf.tcc.MODEL.Mensagem;
import com.example.juniorf.tcc.R;

import java.util.HashMap;
import java.util.List;

public class MessageAdapter extends BaseExpandableListAdapter {

    private List<Mensagem> messages;
    private HashMap<Mensagem, List<Mensagem>> respostas;
    private Context context;


    public MessageAdapter(Context context, List<Mensagem> grupos, HashMap<Mensagem, List<Mensagem>> itensGrupos) {
        // inicializa as variáveis da classe
        this.context = context;
        messages = grupos;
        respostas = itensGrupos;
    }

    @Override
    public int getGroupCount() {
        // retorna a quantidade de grupos
        return messages.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // retorna a quantidade de itens de um grupo
        return respostas.get(getGroup(groupPosition)).size();
    }

    @Override
    public Mensagem getGroup(int groupPosition) {
        // retorna um grupo
        return messages.get(groupPosition);
    }

    @Override
    public Mensagem getChild(int groupPosition, int childPosition) {
        // retorna um item do grupo
        return respostas.get(getGroup(groupPosition)).get(childPosition);
    }



    @Override
    public long getGroupId(int groupPosition) {
        //return (getGroup(groupPosition).getId());
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        // retorna o id do item do grupo, porém como nesse exemplo
        // o item do grupo não possui um id específico, o retorno
        // será o próprio childPosition
        return childPosition;
        //return (getChild(groupPosition, childPosition).getId());
    }

    @Override
    public boolean hasStableIds() {
        // retorna se os ids são específicos (únicos para cada
        // grupo ou item) ou relativos
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        // cria os itens principais (grupos)

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }

        //pegando as referências das Views
        TextView email = (TextView)
                convertView.findViewById(R.id.tvEmail);
        TextView message = (TextView)
                convertView.findViewById(R.id.tvMessage);
        ImageView imagem = (ImageView)
                convertView.findViewById(R.id.ivPerfil);

        //populando as Views
        email.setText( getGroup(groupPosition).getEmailOrigem());
        message.setText( getGroup(groupPosition).getTexto());
        imagem.setImageResource(R.mipmap.ic_user);

        
        ///If expanded
        
        if (isExpanded) {
			email.setTypeface(null, Typeface.BOLD);
			email.setCompoundDrawablesWithIntrinsicBounds(0, 0,
					R.drawable.ic_up, 0);
		} else {
			// If group is not expanded then change the text back into normal
			// and change the icon
 
			email.setTypeface(null, Typeface.NORMAL);
			email.setCompoundDrawablesWithIntrinsicBounds(0, 0,
					R.drawable.ic_down, 0);
		}
        
        return convertView;

        //tvQtde.setText(String.valueOf(getChildrenCount(groupPosition)));

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        // cria os subitens (itens dos grupos)

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item_item, null);
        }

        TextView email = (TextView)
                convertView.findViewById(R.id.tvEmail);
        TextView message = (TextView)
                convertView.findViewById(R.id.tvMessage);
        ImageView imagem = (ImageView)
                convertView.findViewById(R.id.ivPerfil);

        //populando as Views
        email.setText( getChild(groupPosition, childPosition).getEmailOrigem());
        message.setText( getChild(groupPosition, childPosition).getTexto());
        imagem.setImageResource(R.mipmap.ic_user);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        // retorna se o subitem (item do grupo) é selecionável
        return true;
    }

    public void atualizaLista(List<Mensagem> mensagens, HashMap<Mensagem, List<Mensagem>> respostas){
        this.messages = mensagens;
        this.respostas = respostas;
    }

}
