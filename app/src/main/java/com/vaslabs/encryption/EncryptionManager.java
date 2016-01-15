package com.vaslabs.encryption;

import android.content.Context;
import android.util.Base64;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by vnicolaou on 15/12/15.
 */
public class EncryptionManager {
    private final String privateKey = "rsa";
    private final String publicKey = "rsa.pub";

    public void generateKeys(Context context) throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.genKeyPair();
        save(context, kp);
    }

    private void save(Context context, KeyPair kp) throws Exception {
        KeyFactory fact = KeyFactory.getInstance("RSA");
        RSAPublicKeySpec pub = fact.getKeySpec(kp.getPublic(),
                RSAPublicKeySpec.class);
        RSAPrivateKeySpec priv = fact.getKeySpec(kp.getPrivate(),
                RSAPrivateKeySpec.class);

        saveToFile(context, publicKey, pub.getModulus(),
                pub.getPublicExponent());
        saveToFile(context, privateKey, priv.getModulus(),
                priv.getPrivateExponent());
    }

    private void saveToFile(Context context, String fileName,
                            BigInteger mod, BigInteger exp) throws IOException {
        ObjectOutputStream oout = new ObjectOutputStream(
                new BufferedOutputStream(context.openFileOutput(fileName, Context.MODE_PRIVATE)));
        try {
            oout.writeObject(mod);
            oout.writeObject(exp);
        } catch (Exception e) {
            throw new IOException("Unexpected error", e);
        } finally {
            oout.close();
        }
    }

    public String decrypt(byte[] data, Context context) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, IOException {
        PrivateKey privateKey = getPrivateKey(context);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] cipherData = cipher.doFinal(data);
        return new String(cipherData);
    }

    private PrivateKey getPrivateKey(Context context) throws IOException {
        return readrivateKeyFromInputStream(context.openFileInput(this.privateKey));
    }

    private PrivateKey readrivateKeyFromInputStream(InputStream in) throws IOException {
        ObjectInputStream oin =
                new ObjectInputStream(new BufferedInputStream(in));
        try {
            BigInteger m = (BigInteger) oin.readObject();
            BigInteger e = (BigInteger) oin.readObject();
            RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(m, e);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = fact.generatePrivate(keySpec);
            return privateKey;
        } catch (Exception e) {
            throw new RuntimeException("Serialisation error", e);
        } finally {
            oin.close();
        }
    }

    public PublicKey getPublicKey(Context context) throws IOException {
        return readPublicKeyFromInputStream(context.openFileInput(this.publicKey));
    }

    private PublicKey readPublicKeyFromInputStream(InputStream in) throws IOException {

        ObjectInputStream oin =
                new ObjectInputStream(new BufferedInputStream(in));
        try {
            BigInteger m = (BigInteger) oin.readObject();
            BigInteger e = (BigInteger) oin.readObject();
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m, e);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            PublicKey pubKey = fact.generatePublic(keySpec);
            return pubKey;
        } catch (Exception e) {
            throw new RuntimeException("Serialisation error", e);
        } finally {
            oin.close();
        }
    }

    public static String encodePublicKey(PublicKey key) {
        String encoded = Base64.encodeToString(key.getEncoded(), Base64.DEFAULT);
        return encoded;
    }

    public String decrypt(String data, Context context) throws Exception {
        String decryptedData = this.decrypt(Base64.decode(data, Base64.DEFAULT), context);
        return decryptedData;
    }

}
