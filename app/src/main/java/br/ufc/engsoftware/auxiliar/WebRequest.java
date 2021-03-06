package br.ufc.engsoftware.auxiliar;


import java.io.IOException;

import br.ufc.engsoftware.models.Duvida;
import br.ufc.engsoftware.models.Perfil;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WebRequest {

    // Metodo para fazer requisições GET para o web service, os parametros get
    // devem ser colocados diretamente na URL
    public static String httpGet(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    // Envia dados para o web service por POST, os dados do post sao enviados em
    // formato JSON, entao o servidor precisa tratar isso em JSON
    public static String httpPostJson(String url, String json) throws IOException {

        MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return String.valueOf(response.code());
    }


    public static String httpPostCadastrarPerfil(String url, Perfil perfil) throws IOException {

        MediaType JSON
                = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormEncondingBuilder()
                .add("first_name", perfil.getNome())
                .add("username", perfil.getUsuario())
                .add("password", perfil.getSenha())
                .add("email", perfil.getEmail())
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}