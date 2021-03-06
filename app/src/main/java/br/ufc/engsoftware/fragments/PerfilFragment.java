package br.ufc.engsoftware.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.squareup.picasso.Picasso;

import br.ufc.engsoftware.tasabido.PaginaPrincipalActivity;
import br.ufc.engsoftware.tasabido.R;
import br.ufc.engsoftware.views.RoundedImageView;

/**
 * Created by Thiago on 09/05/2016.
 */
public class PerfilFragment extends Fragment {

    // Referencia para os elementos da barra de usuario
    RoundedImageView rivFotoUsuario;
    TextView tvNomeUsuario;
    TextView tvEmailUsuario;
    Button btCreateQr;
    ImageView ivQrCode;


    public void setarBarraUsuario(){
        final Context context = getActivity();
        SharedPreferences sharedPreferences = context.getSharedPreferences(PaginaPrincipalActivity.PREFERENCES_FILE_NAME, context.MODE_PRIVATE);

        // Extrai os valores das preferencias
        final String nomeUsuario = sharedPreferences.getString("USER_NAME", "Visitante");
        final String emailUsuario = sharedPreferences.getString("USER_EMAIL", "email@example.com");
        final String fotoUsuario = sharedPreferences.getString("USER_PHOTO_PATH", "http://khojmaster.com/ui/user/realestate/assets/img/no-user.jpg");

        // Alterando as informações da barra de usuario numa thread secundaria
        tvNomeUsuario.post(new Runnable() {
            @Override
            public void run() {
                tvNomeUsuario.setText(nomeUsuario);
                tvEmailUsuario.setText(emailUsuario);
            }
        });

        rivFotoUsuario.post(new Runnable() {
            @Override
            public void run() {
                // Pega a imagem a partir da URL
                Picasso.with(context).load(fotoUsuario).into(rivFotoUsuario);
            }
        });

    }

    // Método principal do fragment, respectivo ao onCreate nas activities
    // Ele retorna a view que vai ser montada na tela
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

                // Seta o layout que será o view do fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_perfil, container, false);

        // Captura a referencia para os elementos da barra de usuario
        rivFotoUsuario = (RoundedImageView) rootView.findViewById(R.id.riv_foto_usuario);
        tvNomeUsuario = (TextView) rootView.findViewById(R.id.tv_nome_usuario);
        tvEmailUsuario = (TextView) rootView.findViewById(R.id.tv_email_usuario);
        ivQrCode = (ImageView) rootView.findViewById(R.id.ivQrCode);
        ivQrCode.setVisibility(View.GONE);
        btCreateQr = (Button) rootView.findViewById(R.id.btCreateQr);
        btCreateQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickMostrarQR(v);
            }
        });

        // Insere os dados da barra de usuario
        setarBarraUsuario();
        createQr();

        return rootView;
    }

    public void createQr(){
        String text2Qr = "CarteiraDoFulano";
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text2Qr, BarcodeFormat.QR_CODE,300,300);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            ivQrCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    public void onClickMostrarQR(View view){
        if (ivQrCode.getVisibility() == View.GONE) {
            ivQrCode.setVisibility(View.VISIBLE);
            btCreateQr.setText("Esconder carteira");
        } else {
            ivQrCode.setVisibility(View.GONE);
            btCreateQr.setText("Mostrar carteira");
        }


    }


}
