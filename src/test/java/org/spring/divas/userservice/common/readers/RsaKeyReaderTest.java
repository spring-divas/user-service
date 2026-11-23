package org.spring.divas.userservice.common.readers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPairGenerator;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class RsaKeyReaderTest {
    @TempDir
    Path directory;

    @Test
    void readsPublicAndPrivatePemKeys() throws Exception {
        var generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        var pair = generator.generateKeyPair();
        Path publicPath = directory.resolve("public.pem");
        Path privatePath = directory.resolve("private.pem");
        Files.writeString(publicPath, pem("PUBLIC KEY", pair.getPublic().getEncoded()));
        Files.writeString(privatePath, pem("PRIVATE KEY", pair.getPrivate().getEncoded()));

        assertThat(RsaKeyReader.readPublicKey(publicPath.toString()).getEncoded())
                .containsExactly(pair.getPublic().getEncoded());
        assertThat(RsaKeyReader.readPrivateKey(privatePath.toString()).getEncoded())
                .containsExactly(pair.getPrivate().getEncoded());
    }

    @Test
    void rejectsMissingKeyFile() {
        org.assertj.core.api.Assertions.assertThatThrownBy(() ->
                RsaKeyReader.readPublicKey(directory.resolve("absent.pem").toString()))
                .isInstanceOf(java.io.IOException.class);
    }

    private static String pem(String type, byte[] encoded) {
        return "-----BEGIN " + type + "-----\n" + Base64.getMimeEncoder(64, new byte[]{'\n'})
                .encodeToString(encoded) + "\n-----END " + type + "-----\n";
    }
}
