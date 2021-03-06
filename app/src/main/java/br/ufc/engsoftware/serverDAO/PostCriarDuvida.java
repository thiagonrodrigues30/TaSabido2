package br.ufc.engsoftware.serverDAO;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import br.ufc.engsoftware.auxiliar.NoSSLv3SocketFactory;


public class PostCriarDuvida extends AsyncTask<String, String, Void>{
    static String result, mensagem;
    int id_duvida_criada;
    public String param;
    private Context context;

    // you may separate this or combined to caller class.
    public interface AsyncResponse {
        void processFinish(String output, int id_duvida_criada, String mensagem);
    }


    public AsyncResponse delegate = null;

    public PostCriarDuvida(Context context, String param, AsyncResponse delegate){
        this.delegate = delegate;
        this.param = param;
        this.context = context;
    }

    public PostCriarDuvida(String param, AsyncResponse delegate){
        this.delegate = delegate;
        this.param = param;
    }

    @Override
    protected Void doInBackground(String... params) {

        SSLContext sslcontext = null;
        try {
            sslcontext = SSLContext.getInstance("TLSv1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        try {
            sslcontext.init(null,
                    null,
                    null);
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        SSLSocketFactory NoSSLv3Factory = new NoSSLv3SocketFactory(sslcontext.getSocketFactory());

        try {
            URL url = new URL(params[0]);

            //descomentar quando for fazer requisições pro servidor
//            HttpsURLConnection.setDefaultSSLSocketFactory(NoSSLv3Factory);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setDoOutput(true);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
            outputStreamWriter.write(param);
            outputStreamWriter.flush();

            int responseCode = connection.getResponseCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            JSONObject jsonResponse = new JSONObject(response.toString());
            String value = jsonResponse.getString("success");
            if (value.equals("true")){
                int id_duvida = jsonResponse.getInt("id_duvida");
                id_duvida_criada = id_duvida;
            }
            String mensagemResponse = jsonResponse.getString("message");


            result = value;
            mensagem = mensagemResponse;

            in.close();
        } catch (Exception e) {
            result = e.toString();
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        delegate.processFinish(result, id_duvida_criada, mensagem);

    }
}
