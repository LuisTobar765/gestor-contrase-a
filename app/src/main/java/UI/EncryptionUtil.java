package UI;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtil {
    // Método para cifrar una cadena usando AES
    public static String encrypt(String data, String key) throws Exception {
        // Crear la clave secreta basada en la longitud requerida por AES (16 caracteres)
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");

        // Configurar el cifrado AES en modo ECB
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        // Cifrar los datos y convertir a Base64
        byte[] encryptedData = cipher.doFinal(data.getBytes());
        return Base64.encodeToString(encryptedData, Base64.DEFAULT);
    }
}
