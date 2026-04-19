# 📱 WebView App Template — Guia Completo

> Transforme qualquer site em app Android em minutos, sem saber programar.

---

## 📋 O QUE VOCÊ PRECISA (só uma vez)

| Item | Para que serve | Custo |
|------|---------------|-------|
| [Android Studio](https://developer.android.com/studio) | Programa para criar apps | Grátis |
| [Conta GitHub](https://github.com) | Guarda e compila o código | Grátis |
| [Conta Google Play](https://play.google.com/console) | Publica o app | $25 (taxa única) |
| Java (JDK 17) | Linguagem do Android | Grátis |

---

## 🚀 INÍCIO RÁPIDO (passo a passo)

### ETAPA 1 — Configurar o GitHub (só faz uma vez)

1. Acesse **github.com** e crie uma conta gratuita
2. Clique em **"New repository"** (botão verde)
3. Nome: `meus-apps-android` → clique **"Create repository"**
4. Faça upload de todos os arquivos desta pasta

### ETAPA 2 — Criar sua Chave de Assinatura (só faz uma vez)

A chave é como uma "assinatura digital" do seu app. **Guarde-a com cuidado!**

**No Mac ou Linux:**
```bash
bash criar-keystore.sh
```

**No Windows:**
```
Abra o CMD e execute:
keytool -genkey -v -keystore meu-keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias minha-chave
```

O script vai te guiar e no final mostrar as informações para configurar no GitHub.

### ETAPA 3 — Adicionar Secrets no GitHub

Os "secrets" guardam suas senhas de forma segura.

1. No seu repositório GitHub, clique em **Settings**
2. No menu esquerdo: **Secrets and variables** → **Actions**
3. Clique em **"New repository secret"** e adicione:

| Nome do Secret | O que colocar |
|---------------|---------------|
| `KEYSTORE_BASE64` | O texto base64 gerado pelo script |
| `KEYSTORE_PASSWORD` | A senha que você criou |
| `KEY_ALIAS` | O alias (apelido) da chave |
| `KEY_PASSWORD` | A mesma senha |

### ETAPA 4 — Gerar seu Primeiro App

1. No seu repositório GitHub, clique em **"Actions"**
2. Clique em **"Gerar APK e AAB"**
3. Clique em **"Run workflow"**
4. Preencha os campos:
   - **Nome do app**: Ex: `Pizzaria do João`
   - **Identificador único**: Ex: `com.pizzariadojoao.app`
   - **URL do site**: Ex: `https://www.pizzariadojoao.com.br`
   - **Domínio**: Ex: `pizzariadojoao.com.br`
5. Clique em **"Run workflow"** (botão verde)
6. Aguarde 3-5 minutos
7. Clique no job finalizado → role até **"Artifacts"**
8. Baixe o **APK** (para testar) e o **AAB** (para publicar)

---

## 📤 PUBLICAR NA PLAY STORE

### Preparar o app no Play Console

1. Acesse [play.google.com/console](https://play.google.com/console)
2. Clique em **"Criar app"**
3. Preencha: nome, idioma, tipo (app), gratuito/pago
4. Clique em **"Criar app"**

### Subir o AAB

1. No menu esquerdo: **Produção** → **Releases**
2. Clique em **"Criar nova versão"**
3. Faça upload do arquivo **.aab** que você baixou
4. Escreva o que há de novo (ex: "Primeira versão")
5. Clique em **"Salvar"** → **"Revisar versão"**

### Preencher as fichas obrigatórias

O Play Console vai pedir:
- ✅ **Descrição do app** (o que o app faz)
- ✅ **Capturas de tela** (pelo menos 2, tamanho: 1080x1920)
- ✅ **Ícone do app** (512x512 pixels, PNG)
- ✅ **Classificação indicativa** (responder um questionário)
- ✅ **Política de privacidade** (URL de uma página no seu site)

> 💡 **Dica fácil para capturas de tela:** Instale o APK no celular, tire prints e recorte.

### Publicar

1. Após preencher tudo: **Publicar app**
2. O Google vai revisar (pode levar de algumas horas a alguns dias)
3. Quando aprovado, o app aparece na Play Store! 🎉

---

## 🔁 CRIAR OUTRO APP (para um novo cliente)

É muito simples! Só repita a **Etapa 4** com os dados do novo cliente:

```
Nome: App da Padaria Silva
Identificador: com.padariasiva.app
URL: https://www.padariasilva.com.br
Domínio: padariasilva.com.br
```

Cada app gerado fica salvo nos Artifacts do GitHub por 30 dias.

---

## 📁 ORGANIZAÇÃO PARA VÁRIOS CLIENTES

Sugestão de organização:

```
📁 Clientes/
├── 📁 pizzaria-joao/
│   ├── 🔑 keystore.jks
│   ├── 📄 dados.txt (package name, URL, senhas)
│   ├── 📦 app-release.apk
│   └── 📦 app-release.aab
├── 📁 padaria-silva/
│   ├── 🔑 keystore.jks
│   └── ...
└── 📁 loja-maria/
    └── ...
```

**Uma chave por cliente** = cada app é independente e seguro.

---

## 🔧 PERSONALIZAR O APP

### Trocar a cor principal
Abra `app/src/main/res/values/colors.xml` e altere:
```xml
<color name="colorPrimary">#FF5722</color>  ← cor em código hex
```

Use [coolors.co](https://coolors.co) para escolher cores em hex.

### Trocar o ícone
1. Crie uma imagem PNG 512x512 com o logo do cliente
2. Use o site [makeappicon.com](https://makeappicon.com)
3. Substitua os arquivos `ic_launcher.png` nas pastas `mipmap-*`

### Permitir que o app gire a tela
No `AndroidManifest.xml`, remova ou altere:
```xml
android:screenOrientation="portrait"  ← remova esta linha
```

---

## 🆚 COMPARAÇÃO: WebView vs PWA

| | **WebView App** (este template) | **PWA** (alternativa) |
|---|---|---|
| **O que é** | App nativo com site dentro | Site que "vira" app |
| **Dificuldade** | Média (este guia) | Fácil |
| **Aparece na Play Store** | ✅ Sim | ✅ Sim (via TWA) |
| **Funciona offline** | ❌ Não | ✅ Se configurado |
| **Notificações push** | Com código extra | ✅ Nativo |
| **Controle da assinatura** | ✅ Total | ✅ Total |
| **Ferramenta PWA** | — | [PWABuilder.com](https://pwabuilder.com) |

> 💡 **Quando usar PWA:** Se o site já tem PWA configurado (manifest.json), use **pwabuilder.com** — é ainda mais simples.

---

## 🆘 PROBLEMAS COMUNS

### "Build failed" no GitHub Actions
- Verifique se os 4 secrets estão configurados corretamente
- Certifique-se que o package name usa apenas letras, números e pontos

### "Package name já existe" no Play Console
- O identificador (ex: `com.pizzariadojoao.app`) deve ser único no mundo
- Adicione um número: `com.pizzariadojoao.app2`

### O app abre mas mostra página em branco
- Verifique se a URL está correta (com `https://`)
- Teste a URL no navegador do celular

### Keystore perdido
- 🚨 Crie um novo keystore e um novo app no Play Console
- Não tem como recuperar um keystore perdido
- **Sempre faça backup no Google Drive!**

---

## 📞 FERRAMENTAS ÚTEIS

| Ferramenta | Para que serve | Link |
|-----------|---------------|------|
| PWABuilder | Alternativa: converte PWA para Play Store | pwabuilder.com |
| Gonative.io | Alternativa paga ao Median | gonative.io |
| AppMySite | Alternativa fácil | appmysite.com |
| Makeappicon | Gerar ícones nos tamanhos certos | makeappicon.com |
| Coolors | Escolher cores | coolors.co |
| Privacy Policy Generator | Gerar política de privacidade | privacypolicygenerator.info |

---

*Template criado para facilitar a publicação de apps WebView na Play Store.*
*Atualizado em 2025.*
