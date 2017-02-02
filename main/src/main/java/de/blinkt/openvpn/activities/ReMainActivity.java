package de.blinkt.openvpn.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;

import de.blinkt.openvpn.R;

/**
 * Created by root on 2/02/17.
 */

public class ReMainActivity extends Activity  {

    boolean vpnPower,on = false;
    CircularProgressView progressView;
    TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_re_main);

        txt = (TextView) findViewById(R.id.textView2);//texto central

        //associate pb
        progressView = (CircularProgressView) findViewById(R.id.progress_view);
        vpnOff();//Initial pogress bar setting
        //Progress bar click listener
        progressView.setOnClickListener(new View.OnClickListener() {//CLick Listener para el circular progrss bar
            @Override
            public void onClick(View v) {

                vpnPower = !vpnPower;//switch vpn

                if (vpnPower) {//prendieron el vpn
                    vpnOn();
                } else if (vpnPower == false) { // vpn is power off
                    vpnOff();
                }
            }

        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (on){

            progressView.setIndeterminate(false);
            progressView.setMaxProgress(100f);
            progressView.setProgress(100f);
            progressView.setColor(getResources().getColor(R.color.accent));

            txt.setText("VPN conectado");

        }


    }

    //FUNCOONES
    public void vpnOn(){
        progressView.setColor(getResources().getColor(R.color.gelb));
        progressView.setIndeterminate(true);
        progressView.startAnimation();
        txt.setText("Encendiendo VPN");

        //Hilo
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                /* Actions
                progressView.setIndeterminate(false);
                progressView.setMaxProgress(100f);
                progressView.setProgress(100f);
                progressView.setColor(getResources().getColor(R.color.accent));

                txt.setText("VPN conectado");
                */
                startActivity(new Intent(getApplicationContext(),MainActivity.class));//Arrancando actividad de OpenVPN
                System.out.println("Paso el hilo" + progressView.getColor());

            }
        }, 2000);


    }

    public void vpnOff(){
        progressView.setColor(getResources().getColor(R.color.rot));
        progressView.stopAnimation();
        progressView.setIndeterminate(false);
        progressView.setMaxProgress(100);
        progressView.setProgress(100);
        txt.setText("VPN apagado");

    }


}
