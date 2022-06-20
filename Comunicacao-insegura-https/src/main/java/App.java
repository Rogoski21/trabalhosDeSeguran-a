import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class App {
    public static void main(String[] args) throws Exception {

        /*
        Mensagem enviada pelo professor descriptografada

        Mensagem 1: Legal Pedro. Agora se conseguir ler esta mensagem, manda ela de volta invertida e cifrada com a mesma senha

        Mensagem 2: Agora é comentar bem o código e submeter com este exemplo completo no início do código como comentário :-)

         */


        BigInteger p = new BigInteger("B10B8F96A080E01DDE92DE5EAE5D54EC52C99FBCFB06A3C69A6A9DCA52D23B616073E28675A23D189838EF1E2EE652C013ECB4AEA906112324975C3CD49B83BFACCBDD7D90C4BD7098488E9C219A73724EFFD6FAE5644738FAA31A4FF55BCCC0A151AF5F0DC8B4BD45BF37DF365C1A65E68CFDA76D4DA708DF1FB2BC2E4A4371", 16);
        BigInteger g = new BigInteger("A4D1CBD5C3FD34126765A442EFB99905F8104DD258AC507FD6406CFF14266D31266FEA1E5C41564B777E690F5504F213160217B4B01B886A5E91547F9E2749F4D7FBD7D3B9A92EE1909D0D2263F80A76A6A24C087A091F531DBF0A0169B6A28AD662A4D18E73AFA32D779D5918D08BC8858F4DCEF97C2A24855E6EEB22B3B2E5", 16);
        //azinho gerado por mim
        BigInteger azinho = new BigInteger("15DE21C670AE7C3F6F3F1F37029303C9", 16);


        String azao = String.format("%x", g.modPow(azinho, p));

        //Bezao do professor
        BigInteger bezao = new BigInteger("01C3EB24A247FD5E63D291BEFD4A7F2C33EF40D2EDAF9A494A33A7E87AB081A6E45817FE0A730BACB2033A9FC9C21F21BB147597F95B76F42297E71B0FDDB717CE70C75A7D539F857A8A24ABF5AC00B0F6DF0D906A3397487DCB56356F3A2A764AB91310F279EBBADE7200B77126EBB30E1883B9BBA57F1F2C034467BE2EFFCE", 16);

        BigInteger vezao = bezao.modPow(azinho, p);


        //message digest por sha para digerir o V que gerará o S (secret key)
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] secretKey = sha.digest(vezao.toByteArray());

        //Criando senha de 16 bytes (128 bits)
        byte[] senhaDeComunicacao = Arrays.copyOf(secretKey, 16);

        //Decoder do texto bezao
        //mensagem enviada pelo professor
        String mensagemDoProfessor = "AF0C784F7978202D2A0E4AD11DBA76C17052081096C590E60A2EA8EAE8DE70CF426007EE29C8A09DD880B4229B5BA5F7314AE87BDB6D6FD8EFD67F57C08286B40E6DD355D50EE49D7F1304BCDC456D23DB9C9EC84E5AC1DAC1742FCB8DBEB0E7EFEA99F9F66D732D3D74D5C994E2286B5AB16A3CA435A0F326982ED24D143801";

        byte[] mensagemDoProfessorDecodificada = Hex.decodeHex(mensagemDoProfessor);


        /*
        Receber uma mensagem do professor (em hexadecimal), cifrada com o AES no modo de operação CBC, e
        padding. Formato da mensagem recebida: [128 bits com IV][mensagem] – em hexadecimal.
         */

        //geramos o nosso IV
        byte[] IV = Arrays.copyOf(mensagemDoProfessorDecodificada, 16);

        //criamos uma secretKeySpec que será nossa senha de comunicacao
        SecretKeySpec secretKeySpec = new SecretKeySpec(senhaDeComunicacao, "AES");


        //Agora vamos Descriptografar o texto
        String textoDescriptografado = Decrypt(secretKeySpec, mensagemDoProfessorDecodificada, IV);
        System.out.println("---------------------------------------------------------");

        //Vamos remover o IV do início do texto para facilitar nossa leitura
        textoDescriptografado = textoDescriptografado.substring(16);

        System.out.println("Agora sem o IV na frente: " + textoDescriptografado);
        System.out.println("---------------------------------------------------------");

        //Invertemos o texto Descriptografado
        String textoDescriptografadoInvertido = new StringBuilder(textoDescriptografado).reverse().toString();

        System.out.println("Texto invertido e sem o IV: " + textoDescriptografadoInvertido);
        System.out.println("---------------------------------------------------------");

        //Vamos gerar um IV randomizado
        var ivGeradoAleatoriamente = geradorDeIv();

        //agora que temos o texto invertido e descriptografado vamos encriptografalo novamente
        var textoEncriptografadoInvertido = encrypt(textoDescriptografadoInvertido, secretKeySpec, ivGeradoAleatoriamente);

        System.out.println("textoEncriptografadoInvertido: " + textoEncriptografadoInvertido);
        System.out.println("---------------------------------------------------------");

        //neste passo foi quando enviei via whatsapp o texto encriptografado ao professor e ele retornou com a nova mensagem

        //nova mensagem do professor
        var novaMensagemDoProf = "16F58277328641C1A375531E6FEFCC59C534A3C2015C5AA1379232331B10AA8DC8DD27E251C174F843C3C156DEEE159EFEEDBAF2A698AEDF8E571BF44D2FEDE14F6426069F8B660AEC2DEA57218A5D86A6812F547EDAC7BF017D9C737FC8CC961A926D9D97D4A3F0E02EA22B0DAE1953992AF08527EACBCF9A79BBB482204431";

        //convertemos para hexadecimal
        byte[] novaMensagemDoProfDecodificada = Hex.decodeHex(novaMensagemDoProf);

        //descriptografamos a nova mensagem
        String novoTextoDescriptografado = Decrypt(secretKeySpec, novaMensagemDoProfDecodificada, IV);
        System.out.println("---------------------------------------------------------");

        //removemos o IV para facilitar a leitura da mensagem final
        novoTextoDescriptografado = novoTextoDescriptografado.substring(16);

        System.out.println("Agora sem o IV na frente: " + novoTextoDescriptografado);

    }

    /*
    Utiliezei um código do github para me auxiliar com a implementação do AES modo CBC com padding
    link: https://github.com/Malrb/Cryptography/blob/master/AES_CBC_PKCS5PADDING.java
     */

    public static String Decrypt(SecretKey secretKey, byte[] cipherText, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
        byte[] decryptedText = cipher.doFinal(cipherText);
        String strDecryptedText = new String(decryptedText);
        System.out.println("texto descriptografado：" + strDecryptedText);
        return strDecryptedText;
    }

    /*
    Como tive problemas com o uso do encrypt do github de referência, utilizei como referência o artigo https://www.baeldung.com/java-aes-encryption-decryption
    para me auxiliar com o método de encrypt.
     */
    public static String encrypt(String input, SecretKey key,
                                 IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, IOException {

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] ivBytes = iv.getIV();
        byte[] cipherText = cipher.doFinal(input.getBytes());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        outputStream.write(ivBytes);
        outputStream.write(cipherText);

        return new String(Hex.encodeHex(outputStream.toByteArray()));
    }


    //Gerador de IV
    public static IvParameterSpec geradorDeIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public static byte[] convertObjectToBytes(Object obj) {
        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        try (ObjectOutputStream ois = new ObjectOutputStream(boas)) {
            ois.writeObject(obj);
            return boas.toByteArray();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        throw new RuntimeException();
    }

}
