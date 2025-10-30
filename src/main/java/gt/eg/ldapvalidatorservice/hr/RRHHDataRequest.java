package gt.eg.ldapvalidatorservice.hr;

import jakarta.validation.constraints.NotBlank;

/**
 * RRHHDataRequest is a record that holds the authentication and data
 * required for the RRHH (Recursos Humanos) module. It contains two
 * nested static records: Authentication and Data.
 */
public record RRHHDataRequest(
        Authentication authentication,
        Data data
) {
    /**
     * Authentication is a static record that holds the token used for
     * authenticating the RRHHDataRequest. It must not be blank.
     */
    public static record Authentication(@NotBlank String token) {}

    /**
     * Data is a static record that holds the codigo (code) used for
     * identifying the data in the RRHH module. It must not be blank.
     */
    public static record Data(@NotBlank String codigo) {}
}
