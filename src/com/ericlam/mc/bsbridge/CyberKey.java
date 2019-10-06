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
    
    public String encrypt(UUID uuid){
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] bytes = cipher.doFinal(uuid.toString().getBytes());
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            e.printStackTrace();
           System.out.println("Encrypted Failed");
        }

        return null;
    }

    public UUID decrypt(String base64){
        try{
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] bytes = Base64.getDecoder().decode(base64);
            byte[] decrypted = cipher.doFinal(bytes);
            return UUID.fromString(new String(decrypted));
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Decrypted failed");
        }

        return null;
    }
}
