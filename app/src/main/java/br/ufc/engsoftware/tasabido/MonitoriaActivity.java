package br.ufc.engsoftware.tasabido;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.ufc.engsoftware.BDLocalManager.MonitoriaBDManager;
import br.ufc.engsoftware.auxiliar.Statics;
import br.ufc.engsoftware.auxiliar.Utils;
import br.ufc.engsoftware.models.Monitoria;
import br.ufc.engsoftware.serverDAO.PostCriarMonitoria;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class MonitoriaActivity extends AppCompatActivity {

    String titulo, descricao, data, endereco;
    int id_monitoria, id_subtopico, id_materia;

    @InjectView(R.id.data) EditText _data;
    @InjectView(R.id.input_descricao) EditText _descricao;
    @InjectView(R.id.input_titulo) EditText _titulo;
    @InjectView(R.id.input_endereco) EditText _endereco;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoria);
        ButterKnife.inject(this);


        // Pega a intent que chamou essa activity
        Intent intent = getIntent();
        data = intent.getStringExtra("DATA");
        titulo = intent.getStringExtra("TITULO");
        descricao = intent.getStringExtra("DESCRICAO");
        endereco = intent.getStringExtra("ENDERECO");
        id_monitoria = intent.getIntExtra("ID_MONITORIA", 0);
        id_subtopico = intent.getIntExtra("ID_SUBTOPICO", 0);
        id_materia = intent.getIntExtra("ID_MATERIA", 0);


        _data.setText(data);
        _descricao.setText(descricao);
        _titulo.setText(titulo);
    }


    public void onClickAtualizarMonitoria(View view){
        Utils utils = new Utils(this);
        String id_usuario_string = utils.getFromSharedPreferences("id_usuario", "");
        int id_usuario = Integer.parseInt(id_usuario_string);
        String titulo = _titulo.getText().toString();
        String descricao = _descricao.getText().toString();
        String endereco = _endereco.getText().toString();
        String data = _data.getText().toString();

        Monitoria monitoria = new Monitoria(id_monitoria, id_usuario, id_materia, id_subtopico, titulo, descricao, data, endereco);
        JSONObject jsonParam = createJsonParam(monitoria);

        try {
            new PostCriarMonitoria(this, jsonParam, Statics.ATUALIZAR_MONITORIA, new PostCriarMonitoria.AsyncResponse(){
                public void processFinish(String output){
                    if (output.equals("200")){
                        Utils.progressDialog.setMessage("Monitoria atualizada.");
                        Utils.delayMessage();
                        deletarMonitoriaBDLocal(id_monitoria);
                        finish();
                    }else{
                        Utils.progressDialog.setMessage("Algum erro ocorreu, tente denovo mais tarde.");
                        Utils.delayMessage();
                    }
                }
            }).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deletarMonitoriaBDLocal(int id_monitoria) {
        MonitoriaBDManager db = new MonitoriaBDManager(this);
        db.deletarMonitoriaPorId(id_monitoria, this);
    }

    public void onClickDeletarMonitoria(View view){
        Utils utils = new Utils(this);
        String id_usuario_string = utils.getFromSharedPreferences("id_usuario", "");
        int id_usuario = Integer.parseInt(id_usuario_string);

        Monitoria monitoria = new Monitoria(id_monitoria, id_usuario);
        JSONObject jsonParam = createJsonParamToDeleteMonitoria(monitoria);

        try {
            new PostCriarMonitoria(this, jsonParam, Statics.DELETAR_MONITORIA, new PostCriarMonitoria.AsyncResponse(){
                public void processFinish(String output){
                    if (output.equals("200")){
                        Utils.progressDialog.setMessage("Monitoria deletada.");
                        Utils.delayMessage();
                        deletarMonitoriaBDLocal(id_monitoria);
                        finish();
                    }else{
                        Utils.progressDialog.setMessage("Algum erro ocorreu, tente denovo mais tarde.");
                        Utils.delayMessage();
                    }
                }
            }).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public JSONObject createJsonParam(Monitoria monitoria) {
        JSONObject json = new JSONObject();
        JSONArray subtopicosJson = new JSONArray();
        try {
            json.put("titulo", monitoria.getTitulo());
            json.put("descricao", monitoria.getDescricao());
            json.put("endereco", monitoria.getEndereco());
            json.put("id_usuario", monitoria.getId_usuario());
            json.put("id_materia", monitoria.getId_materia());
            json.put("id_monitoria", monitoria.getId_monitoria());
            json.put("data_monitoria", monitoria.getData());
            json.put("lat", "0.00");
            json.put("long", "0.00");
            subtopicosJson.put(id_subtopico);
            json.put("ids_subtopicos", subtopicosJson);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public JSONObject createJsonParamToDeleteMonitoria(Monitoria monitoria) {
        JSONObject json = new JSONObject();
        JSONArray subtopicosJson = new JSONArray();
        try {
            json.put("id_usuario", monitoria.getId_usuario());
            json.put("id_monitoria", monitoria.getId_monitoria());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

}
