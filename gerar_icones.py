from PIL import Image, ImageDraw, ImageFont
import os

sizes = {
    "mipmap-mdpi": 48,
    "mipmap-hdpi": 72,
    "mipmap-xhdpi": 96,
    "mipmap-xxhdpi": 144,
    "mipmap-xxxhdpi": 192,
}

for folder, size in sizes.items():
    path = f"app/src/main/res/{folder}"
    os.makedirs(path, exist_ok=True)
    for name in ["ic_launcher.png", "ic_launcher_round.png"]:
        dest = f"{path}/{name}"
        if os.path.exists(dest):
            continue
        img = Image.new("RGBA", (size, size), (25, 118, 210, 255))
        draw = ImageDraw.Draw(img)
        m = size // 6
        draw.ellipse([m, m, size - m, size - m], fill=(255, 255, 255, 255))
        try:
            font = ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf", size // 3)
        except:
            font = ImageFont.load_default()
        bb = draw.textbbox((0, 0), "A", font=font)
        draw.text(((size - (bb[2] - bb[0])) // 2, (size - (bb[3] - bb[1])) // 2), "A", fill=(25, 118, 210, 255), font=font)
        if "round" in name:
            mask = Image.new("L", (size, size), 0)
            ImageDraw.Draw(mask).ellipse([0, 0, size, size], fill=255)
            img.putalpha(mask)
        img.save(dest, "PNG")
        print(f"Gerado: {dest}")

print("Icones OK")
