// File: QrComposer.java
package com.example.qr;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.Map;

public class QrComposer {

    public static BufferedImage compose(
            BufferedImage qrImg,
            BufferedImage bgImg,
            Map<String, Object> params
    ) {
        int canvasW = (int) params.get("canvasWidth");
        int canvasH = (int) params.get("canvasHeight");
        int qrSize = (int) params.get("qrSizePx");

        Map<String, Object> transform = (Map<String, Object>) params.get("transform");
        double relX = ((Number) transform.get("relX")).doubleValue();
        double relY = ((Number) transform.get("relY")).doubleValue();
        double relW = ((Number) transform.get("relW")).doubleValue();
        double rotation = ((Number) transform.get("rotation")).doubleValue();
        double opacity = ((Number) transform.get("opacity")).doubleValue();

        // Create canvas
        BufferedImage canvas = new BufferedImage(canvasW, canvasH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = canvas.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // ---- 1. Draw background with transform ----
        double bgTargetW = qrSize * relW;
        double scale = bgTargetW / bgImg.getWidth();
        double bgTargetH = bgImg.getHeight() * scale;

        int qrX = (canvasW - qrSize) / 2;
        int qrY = (canvasH - qrSize) / 2;

        double bgX = qrX + relX * qrSize;
        double bgY = qrY + relY * qrSize;

        AffineTransform tx = new AffineTransform();
        tx.translate(bgX, bgY);
        tx.rotate(Math.toRadians(rotation), bgTargetW / 2.0, bgTargetH / 2.0);
        tx.scale(scale, scale);

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) opacity));
        g.drawImage(bgImg, tx, null);

        // ---- 2. Draw QR fixed in center ----
        int qrDrawX = (canvasW - qrSize) / 2;
        int qrDrawY = (canvasH - qrSize) / 2;
        g.setComposite(AlphaComposite.SrcOver);
        g.drawImage(qrImg, qrDrawX, qrDrawY, qrSize, qrSize, null);

        g.dispose();
        return canvas;
    }

    // Example usage
    public static void main(String[] args) throws Exception {
        // Load QR (from file or stream)
        BufferedImage qr = ImageIO.read(new File("qr.png"));
        BufferedImage bg = ImageIO.read(new File("bg.png"));

        Map<String, Object> params = Map.of(
                "canvasWidth", 1024,
                "canvasHeight", 1024,
                "qrSizePx", 720,
                "transform", Map.of(
                        "relX", 0.10,
                        "relY", 0.15,
                        "relW", 0.35,
                        "rotation", 12,
                        "anchorAx", 0.5,
                        "anchorAy", 0.5,
                        "opacity", 0.95
                )
        );

        BufferedImage result = compose(qr, bg, params);
        ImageIO.write(result, "PNG", new File("result.png"));
    }
}