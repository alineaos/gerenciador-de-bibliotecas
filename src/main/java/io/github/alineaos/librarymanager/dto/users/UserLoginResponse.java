package io.github.alineaos.librarymanager.dto.users;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserLoginResponse(
        @Schema(description = "The access JWT token generated upon successful authentication.",
                example = "eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJsaWJyYXJ5LW1hbmFnZXItYXBpIiwic3ViIjoiYWRtaW5AbGlicmFyeS5jb20iLCJleHAiOjE3ODA1MjEyMjUsImlhdCI6MTc4MDUxNzYyNSwidXNlcklkIjoxLCJzY29wZSI6IkFETUlOIFVTRVIifQ.YXL2qnVD2bcx7sf856dG4GxqG5gHwRlhAYBqqUwFNqexdUaS6_gr7H2ZVx-M6rOu9N7BSA9Z17oTtsIm3TVBpYZAPE4oKzXruwKEMTlgLPIt8XAg36IBoClgQ3NeoA8nz2AtWFJvYXrCVE8ptCrr9SRCjMp2nw_MNs6PoC1ZBVUTFSPLGcPpNWKZSLpfcJp1xhndpWinKwb3xO_bQ3kj_vxVuXqDQSDSojR_yslz5kaPACs4jk5pYzBjJmqoRoNuR_dvV1vx-abk2aB5oZmIWAi59t_W6k8ZnobnCIbuiWoRxklTI3zZ3NLdeSnxoOtVxGIUtBol8bS6PNE6RVH8Wg")
        String accessToken,

        @Schema(description = "The type of the generated token.", example = "Bearer")
        String tokenType,

        @Schema(description = "The token expiration time in seconds.", example = "3600")
        Long expiresIn
) {}
