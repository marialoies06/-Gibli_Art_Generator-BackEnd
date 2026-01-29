package in.chris.ghbliapi.service;

import in.chris.ghbliapi.client.StabilityAIClient;
import in.chris.ghbliapi.dto.TextToImageRequest;
import in.chris.ghbliapi.util.ImageResizer;
import in.chris.ghbliapi.util.ByteArrayMultipartFile;  // CHANGED
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class GhibliArtService {

    private final StabilityAIClient stabilityAIClient;
    private final String apiKey;
    private final ImageResizer imageResizer;

    public GhibliArtService(StabilityAIClient stabilityAIClient,
                            @Value("${stability.api.key}") String apiKey,
                            ImageResizer imageResizer) {
        this.stabilityAIClient = stabilityAIClient;
        this.apiKey = apiKey;
        this.imageResizer = imageResizer;
    }

    public byte[] createGhibliArt(MultipartFile image, String prompt) {
        try {
            // Resize image to SDXL-compatible dimensions
            byte[] resizedImageBytes = imageResizer.resizeToSDXL(image);
            MultipartFile resizedImage = new ByteArrayMultipartFile(  // CHANGED
                    resizedImageBytes,
                    "image",
                    image.getOriginalFilename(),
                    "image/jpeg"
            );

            String finalPrompt = prompt + ", in the beautiful, detailed anime style of studio ghibli.";
            String engineId = "stable-diffusion-xl-1024-v1-0";
            String stylePreset = "anime";

            return stabilityAIClient.generateImageFromImage(
                    "Bearer " + apiKey,
                    engineId,
                    resizedImage,
                    finalPrompt,
                    stylePreset
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to process image", e);
        }
    }

    public byte[] createGhibliArtFromText(String prompt, String style) {
        String finalPrompt = prompt + ", in the beautiful, detailed anime style of studio ghibli.";
        String engineId = "stable-diffusion-xl-1024-v1-0";
        String stylePreset = style.equals("general") ? "anime" : style.replace("_", "-");

        TextToImageRequest requestPayload = new TextToImageRequest(finalPrompt, stylePreset);

        return stabilityAIClient.generateImageFromText(
                "Bearer " + apiKey,
                engineId,
                requestPayload
        );
    }
}