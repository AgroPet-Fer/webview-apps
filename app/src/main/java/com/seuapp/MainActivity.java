package com.seuapp.webview;

// ============================================================
//  ARQUIVO PRINCIPAL DO APP - É AQUI QUE ACONTECE A MÁGICA
//  ✏️ = partes que você precisa alterar
// ============================================================

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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity {

    // ✏️ ALTERE AQUI: coloque a URL do seu site
    private static final String SITE_URL = "https://www.seusite.com.br";

    // ✏️ ALTERE AQUI: domínios que devem abrir DENTRO do app (separados por vírgula)
    // Links de outros sites serão abertos no navegador do celular
    private static final String[] DOMINIOS_INTERNOS = {
        "seusite.com.br",
        "www.seusite.com.br"
    };

    private WebView webView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefresh;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Conecta as peças visuais ao código
        webView      = findViewById(R.id.webview);
        progressBar  = findViewById(R.id.progressBar);
        swipeRefresh = findViewById(R.id.swipeRefresh);

        configurarWebView();
        configurarPuxarParaAtualizar();

        // Verifica se tem internet antes de carregar
        if (temInternet()) {
            webView.loadUrl(SITE_URL);
        } else {
            mostrarErroSemInternet();
        }
    }

    /** Configura todas as opções do WebView */
    @SuppressLint("SetJavaScriptEnabled")
    private void configurarWebView() {
        WebSettings config = webView.getSettings();

        // Habilita JavaScript (necessário para a maioria dos sites modernos)
        config.setJavaScriptEnabled(true);

        // Permite que o site guarde dados localmente (login, preferências)
        config.setDomStorageEnabled(true);

        // Melhora a exibição em telas de celular
        config.setLoadWithOverviewMode(true);
        config.setUseWideViewPort(true);

        // Permite zoom com dois dedos
        config.setSupportZoom(true);
        config.setBuiltInZoomControls(true);
        config.setDisplayZoomControls(false); // esconde os botões feios de zoom

        // Cache: carrega do cache quando possível (mais rápido)
        config.setCacheMode(WebSettings.LOAD_DEFAULT);

        // Bloqueia conteúdo inseguro (HTTP dentro de HTTPS)
        config.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);

        // Controla a barra de progresso enquanto a página carrega
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progresso) {
                if (progresso < 100) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(progresso);
                } else {
                    progressBar.setVisibility(View.GONE);
                    swipeRefresh.setRefreshing(false);
                }
            }

            // Permite que o site mostre o título do app na barra de status
            @Override
            public void onReceivedTitle(WebView view, String titulo) {
                super.onReceivedTitle(view, titulo);
                // getSupportActionBar().setTitle(titulo); // Descomente se quiser mostrar o título
            }
        });

        // Controla quais links abrem dentro do app e quais vão para o navegador
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();

                // Verifica se é um dos domínios do seu site
                for (String dominio : DOMINIOS_INTERNOS) {
                    if (url.contains(dominio)) {
                        return false; // Abre dentro do app
                    }
                }

                // Trata links especiais (email, telefone, WhatsApp)
                if (url.startsWith("mailto:") || url.startsWith("tel:") ||
                    url.startsWith("whatsapp:") || url.startsWith("intent:")) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    } catch (Exception e) {
                        // Ignora se não conseguir abrir
                    }
                    return true;
                }

                // Qualquer outro link abre no navegador do celular
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    /** Configura o "puxar para atualizar" (swipe down to refresh) */
    private void configurarPuxarParaAtualizar() {
        swipeRefresh.setColorSchemeResources(android.R.color.holo_blue_bright);
        swipeRefresh.setOnRefreshListener(() -> webView.reload());
    }

    /** Verifica se o celular tem conexão com a internet */
    private boolean temInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo rede = cm.getActiveNetworkInfo();
        return rede != null && rede.isConnected();
    }

    /** Mostra uma mensagem quando não há internet */
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

    /** Botão voltar: navega para a página anterior ao invés de fechar o app */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /** Pausa o WebView quando o app vai para segundo plano (economiza bateria) */
    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    /** Retoma o WebView quando o usuário volta ao app */
    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
        // Verifica se voltou a ter internet
        if (!temInternet()) mostrarErroSemInternet();
    }
}
