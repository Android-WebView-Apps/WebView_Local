package com.webview.local;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import org.codechimp.apprater.AppRater;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

//    private WebView webView;
    private WebViewClient client;

    private InterstitialAd interstitial; // nome dado ao intestitial
    private AdView mAdView; // nome dado ao banner do admob
    ImageView Splash; // nome dado a imagem do SPLASH dentro do XML activity_main
    WebView webView;
    AdView Banner;
    Timer waitTimer; // nome dado ao timer
    ProgressBar Carregando;
    private boolean interstitialCanceled = false; // nome dado ao fato do interstitial ser cancelado



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);





        setContentView(com.webview.local.R.layout.activity_main); // determina o nome do arquivo na pasta Layouts, da MainActivity

                        webView = (WebView) findViewById(com.webview.local.R.id.webview);
                        WebSettings webSettings = webView.getSettings();
                        webSettings.setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (url.startsWith("whatsapp://")) {
                    try{
                        view.getContext().startActivity(
                                new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    }
                    catch(Exception e){
                        Toast.makeText(getApplicationContext(), "Você precisa ter o WhatsApp instalado para compartilhar", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }

                if (url.startsWith("market://")) {
                    view.getContext().startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                }

                if (url.startsWith("mailto:")) {
                    view.getContext().startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                }
                if (url.startsWith("http://www.facebook.com")) {
                    view.getContext().startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                }

                else {
                    return false;
                }
            }
        });

                        webView.loadUrl("file:///android_asset/www/index.html");






        Splash = (ImageView)findViewById(com.webview.local.R.id.splash); // determina o ID do imageview que está o splash
        webView = (WebView) findViewById(com.webview.local.R.id.webview);
        mAdView = (AdView)findViewById(com.webview.local.R.id.banner_admob);
        Carregando = (ProgressBar)findViewById(com.webview.local.R.id.loading);


        MobileAds.initialize(this, (getString(com.webview.local.R.string.admob_id))); // função que identifica o ID do APP

        AdRequest adRequest = new AdRequest.Builder() // função que possibilita todas as outas subsequentes do admob
                .addTestDevice(getString(com.webview.local.R.string.admob_test)) // puxa o ID de testador 1 do arquivo strings.xml
                .addTestDevice(getString(com.webview.local.R.string.admob_test_2)) // puxa o ID de testador 2 do arquivo strings.xml
                .build();

        interstitial = new InterstitialAd(this); // cria a função que possibilita a chama do interstitial
        interstitial.setAdUnitId(getString(com.webview.local.R.string.admob_inter)); //chama o ID do interstitial do arquivo strings.xml
        interstitial.loadAd(adRequest); // faz o request pra carregar o interstitial e coloca no cache assim que termina

        mAdView = (AdView) findViewById(com.webview.local.R.id.banner_admob);
        mAdView.loadAd(adRequest);


            interstitial.setAdListener(new AdListener() { // determina tudo que vai ser feito com as ações a seguir

                @Override
                public void onAdLoaded() { // O será feito quando o anúncio estiver carregado?
                    if (!interstitialCanceled) {
                        waitTimer.cancel(); //Quando estiver carregado vai parar o contedor e...
                        interstitial.show(); //... exibir a propaganda
                    }
                }

                @Override
                public void onAdClosed(){  //O que será feito quando FECHAR a propaganda? (clicar no X ou apertar o voltar no android)
                    Splash.setVisibility(View.GONE); // Quando fechar a propaganda, eu vou SUMIR (GONE) com o SPLASH
                    Carregando.setVisibility(View.GONE);
                    webView.setVisibility(View.VISIBLE);// depois de sumir com a splah, mostra o conteúdo do app
                    mAdView.setVisibility(View.VISIBLE);
                    avaliacao();
                }

                @Override
                public void onAdLeftApplication() { //O que será feito quando CLICAR NA PROPAGANDA (aonde aparecia o fantasminha camarada)

                    // Quando clicar na propaganda, eu vou dar uma de GHOSTBUSTER e com o fantasma do splash
                    Splash.setVisibility(View.GONE);
                    webView.setVisibility(View.VISIBLE);// depois de sumir com a splah, mostra o conteúdo do app
                    mAdView.setVisibility(View.VISIBLE);
                    Carregando.setVisibility(View.GONE);
                }
           });


                waitTimer = new Timer(); // inicio do timer
                waitTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        interstitialCanceled = true;

                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() { // O que vai ser feito quando o timer esgotar ou
                                Carregando.setVisibility(View.GONE);
                                Splash.setVisibility(View.GONE); // Quando qualquer função que CANCELE o INTERSTITIAL
                                                                // (como por exemplo não ter internet quando acabar
                                                                // o tempo, vai SUMIR com a imagem do SPLASH e
                                                                // carregar imediatamente a INDEX com o conteúdo, sem tela branca ou preta

                                webView.setVisibility(View.VISIBLE);// depois de sumir com a splah, mostra o conteúdo do app
                                mAdView.setVisibility(View.VISIBLE);

                            }
                        });
                    }
                }, 14000); // Tempo do contador, em milisegundos
            }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            public void run() {
                texto();
            }
        }, 30); // 3000 milliseconds delay
    }




    private void avaliacao() {
        AppRater.app_launched(this, 1, 3);
    }



    private void texto() {

        new AlertDialog.Builder(this)
                .setTitle(com.webview.local.R.string.DialogoSair)
//                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(com.webview.local.R.string.DialogoNao, null)
                .setPositiveButton(com.webview.local.R.string.DialogoSim, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        MainActivity.super.onBackPressed();
                    }
                }).create().show();
    }


}
