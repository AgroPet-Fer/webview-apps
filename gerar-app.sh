#!/bin/bash
# ============================================================
#  SCRIPT: Gerar APK e AAB localmente (sem GitHub)
#  
#  Pré-requisitos:
#  - Android Studio instalado
#  - Java 17 instalado
#  - Este script na raiz do projeto
#
#  Como usar:
#  bash gerar-app.sh
# ============================================================

echo ""
echo "📱 GERADOR DE APP ANDROID"
echo "========================="
echo ""

# Verifica se o Gradle está disponível
if [ ! -f "./gradlew" ]; then
    echo "❌ Execute este script na raiz do projeto webview-template"
    exit 1
fi

# Coleta os dados do app
echo "Vou te perguntar algumas coisas sobre o app que quer criar."
echo ""

read -p "📱 Nome do app (ex: Pizzaria do João): " APP_NAME
read -p "🌐 URL do site (com https://): " SITE_URL
read -p "🔖 Package name (ex: com.pizzariadojoao.app): " PACKAGE_NAME
read -p "📁 Caminho do keystore (ex: /home/usuario/minha-chave.jks): " KEYSTORE_PATH
read -p "🏷️  Alias do keystore: " KEY_ALIAS
read -s -p "🔒 Senha do keystore: " STORE_PASS
echo ""
read -s -p "🔒 Senha da chave: " KEY_PASS
echo ""

echo ""
echo "⏳ Personalizando o app '$APP_NAME'..."

# Faz backup do projeto original
cp -r . /tmp/webview-backup-$$ 2>/dev/null

# Substitui os valores
find . -type f \( -name "*.gradle" -o -name "*.xml" -o -name "*.java" \) \
    -exec sed -i.bak \
        -e "s|com\.seuapp\.webview|$PACKAGE_NAME|g" \
        -e "s|https://www\.seusite\.com\.br|$SITE_URL|g" \
        -e "s|Meu App|$APP_NAME|g" \
    {} \;

# Remove backups temporários
find . -name "*.bak" -delete

echo "✅ App personalizado"
echo ""
echo "🔨 Compilando... (pode demorar alguns minutos)"
echo ""

chmod +x gradlew

# Gera APK
./gradlew assembleRelease \
    -PKEYSTORE_FILE="$KEYSTORE_PATH" \
    -PKEYSTORE_PASSWORD="$STORE_PASS" \
    -PKEY_ALIAS="$KEY_ALIAS" \
    -PKEY_PASSWORD="$KEY_PASS"

APK_OK=$?

# Gera AAB
./gradlew bundleRelease \
    -PKEYSTORE_FILE="$KEYSTORE_PATH" \
    -PKEYSTORE_PASSWORD="$STORE_PASS" \
    -PKEY_ALIAS="$KEY_ALIAS" \
    -PKEY_PASSWORD="$KEY_PASS"

AAB_OK=$?

echo ""
if [ $APK_OK -eq 0 ] && [ $AAB_OK -eq 0 ]; then
    PASTA_SAIDA="./saida-$APP_NAME"
    mkdir -p "$PASTA_SAIDA"
    
    cp app/build/outputs/apk/release/*.apk "$PASTA_SAIDA/${APP_NAME}-teste.apk" 2>/dev/null
    cp app/build/outputs/bundle/release/*.aab "$PASTA_SAIDA/${APP_NAME}-playstore.aab" 2>/dev/null
    
    echo "🎉 PRONTO! Arquivos gerados em: $PASTA_SAIDA"
    echo ""
    echo "   📦 APK (para testar no celular): ${APP_NAME}-teste.apk"
    echo "   🏪 AAB (para a Play Store):      ${APP_NAME}-playstore.aab"
    echo ""
else
    echo "❌ Erro ao compilar. Verifique o caminho do keystore e as senhas."
    echo "   Tente usar o GitHub Actions que é mais simples!"
fi
