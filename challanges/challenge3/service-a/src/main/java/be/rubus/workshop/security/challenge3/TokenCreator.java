package be.rubus.workshop.security.challenge3;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@ApplicationScoped
public class TokenCreator {

    @Inject
    private SecurityContext securityContext;

    private String keyAsString;

    @PostConstruct
    public void init() {
        keyAsString = readPemFile();
    }


    public String createToken() {
        if (keyAsString == null) {
            throw new RuntimeException("Unable to read privateKey.pem");
        }
        try {
            return generateJWT();
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateJWT() throws InvalidKeySpecException, NoSuchAlgorithmException, JOSEException {
        JWSSigner signer = new RSASSASigner(createRSAKey());

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(securityContext.getCallerPrincipal().getName())
                .issuer("https://education.io/workshop")
                .issueTime(new Date())
                .jwtID(UUID.randomUUID().toString())
                .expirationTime(new Date(System.currentTimeMillis() + 30000)) // 30 Seconds expiration!
                .claim("groups", List.of("user", "protected"))
                .claim("custom-value", "Jessie specific value")
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256)
                        .type(JOSEObjectType.JWT)
                        .build(),
                claimsSet);

        // Compute the RSA signature
        signedJWT.sign(signer);

        return signedJWT.serialize();
    }

    private PrivateKey createRSAKey() throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] encoded = Base64.getDecoder().decode(keyAsString);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        return keyFactory.generatePrivate(keySpec);
    }

    private static String readPemFile() {
        StringBuilder sb = new StringBuilder(8192);
        try (BufferedReader is = new BufferedReader(
                new InputStreamReader(
                        TokenCreator.class.getResourceAsStream("/privateKey.pem"), StandardCharsets.US_ASCII))) {
            String line;
            while ((line = is.readLine()) != null) {
                if (!line.startsWith("-")) {
                    sb.append(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
