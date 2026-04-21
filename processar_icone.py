import sys
import os
from PIL import Image, ImageDraw, ImageFont

# Tamanhos necessários para cada densidade de tela Android
TAMANHOS = {
    "mipmap-mdpi":    48,
    "mipmap-hdpi":    72,
    "mipmap-xhdpi":   96,
    "mipmap-xxhdpi":  144,
    "mipmap-xxxhdpi": 192,
}

BASE = "app/src/main/res"
icone_url = sys.argv[1] if len(sys.argv) > 1 else ""

def gerar_icone_padrao(size):
    """Gera um ícone azul padrão com letra A"""
    img = Image.new("RGBA", (size, size), (25, 118, 210, 255))
    draw = ImageDraw.Draw(img)
    m = size // 6
    draw.ellipse([m, m, size - m, size - m], fill=(255, 255, 255, 255))
    try:
        font = ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf", size // 3)
    except:
        font = ImageFont.load_default()
    bb = draw.textbbox((0, 0), "A", font=font)
    tw, th = bb[2] - bb[0], bb[3] - bb[1]
    draw.text(((size - tw) // 2, (size - th) // 2), "A", fill=(25, 118, 210, 255), font=font)
    return img

def fazer_redondo(img, size):
    """Aplica máscara circular na imagem"""
    mask = Image.new("L", (size, size), 0)
    ImageDraw.Draw(mask).ellipse([0, 0, size, size], fill=255)
    result = img.copy().convert("RGBA")
    result.putalpha(mask)
    return result

# Tenta baixar o ícone da URL fornecida
icone_base = None
if icone_url and icone_url.strip():
    try:
        import urllib.request
        print(f"⬇️  Baixando ícone de: {icone_url}")
        urllib.request.urlretrieve(icone_url.strip(), "/tmp/icone_original.png")
        icone_base = Image.open("/tmp/icone_original.png").convert("RGBA")
        print(f"✅ Ícone baixado: {icone_base.size}")
    except Exception as e:
        print(f"⚠️  Não foi possível baixar o ícone: {e}")
        print("   Usando ícone padrão...")
        icone_base = None
else:
    print("ℹ️  Nenhuma URL de ícone fornecida. Usando ícone padrão.")

# Gera os ícones em todos os tamanhos
for folder, size in TAMANHOS.items():
    path = f"{BASE}/{folder}"
    os.makedirs(path, exist_ok=True)

    if icone_base:
        # Redimensiona o ícone personalizado
        img_normal = icone_base.resize((size, size), Image.LANCZOS)
        img_round = fazer_redondo(img_normal, size)
    else:
        # Usa o ícone padrão gerado
        img_normal = gerar_icone_padrao(size)
        img_round = fazer_redondo(img_normal, size)

    img_normal.save(f"{path}/ic_launcher.png", "PNG")
    img_round.save(f"{path}/ic_launcher_round.png", "PNG")
    print(f"  ✅ {folder}: {size}x{size}px")

print("\n🎨 Ícones gerados com sucesso!")
