#!/bin/bash
# ============================================================
#  SCRIPT: Criar Chave de Assinatura (Keystore)
#  
#  O que faz: Cria a "assinatura digital" do seu app.
#  IMPORTANTE: Guarde o arquivo .jks gerado em lugar seguro!
#  Sem ele, você não pode atualizar o app na Play Store.
#
#  Como usar:
#  1. Abra o Terminal (Mac/Linux) ou Git Bash (Windows)
#  2. Navegue até a pasta onde quer salvar: cd ~/meus-apps
#  3. Execute: bash criar-keystore.sh
# ============================================================

echo ""
echo "🔑 CRIADOR DE CHAVE DE ASSINATURA"
echo "=================================="
echo ""
echo "Vou te fazer algumas perguntas para criar sua chave."
echo "Anote as respostas — você vai precisar delas!"
echo ""

# Coleta as informações
read -p "👤 Seu nome ou nome da empresa: " NOME
read -p "📱 Nome do app (ex: Loja da Maria): " NOME_APP
read -p "🏙️ Sua cidade: " CIDADE
read -p "🗺️ Seu estado (sigla, ex: RS, SP, RJ): " ESTADO
read -p "🌍 País (BR para Brasil): " PAIS
PAIS=${PAIS:-BR}

# Cria um nome de arquivo baseado no app
NOME_ARQUIVO=$(echo "$NOME_APP" | tr '[:upper:]' '[:lower:]' | tr ' ' '-' | tr -cd '[:alnum:]-')
ARQUIVO_KEYSTORE="${NOME_ARQUIVO}-keystore.jks"
ALIAS="${NOME_ARQUIVO}-key"

echo ""
echo "🔐 Agora escolha uma SENHA FORTE para proteger sua chave."
echo "   ⚠️  ANOTE ESSA SENHA! Se perder, não consegue atualizar o app."
echo ""
read -s -p "Senha (não aparece ao digitar): " SENHA
echo ""
read -s -p "Confirme a senha: " SENHA2
echo ""

if [ "$SENHA" != "$SENHA2" ]; then
    echo "❌ As senhas não coincidem. Tente novamente."
    exit 1
fi

if [ ${#SENHA} -lt 6 ]; then
    echo "❌ A senha precisa ter pelo menos 6 caracteres."
    exit 1
fi

echo ""
echo "⏳ Criando sua chave de assinatura..."
echo ""

# Cria o keystore
keytool -genkey -v \
    -keystore "$ARQUIVO_KEYSTORE" \
    -keyalg RSA \
    -keysize 2048 \
    -validity 10000 \
    -alias "$ALIAS" \
    -storepass "$SENHA" \
    -keypass "$SENHA" \
    -dname "CN=$NOME, OU=Dev, O=$NOME, L=$CIDADE, S=$ESTADO, C=$PAIS"

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ CHAVE CRIADA COM SUCESSO!"
    echo ""
    echo "════════════════════════════════════════"
    echo "📋 ANOTE ESTAS INFORMAÇÕES (muito importante):"
    echo "════════════════════════════════════════"
    echo ""
    echo "   📁 Arquivo keystore: $ARQUIVO_KEYSTORE"
    echo "   🏷️  Alias (apelido): $ALIAS"
    echo "   🔒 Senha: (a que você digitou acima)"
    echo ""
    echo "════════════════════════════════════════"
    echo ""
    echo "📤 Para usar no GitHub Actions, converta para Base64:"
    echo ""
    
    # Converte para base64 para uso no GitHub Secrets
    BASE64=$(base64 -w 0 "$ARQUIVO_KEYSTORE" 2>/dev/null || base64 "$ARQUIVO_KEYSTORE")
    echo "   base64 do keystore (copie tudo abaixo):"
    echo ""
    echo "$BASE64"
    echo ""
    echo "════════════════════════════════════════"
    echo ""
    echo "📝 Configure esses 4 secrets no GitHub:"
    echo "   KEYSTORE_BASE64  → (o texto base64 acima)"
    echo "   KEYSTORE_PASSWORD → $SENHA"
    echo "   KEY_ALIAS        → $ALIAS"
    echo "   KEY_PASSWORD     → $SENHA"
    echo ""
    echo "⚠️  IMPORTANTE: Faça BACKUP do arquivo $ARQUIVO_KEYSTORE"
    echo "    Coloque em Google Drive, Dropbox ou pen drive!"
    echo ""
else
    echo ""
    echo "❌ Erro ao criar a chave. Verifique se o Java está instalado."
    echo "   Instale em: https://adoptium.net/"
fi
