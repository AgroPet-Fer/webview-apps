package com.seuapp.webview;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String SITE_URL = "https://www.seusite.com.br";
    private static final String[] DOMINIOS_INTERNOS = {
        "seusite.com.br",
        "www.seusite.com.br"
    };

    // ✏️ true = mostra barra de progresso | false = oculta
    private static final boolean MOSTRAR_BARRA_PROGRESSO = true;

    private WebView webView;
    private ProgressBar progressBar;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView     = findViewById(R.id.webview);
        progressBar = findViewById(R.id.progressBar);

        configurarWebView();

        if (temInternet()) {
            webView.loadUrl(SITE_URL);
        } else {
            mostrarErroSemInternet();
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void configurarWebView() {
        WebSettings config = webView.getSettings();
        config.setJavaScriptEnabled(true);
        config.setDomStorageEnabled(true);
        config.setLoadWithOverviewMode(true);
        config.setUseWideViewPort(true);
        config.setSupportZoom(true);
        config.setBuiltInZoomControls(true);
        config.setDisplayZoomControls(false);
        config.setCacheMode(WebSettings.LOAD_DEFAULT);
        config.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progresso) {
                if (!MOSTRAR_BARRA_PROGRESSO) {
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (progresso < 100) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(progresso);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                for (String dominio : DOMINIOS_INTERNOS) {
                    if (url.contains(dominio)) return false;
                }
                if (url.startsWith("mailto:") || url.startsWith("tel:") ||
                    url.startsWith("whatsapp:") || url.startsWith("intent:")) {
                    try { startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url))); }
                    catch (Exception ignored) {}
                    return true;
                }
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }
        });
    }

    private boolean temInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo rede = cm.getActiveNetworkInfo();
        return rede != null && rede.isConnected();
    }

    private void mostrarErroSemInternet() {
        new AlertDialog.Builder(this)
            .setTitle("Sem conexão")
            .setMessage("Verifique sua conexão com a internet e tente novamente.")
            .setPositiveButton("Tentar novamente", (d, w) -> {
                if (temInternet()) webView.loadUrl(SITE_URL);
                else mostrarErroSemInternet();
            })
            .setNegativeButton("Fechar", (d, w) -> finish())
            .setCancelable(false)
            .show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override protected void onPause()  { super.onPause();  webView.onPause();  }
    @Override protected void onResume() { super.onResume(); webView.onResume(); if (!temInternet()) mostrarErroSemInternet(); }
}
