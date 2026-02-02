package fahchouch.fixMyCity.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class PublicKeyProvider {
    private final AtomicReference<PublicKey> cachedKey = new AtomicReference<>();
    private final WebClient webClient;
    private final String publicKeyUrl;

    public PublicKeyProvider(
            WebClient.Builder builder,
            @Value("${security.jwt.public-key-url}") String publicKeyUrl
    ) {
        this.webClient = builder.build();
        this.publicKeyUrl = publicKeyUrl;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadKeyOnStartup() {
        PublicKey key = fetchKey();
        cachedKey.set(key);
    }

    public PublicKey getKey() {
        PublicKey key = cachedKey.get();
        if (key == null) {
            throw new IllegalStateException("JWT public key not loaded yet");
        }
        return key;
    }

    private PublicKey fetchKey() {
        String base64 = webClient.post()
                .uri(publicKeyUrl)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if (base64 == null || base64.isBlank()) {
            throw new IllegalStateException("Public key endpoint returned empty key");
        }

        try {
            byte[] decoded = Base64.getDecoder().decode(base64);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
            return KeyFactory.getInstance("RSA").generatePublic(spec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decode RSA public key", e);
        }
    }
}
