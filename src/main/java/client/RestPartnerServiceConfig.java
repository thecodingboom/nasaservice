package client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RestPartnerServiceConfig {
    private final String url;
    private final long readTimeoutSeconds;
    private final long connectionTimeoutSeconds;
}
