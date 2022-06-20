import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {


    public static void main(String[] args) throws IOException {

        String caminhoDoTexto = "texto-ingles.txt";
        String textoCifrado = readFile(caminhoDoTexto);

        System.out.println("Texto criptografado: " + textoCifrado);

        int tamanhoDaChave = procuraTamanhoDaChave(textoCifrado);
        System.out.println("Tamanho da chave: " + tamanhoDaChave);

        String chaveDescoberta = descobreChave(tamanhoDaChave, textoCifrado);
        System.out.println("Chave descoberta: " + chaveDescoberta);

        StringBuilder textoDescriptografado = textoDescriptografado(chaveDescoberta, tamanhoDaChave, textoCifrado);
        System.out.println("Texto descriptografado: " + textoDescriptografado);
    }

    public static StringBuilder textoDescriptografado(String chaveDescoberta, int tamanhoDaChave, String textoCifrado) {
        StringBuilder textoDescriptografado = new StringBuilder();

        for (int i = 0; i < textoCifrado.length(); i++) {

            if ((textoCifrado.charAt(i) - chaveDescoberta.charAt(i % tamanhoDaChave) + 97) < 97) {
                textoDescriptografado.append((char) (textoCifrado.charAt(i) - chaveDescoberta.charAt(i % tamanhoDaChave) + 97 + 26));
            } else {
                textoDescriptografado.append((char) (textoCifrado.charAt(i) - chaveDescoberta.charAt(i % tamanhoDaChave) + 97));
            }
        }
        return textoDescriptografado;
    }

    public static String descobreChave(int tamanhoDaChave, String textoCifrado) {
        StringBuilder chaveDescoberta = new StringBuilder();

        //Caminha pela letra mais frequente do ingles

        //vamos caminhar pelo tamanho da chave
        for (int i = 0; i < tamanhoDaChave; i++) {
            StringBuilder textoCaminhadoPeloTamanhoDeChave = new StringBuilder();
            for (int j = i; j < textoCifrado.length(); j += tamanhoDaChave) {
                //vamos caminhar em relação ao tamanho da chave em noso texto cifrado
                //vamos pegando as ocorrencias de char em relação ao tamanho da chave
                textoCaminhadoPeloTamanhoDeChave.append(textoCifrado.charAt(j));
            }
            char letaMaisFrequente = letraMaisFrequente(textoCaminhadoPeloTamanhoDeChave.toString());
            chaveDescoberta.append(pegaAdistanciaEntreAletraDoInglesXLetraDoTexto(letaMaisFrequente));
        }

        return chaveDescoberta.toString();
    }

    public static char pegaAdistanciaEntreAletraDoInglesXLetraDoTexto(char letraMaisFrequente) {
        char letrasMaisFrequentesNoIngles = 'e';
        char letra;
        var distancia = letraMaisFrequente - letrasMaisFrequentesNoIngles;

        letra = (char) (distancia + 97);

        return letra;
    }

    public static char letraMaisFrequente(String textoCaminhadoPeloTamanhoDeChave) {
        char[] alfabeto = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

        Map<Character, Integer> mapaDaFrequenciaDoTextoCaminhadoPeloTamanhoDaChave = new HashMap<>();
        mapaDaFrequenciaDoTextoCaminhadoPeloTamanhoDaChave = pegaOcorrenciasdoTexto(textoCaminhadoPeloTamanhoDeChave);

        int quantasVezesAletraMaisFrequenteApareceu = 0;
        char letra = 0;

        for (int i = 0; i < alfabeto.length; i++) {
            if (mapaDaFrequenciaDoTextoCaminhadoPeloTamanhoDaChave.get(alfabeto[i]) == null) {
                continue;

            } else if (mapaDaFrequenciaDoTextoCaminhadoPeloTamanhoDaChave.get(alfabeto[i]) > quantasVezesAletraMaisFrequenteApareceu) {
                quantasVezesAletraMaisFrequenteApareceu = mapaDaFrequenciaDoTextoCaminhadoPeloTamanhoDaChave.get(alfabeto[i]);
                letra = alfabeto[i];
            }
        }
        return letra;
    }

    public static int procuraTamanhoDaChave(String textCifrado) {
        int tamanhoDaChave = 0;

        //procura por chave de tamanho até 20 char
        for (int i = 1; i < 20; i++) {
            //var para somarmos todos os ics dos subtextos
            double somaTodosIc = 0;
            // ic do subtexto
            double ic;

            List<String> listaDeSubTextos = divideEmSubTesxto(textCifrado, i);
            //calcula o indice de coincidencia para cada subtexto, pegando da listaDeSubTextos
            for (String subTexto : listaDeSubTextos) {
                Map<Character, Integer> frequenciaDasLetrasNoTexto = pegaOcorrenciasdoTexto(subTexto);
                // para calcular o IC vamos passar o subtexto e a frequencia das letras no texto
                //comparar com o ingles


                ic = calculaIndiceDeCoinidencia(subTexto, frequenciaDasLetrasNoTexto);
                somaTodosIc += ic;
                boolean resultado = comparaIcdoTextoComIngles(somaTodosIc, i);
                if (resultado == true) {
                    tamanhoDaChave = i;
                    return tamanhoDaChave;
                }
            }
        }
        return tamanhoDaChave;
    }

    public static Map<Character, Integer> pegaOcorrenciasdoTexto(String subTexto) {
        Map<Character, Integer> frequenciaDasLetras = new HashMap<>();

        for (int i = 0; i < subTexto.length(); i++) {
            // estamos pegando todas as ocorrencias das letras e suas frequencias no texto
            //caso a letra já esteja mapeada vamos só adicionar ++ na sua frequencia
            if (frequenciaDasLetras.get(subTexto.charAt(i)) != null) {
                int frequenciaAtual = frequenciaDasLetras.get(subTexto.charAt(i));
                frequenciaAtual++;
                frequenciaDasLetras.put(subTexto.charAt(i), frequenciaAtual);
            } else {
                //caso ela não esteja mapeada vamos adicionar sua existênica ao mapa e adicionar sua primeira ocorrencia.
                frequenciaDasLetras.put(subTexto.charAt(i), 1);
            }
        }
        return frequenciaDasLetras;
    }

    public static boolean comparaIcdoTextoComIngles(double somaTodosIc, int i) {
        double indiceDoIngles = 0.065;
        //média do ic do texto
        double mediaIcDoTexto = somaTodosIc / i;

        double resultadoDosIcs = Math.abs(mediaIcDoTexto - indiceDoIngles);
        // o resultadoDosIcs tem que ser menor que 0.001
        if (resultadoDosIcs < 0.01) {
            return true;
        } else {
            return false;
        }
    }

    public static double calculaIndiceDeCoinidencia(String subTexto, Map<Character, Integer> frequenciaDasLetrasNoTexto) {
        // precisamos montar a lista de frequencia da string, para comparar com a mais frequene do ingles

        char[] alfabeto = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        double ic = 0;
        int tamanhoDoAlfabeto = alfabeto.length;
        int tamanhoDoTexto = subTexto.length();


        for (int i = 0; i < tamanhoDoAlfabeto; i++) {
            if (frequenciaDasLetrasNoTexto.get(alfabeto[i]) == null) {
                continue;
            } else {
                long frequencia = frequenciaDasLetrasNoTexto.get(alfabeto[i]);
                ic += (frequencia * (frequencia - 1)) / (double) (tamanhoDoTexto * (tamanhoDoTexto - 1));
            }
        }
        return ic;
    }

    public static List<String> divideEmSubTesxto(String texto, int tamanhoChave) {
        // Guardar os subtextos divididos pelo tamanho da chave
        List<String> subTextos = new ArrayList<>();

        for (int j = 0; j < tamanhoChave; j++) {
            StringBuilder textoDoTamanhoDaChave = new StringBuilder();
            // estamos caminhando pelo texto em relação ao tamanho da chave, OBS I + Tamanho da chave
            for (int i = 0; i + j < texto.length(); i += tamanhoChave) {
                //preciso andar de tamanho da chave em tamanho da chave
                textoDoTamanhoDaChave.append(texto.charAt(i + j));
            }
            subTextos.add(textoDoTamanhoDaChave.toString());
        }
        return subTextos;
    }

    public static String readFile(String Texto) throws IOException {
        //le o arquivo e armazena na variavel global String
        try (BufferedReader br = new BufferedReader(new FileReader(Texto))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                //sb.append(System.lineSeparator());
                line = br.readLine();
            }
            return sb.toString().toLowerCase();
        }
    }


}

