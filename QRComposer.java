package com.example.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Utility to embed a background image over a QR code image using normalized transform parameters.
 *
 * Input assumptions:
 * - canvasWidth/canvasHeight describe the pixel resolution that the front-end used (and that we should use).
 * - transform.normX, normY are top-left corner of the background image relative to the canvas (0..1).
 * - transform.normW is the background displayed width relative to canvas width (0..1).
 * - rotation is degrees clockwise.
 * - anchor.ax, anchor.ay are relative (0..1) coordinates within the background image that are treated as the rotation/scale anchor.
 *
 * Returns a PNG byte[] of the composed image.
 */
public final class QRComposer {

    private QRComposer() {}

    public static class Transform {
        public double normX;      // 0..1 top-left x relative to canvas width
        public double normY;      // 0..1 top-left y relative to canvas height
        public double normW;      // 0..1 width relative to canvas width
        public double rotation;   // degrees clockwise
        public double anchorAx;   // 0..1 inside background image (default 0.5)
        public double anchorAy;   // 0..1
        public float opacity = 1f;// 0..1
        // optionally: normH if non-uniform scaling needed
    }

    /**
     * Compose background image onto QR image using transform parameters.
     *
     * @param qrImageStream   input stream for QR image (any format supported by ImageIO)
     * @param bgImageStream   input stream for background image
     * @param canvasWidth     target canvas width (pixels) used by front-end
     * @param canvasHeight    target canvas height (pixels) used by front-end
     * @param t               transform params (normalized)
     * @return PNG bytes of the composed image
     * @throws IOException on I/O errors
     */
    public static byte[] compose(InputStream qrImageStream,
                                 InputStream bgImageStream,
                                 int canvasWidth,
                                 int canvasHeight,
                                 Transform t) throws IOException {

        // Load images
        BufferedImage qrImg = ImageIO.read(qrImageStream);
        BufferedImage bgImg = ImageIO.read(bgImageStream);
        if (qrImg == null) throw new IOException("Could not read qr image");
        if (bgImg == null) throw new IOException("Could not read background image");

        // Create target canvas at requested resolution.
        // If the provided QR image has different resolution, scale it to canvas size (preserve aspect).
        BufferedImage canvas = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = canvas.createGraphics();
        try {
            // High quality rendering
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw QR as base, scaling to fill canvas (preserve aspect by centering)
            drawImageCenteredFill(g, qrImg, canvasWidth, canvasHeight);

            // Compute absolute placement for bg
            int targetX = (int) Math.round(t.normX * canvasWidth);
            int targetY = (int) Math.round(t.normY * canvasHeight);
            int targetW = (int) Math.round(t.normW * canvasWidth);

            // Determine target height preserving bg aspect ratio
            double bgAspect = (double) bgImg.getHeight() / (double) bgImg.getWidth();
            int targetH = (int) Math.round(targetW * bgAspect);

            // Anchor point in bg pixel coords (within the scaled target)
            double anchorPx = t.anchorAx * targetW;
            double anchorPy = t.anchorAy * targetH;

            // Build AffineTransform:
            // 1) translate to targetX,targetY
            // 2) translate by anchor offset (so rotation/scale about anchor)
            // 3) apply rotation and scale
            AffineTransform tx = new AffineTransform();

            // First translate to target location
            tx.translate(targetX, targetY);

            // Translate to anchor point (so subsequent rotate/scale happens about anchor)
            tx.translate(anchorPx, anchorPy);

            // Rotation about anchor (convert degrees to radians, positive rotates clockwise -> use negative for Java's math)
            double theta = Math.toRadians(t.rotation);
            tx.rotate(theta);

            // Scale to fit targetW/targetH from original bg size
            double sx = (double) targetW / bgImg.getWidth();
            double sy = (double) targetH / bgImg.getHeight();
            tx.scale(sx, sy);

            // After scaling and rotating about anchor, we need to translate back by -anchor in original coords
            tx.translate(-t.anchorAx * bgImg.getWidth(), -t.anchorAy * bgImg.getHeight());

            // Set composite for opacity if needed
            if (t.opacity < 1f) {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0f, Math.min(1f, t.opacity))));
            } else {
                g.setComposite(AlphaComposite.SrcOver);
            }

            // Draw background using transform
            g.drawImage(bgImg, tx, null);

            // Optionally: additional server-side masks / QR-protection, etc.

        } finally {
            g.dispose();
        }

        // Write to PNG bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(canvas, "png", baos);
        return baos.toByteArray();
    }

    // Helper: draw image to fill canvas while preserving aspect ratio (centered)
    private static void drawImageCenteredFill(Graphics2D g, BufferedImage img, int canvasW, int canvasH) {
        double imgAspect = (double) img.getWidth() / img.getHeight();
        double canvasAspect = (double) canvasW / canvasH;

        int drawW, drawH;
        if (imgAspect > canvasAspect) {
            // image is wider -> fit by height
            drawH = canvasH;
            drawW = (int) Math.round(drawH * imgAspect);
        } else {
            // image is taller -> fit by width
            drawW = canvasW;
            drawH = (int) Math.round(drawW / imgAspect);
        }

        int offsetX = (canvasW - drawW) / 2;
        int offsetY = (canvasH - drawH) / 2;
        g.drawImage(img, offsetX, offsetY, drawW, drawH, null);
    }

    // Example usage (for testing)
    public static void main(String[] args) throws Exception {
        try (InputStream qr = new FileInputStream("example_qr.png");
             InputStream bg = new FileInputStream("bg_logo.png")) {

            Transform t = new Transform();
            t.normX = 0.2;
            t.normY = 0.25;
            t.normW = 0.4; // 40% of canvas width
            t.rotation = 12.0;
            t.anchorAx = 0.5;
            t.anchorAy = 0.5;
            t.opacity = 0.95f;

            byte[] out = compose(qr, bg, 1024, 1024, t);
            try (OutputStream fos = new FileOutputStream("composed.png")) {
                fos.write(out);
            }
            System.out.println("Composed image written to composed.png");
        }
    }
}