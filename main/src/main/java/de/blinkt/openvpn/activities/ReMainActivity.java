package de.blinkt.openvpn.activities;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.VpnService;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.annotation.RequiresApi;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rahatarmanahmed.cpv.CircularProgressView;

import java.util.Collection;

import de.blinkt.openvpn.LaunchVPN;
import de.blinkt.openvpn.R;
import de.blinkt.openvpn.VpnProfile;
import de.blinkt.openvpn.api.IOpenVPNAPIService;
import de.blinkt.openvpn.core.ProfileManager;
import de.blinkt.openvpn.core.VpnStatus;
import de.blinkt.openvpn.fragments.Utils;
import de.blinkt.openvpn.fragments.VPNProfileList;

/**
 * Created by root on 2/02/17.
 */

public class ReMainActivity extends Activity  {



    protected IOpenVPNAPIService mService=null;


    boolean vpnPower = false;

    String on;
    CircularProgressView progressView;
    TextView txt;
    ProfileManager pM;
    VPNProfileList vpnList;
    Collection<VpnProfile> col;
    VpnProfile[] p ;
    Context vpnListContext;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_re_main);

        ActionBar actionBar = getActionBar();

        actionBar.setTitle("");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
        actionBar.setIcon(android.R.color.transparent);

        Menu m = (Menu)findViewById(R.menu.re_main_menu);

        vpnList = new VPNProfileList();



       System.out.println(m.getItem());
        //Profiles VPN manager
        pM = ProfileManager.getInstance(vpnList.getContext());
        col = pM.getProfiles();
        p = new VpnProfile[pM.getProfiles().size()];

        int i = 0;//iterator
        //Getting profiles !! TNKS GOSHH
        for(VpnProfile vp : col) {
            p[i] = vp;
            //System.out.println("perfil = " + vp);
            System.out.println("perfil" + i + " = "+ p[i]);
            i+=1;
        }


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
                    startVPN(p[2]);//Star first VPN
                } else if (vpnPower == false) { // vpn is power off
                    vpnOff();
                    Intent disconnectVPN = new Intent(getApplicationContext(), DisconnectVPN.class);//desconectar vpn
                    startActivity(disconnectVPN);
                }
            }

        });


    }



    @Override
    protected void onResume() {
        super.onResume();

        System.out.println("ESTADO DE VPN: "+ VpnStatus.isVPNActive());


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    //FUNCOONES
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void vpnOn(){
        progressView.setColor(getResources().getColor(R.color.bar));
        progressView.setIndeterminate(true);
        progressView.startAnimation();
        txt.setText("Encendiendo VPN");
        txt.setTextColor(getResources().getColor(R.color.bar));




        //Hilo
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions
                progressView.setIndeterminate(false);
                progressView.setMaxProgress(100f);
                progressView.setProgress(100f);
                progressView.setColor(getResources().getColor(R.color.bar_complete));
                //startActivity(new Intent(getApplicationContext(),MainActivity.class));//Arrancando actividad de OpenVPN

                txt.setTextColor(getResources().getColor(R.color.bar_complete));
                txt.setText("VPN conectado");

            }
        }, 3000);

    }

    public void vpnOff(){
        progressView.setColor(getResources().getColor(R.color.loaded_bar));
        progressView.stopAnimation();
        progressView.setIndeterminate(false);
        progressView.setMaxProgress(100);
        progressView.setProgress(100);


        txt.setTextColor(getResources().getColor(R.color.loaded_bar));
        txt.setText("VPN apagado");

    }

    private void startVPN(VpnProfile profile) {


        Intent intent = new Intent(getApplicationContext(), LaunchVPN.class);
        intent.putExtra(LaunchVPN.EXTRA_KEY, profile.getUUID().toString());
        intent.setAction(Intent.ACTION_MAIN);
        startActivity(intent);
    }


    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.re_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favorite:
               System.out.println("Undido boton del menu action bar");
                startImportConfigFilePicker();
                //vpnList.startImportConfigFilePicker();
                return true;

            case R.id.show_vpn:
                //showPopup();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private boolean startFilePicker() {

        Intent i = Utils.getFilePickerIntent(getApplicationContext(), Utils.FileType.OVPN_CONFIG);
        if (i != null) {
            startActivityForResult(i, 392);
            return true;
        } else
            return false;
    }

    public boolean startImportConfigFilePicker() {//Metodo para llamar el ovpn desde un archivo
        boolean startOldFileDialog = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            startOldFileDialog = !startFilePicker();

        if (startOldFileDialog)
            startImportConfig();

        return true;
    }


    private void startImportConfig() {
        Intent intent = new Intent(getApplicationContext(), FileSelect.class);
        intent.putExtra(FileSelect.NO_INLINE_SELECTION, true);
        intent.putExtra(FileSelect.WINDOW_TITLE, R.string.import_configuration_file);
        startActivityForResult(intent, 43);
    }

}
