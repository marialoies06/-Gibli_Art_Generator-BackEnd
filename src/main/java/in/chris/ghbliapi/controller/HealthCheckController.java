package in.chris.ghbliapi.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class HealthCheckController {

    @Value("${stability.api.base-url}")
    private String stabilityApiUrl;

    @Value("${stability.api.key:NOT_SET}")
    private String apiKey;

    /**
     * Simple health check endpoint to verify backend is running
     * Access at: http://localhost:8080/api/v1/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Ghibli Art Generator API");
        response.put("timestamp", System.currentTimeMillis());
        
        // Check if API key is configured
        boolean apiKeyConfigured = apiKey != null 
                && !apiKey.equals("NOT_SET") 
                && !apiKey.isEmpty() 
                && !apiKey.equals("${STABILITY_API_KEY}");
        
        response.put("stabilityApiConfigured", apiKeyConfigured);
        response.put("stabilityApiUrl", stabilityApiUrl);
        
        if (!apiKeyConfigured) {
            response.put("warning", "Stability API key is not configured. Set STABILITY_API_KEY environment variable.");
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Simple test endpoint that doesn't require API key
     * Access at: http://localhost:8080/api/v1/test
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> test() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Backend is working!");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return ResponseEntity.ok(response);
    }
}
