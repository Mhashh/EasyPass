package com.maheshtiria.easypass.encryption;

import android.util.Log;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class PassEncrypt {

  public static IvParameterSpec generateIv() {
    byte[] iv = new byte[16];
    new SecureRandom().nextBytes(iv);
    return new IvParameterSpec(iv);
  }

  public static SecretKey getKeyFromPassword(String password,String salt)throws NoSuchAlgorithmException, InvalidKeySpecException {
    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
    return new SecretKeySpec(factory.generateSecret(spec)
      .getEncoded(), "AES");
  }

  //AES/CBC/PKCS5PADDING
  public static String encrypt(String algorithm, String input, SecretKey key,IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
    BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {

    Cipher cipher = Cipher.getInstance(algorithm);
    cipher.init(Cipher.ENCRYPT_MODE, key,iv);
    byte[] cipherText = cipher.doFinal(input.getBytes());
    return Base64.getEncoder()
            .encodeToString(cipherText);
  }

  public static String decrypt(String algorithm, String cipherText, SecretKey key,IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
    BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
    Cipher cipher = Cipher.getInstance(algorithm);
    cipher.init(Cipher.DECRYPT_MODE, key,iv);
    byte[] plainText = cipher.doFinal(Base64.getDecoder()
      .decode(cipherText));
    return new String(plainText);
  }

  public static String storeEncryptAuth(String auth,String pass,String salt,IvParameterSpec iv) {
    try {
      SecretKey key = getKeyFromPassword(pass,salt);
      return  encrypt("AES",auth,key,iv);
    }
    catch (Exception e){
      return "";
    }
  }

  public static String decryptAuth(String auth,String pass,String salt,IvParameterSpec iv) {
    try {
      SecretKey key = getKeyFromPassword(pass,salt);
      return decrypt("AES",auth,key,iv);
    }
    catch (Exception e){
      return "";
    }
  }
}
