import sys, os, urllib.request, urllib.error
from PIL import Image, ImageDraw, ImageFont

TAMANHOS = {
    "mipmap-mdpi":    48,
    "mipmap-hdpi":    72,
    "mipmap-xhdpi":   96,
    "mipmap-xxhdpi":  144,
    "mipmap-xxxhdpi": 192,
}
BASE = "app/src/main/res"

icone_url = sys.argv[1].strip() if len(sys.argv) > 1 else ""

def gerar_padrao(size):
    img = Image.new("RGBA", (size, size), (25, 118, 210, 255))
    draw = ImageDraw.Draw(img)
    m = size // 6
    draw.ellipse([m, m, size-m, size-m], fill=(255,255,255,255))
    try:
        font = ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf", size//3)
    except:
        font = ImageFont.load_default()
    bb = draw.textbbox((0,0), "A", font=font)
    draw.text(((size-(bb[2]-bb[0]))//2, (size-(bb[3]-bb[1]))//2), "A", fill=(25,118,210,255), font=font)
    return img

def fazer_redondo(img, size):
    mask = Image.new("L", (size, size), 0)
    ImageDraw.Draw(mask).ellipse([0, 0, size, size], fill=255)
    result = img.copy().convert("RGBA")
    result.putalpha(mask)
    return result

# Prioridade 1: arquivo icone.png na raiz do repositório
icone_base = None
if os.path.exists("icone.png"):
    print("✅ Usando icone.png da raiz do repositório")
    icone_base = Image.open("icone.png").convert("RGBA")

# Prioridade 2: URL fornecida no workflow
elif icone_url:
    print(f"⬇️  Baixando ícone de: {icone_url}")
    try:
        req = urllib.request.Request(icone_url, headers={
            "User-Agent": "Mozilla/5.0"
        })
        with urllib.request.urlopen(req, timeout=15) as resp:
            with open("/tmp/icone_download.png", "wb") as f:
                f.write(resp.read())
        icone_base = Image.open("/tmp/icone_download.png").convert("RGBA")
        print(f"✅ Ícone baixado: {icone_base.size}")
    except Exception as e:
        print(f"⚠️  Erro ao baixar URL: {e}")
        print("   Usando ícone padrão azul.")

# Prioridade 3: ícone padrão
else:
    print("ℹ️  Sem ícone fornecido. Usando padrão azul.")

# Gera os ícones em todos os tamanhos
for folder, size in TAMANHOS.items():
    path = f"{BASE}/{folder}"
    os.makedirs(path, exist_ok=True)

    if icone_base:
        img_n = icone_base.resize((size, size), Image.LANCZOS)
    else:
        img_n = gerar_padrao(size)

    img_r = fazer_redondo(img_n, size)
    img_n.save(f"{path}/ic_launcher.png", "PNG")
    img_r.save(f"{path}/ic_launcher_round.png", "PNG")
    print(f"  ✅ {folder} ({size}px)")

print("\n🎨 Ícones prontos!")
