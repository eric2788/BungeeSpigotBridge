package com.ericlam.mc.bsbridge;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

public class CyberKey {

    private SecretKey secretKey;
    private Cipher cipher;
    
    public CyberKey(File file) throws Exception {
        if (!file.exists()) {
            file.createNewFile();
            try(PrintWriter writer = new PrintWriter(new FileOutputStream(file))){
                KeyGenerator generator = KeyGenerator.getInstance("AES");;
                SecureRandom random = new SecureRandom(); // cryptograph. secure random
                generator.init(random);
                SecretKey secretKey = generator.generateKey();
                String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
                writer.println(encodedKey);
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))){
            String str = "";
            while ((str = reader.readLine()) != null){
                stringBuilder.append(str);
            }
        }
        byte[] key = Base64.getDecoder().decode(stringBuilder.toString());
        secretKey = new SecretKeySpec(key, 0, key.length,"AES");
        cipher = Cipher.getInstance("AES");
    }

    public String encrypt(UUID uuid, UUID randomUUID) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] bytes = cipher.doFinal((uuid.toString() + "_" + randomUUID.toString()).getBytes());
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String encrypt(UUID uuid) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] bytes = cipher.doFinal(uuid.toString().getBytes());
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public UUID[] decrypt(String base64) {
        try{
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] bytes = Base64.getDecoder().decode(base64);
            byte[] decrypted = cipher.doFinal(bytes);
            String[] uuids = new String(decrypted).split("_");
            return new UUID[]{
                    UUID.fromString(uuids[0]),
                    UUID.fromString(uuids[1])
            };
        } catch (Exception e){
            System.out.println("[BungeeSpigotBridge] decryption failed!");
            System.out.println("[BungeeSpigotBridge] Error: " + e);
        }

        return null;
    }

    public boolean verify(String base64, final UUID randomUUID) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] by = Base64.getDecoder().decode(base64);
            byte[] msg = cipher.doFinal(by);
            return randomUUID.toString().equals(new String(msg));
        } catch (Exception e) {
            System.out.println("[BungeeSpigotBridge] decryption failed!");
            System.out.println("[BungeeSpigotBridge] Error: " + e.getMessage());
        }
        return false;
    }
}
