package UI;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import android.util.Base64;

public class EncryptionUtil {

    public static String encrypt(String data, String secretKey) throws Exception {
        // Convertir la clave en un objeto SecretKeySpec
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), "AES");

        // Configurar el cifrador AES
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);

        // Cifrar la contraseña
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());

        // Codificar el resultado en Base64 y devolverlo
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
    }
}
