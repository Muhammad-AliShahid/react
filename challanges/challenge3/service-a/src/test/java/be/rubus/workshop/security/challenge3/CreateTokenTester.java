package be.rubus.workshop.security.challenge3;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

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

public class CreateTokenTester {

    public static void main(String[] args) {
        System.out.println(new CreateTokenTester().createToken());
    }

    private String keyAsString;

    public String createToken() {
        keyAsString = readPemFile();

        try {
            return generateJWT();
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | JOSEException e) {
            throw new RuntimeException(e);
        }

    }

    private String generateJWT() throws InvalidKeySpecException, NoSuchAlgorithmException, JOSEException {
        JWSSigner signer = new RSASSASigner(createRSAKey());

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject("Rudy")
                .issuer("https://education.io/workshop")
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + 30000)) // 30 Seconds expiration!
                .claim("groups", List.of("user", "protected"))
                .claim("custom-value", "Jessie specific value")
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256).build(),
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

    // NOTE:   Expected format is PKCS#8 (BEGIN PRIVATE KEY) NOT PKCS#1 (BEGIN RSA PRIVATE KEY)
    // See gencerts.sh
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
