import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Stack;

public class App {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {


        // chunk do buffer com 1024 bytes
        var input = new BufferedInputStream(new FileInputStream("src/video/FuncoesResumo-SHA1.mp4")); // conteudo
        var pilha = new Stack<byte[]>(); // pilha
        var tamanhoDoBuffer = 1024; // tamanho do buffer
        var buffer = new byte[tamanhoDoBuffer]; // array de bytes com o conteúdo do buffer
        int count = 0;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        //primeira posição do nosso pilha deve ser a posição menor

        while ((count = input.read(buffer, 0, buffer.length)) != -1) {
            if (count != 1024) {
                var parteMenorDoArray = Arrays.copyOfRange(buffer, 0, count);
                //primeira posição do array é menor que 1024 então a ideia é cortar e botar no topo da pilha
                //com o valor partido vamos adicionar em nossa pilha
                pilha.push(parteMenorDoArray);
                continue;
            }
            //Depois de termos empilhado o primeiro elemento da pilha vamos começar a botar o resto dos elementos da pilha
            pilha.push(buffer);
            //Empilhamos e limpamos o buffer para a próxima interação
            buffer = new byte[1024];
        }

        // como vamos ir caminhando pelas posições e fazendo hash vamos primeiro botar o hash inicial em posição
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] messageDigest = sha.digest(pilha.pop());
        System.out.println("Hash inicial: " + bytesToHex(messageDigest));

        //Após a primeira posição vamos ir fazendo o pop da pilha -> depois o digest -> e por fim o sha
        int tamanhoDaPilha = pilha.size();
        for (int i = 0; i < tamanhoDaPilha; i++) {
            byteArrayOutputStream.write(pilha.pop());
            byteArrayOutputStream.write(messageDigest);
            messageDigest = sha.digest(byteArrayOutputStream.toByteArray());
            byteArrayOutputStream.reset();
        }
        //print do resutlado final
        System.out.println("tragédia final: " + bytesToHex(messageDigest));
    }


    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private static byte[] toBytes(char[] chars) {
        CharBuffer charBuffer = CharBuffer.wrap(chars);
        ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(charBuffer);
        byte[] bytes = Arrays.copyOfRange(byteBuffer.array(),
                byteBuffer.position(), byteBuffer.limit());
        Arrays.fill(byteBuffer.array(), (byte) 0); // clear sensitive data
        return bytes;
    }

}
