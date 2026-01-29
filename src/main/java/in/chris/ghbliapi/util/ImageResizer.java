package in.chris.ghbliapi.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class ImageResizer {

    /**
     * Resize image to the nearest SDXL-compatible dimension
     * Valid dimensions: 1024x1024, 1152x896, 1216x832, 1344x768, 1536x640, 640x1536, 768x1344, 832x1216, 896x1152
     */
    public byte[] resizeToSDXL(MultipartFile file) throws IOException {
        BufferedImage originalImage = ImageIO.read(file.getInputStream());

        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        // Determine best SDXL dimensions based on aspect ratio
        int targetWidth;
        int targetHeight;

        double aspectRatio = (double) originalWidth / originalHeight;

        if (aspectRatio >= 1.5) {
            // Wide landscape
            targetWidth = 1536;
            targetHeight = 640;
        } else if (aspectRatio >= 1.3) {
            // Landscape
            targetWidth = 1344;
            targetHeight = 768;
        } else if (aspectRatio >= 1.1) {
            // Slight landscape
            targetWidth = 1152;
            targetHeight = 896;
        } else if (aspectRatio >= 0.9) {
            // Square-ish
            targetWidth = 1024;
            targetHeight = 1024;
        } else if (aspectRatio >= 0.7) {
            // Slight portrait
            targetWidth = 896;
            targetHeight = 1152;
        } else if (aspectRatio >= 0.5) {
            // Portrait
            targetWidth = 768;
            targetHeight = 1344;
        } else {
            // Tall portrait
            targetWidth = 640;
            targetHeight = 1536;
        }

        // Create resized image
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();

        // High-quality resizing
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g.dispose();

        // Convert to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "jpg", baos);
        return baos.toByteArray();
    }
}