package rna;

/**
 * Classe dedicada para guardar as funções de ativação da rede neural
 */
public class Ativacoes{
   private static double alfaLeakyRelu = 0.01;
   private static double alfaElu = 0.01;

   public static double relu(double valor){
      if(valor < 0) return 0;
      return valor;
   }


   public static double reluDx(double valor){
      if(valor < 0) return 0;
      return 1;     
   }


   public static double sigmoid(double valor){
      return (1 / (1 + Math.exp(-valor)));
   }


   public static double sigmoidDx(double valor){
      return (sigmoid(valor) * (1-sigmoid(valor)));
   }


   public static double tanH(double valor){
      return Math.tanh(valor);
   }


   public static double tanHDx(double valor){
      double resultado = Math.tanh(valor);
      return (1 - Math.pow(resultado, 2));
   }


   public static double leakyRelu(double valor){
      if(valor > 0) return valor;
      else return ((alfaLeakyRelu) * valor);
   }


   public static double leakyReluDx(double valor){
      if(valor > 0) return 1;
      else return alfaLeakyRelu;
   }


   public static double elu(double valor){
      if(valor > 0) return valor;
      else return (alfaElu * (Math.exp(valor)-1));
   }


   public static double eluDx(double valor){
      if(valor > 0) return 1;
      else return (alfaElu * Math.exp(valor));
   }


   public static double linear(double valor){
      return valor;
   }


   public static double linearDx(double valor){
      return 1;
   }


   public static void argmax(Camada camada){
      int indiceMaior = 0;
      double maiorValor = camada.neuronios[0].saida;

      //procurar maior valor da saída
      for(int i = 0; i < camada.neuronios.length; i++){
         if(camada.neuronios[i].saida > maiorValor){
            indiceMaior = i;
            maiorValor = camada.neuronios[i].saida;
         }
      }

      //aplicar argmax
      for(int i = 0; i < camada.neuronios.length; i++){
         if(i == indiceMaior) camada.neuronios[i].saida = 1;
         else camada.neuronios[i].saida = 0;
      }
   }


   public static void softmax(Camada camada){
      double somaExponencial = 0.0;

      //soma exponencial da saída
      for (int i = 0; i < camada.neuronios.length; i++){
         somaExponencial += Math.exp(camada.neuronios[i].saida);
      }

      //aplicar softmax
      for (int i = 0; i < camada.neuronios.length; i++){
         double valorExponencial = Math.exp(camada.neuronios[i].saida);
         camada.neuronios[i].saida = (valorExponencial / somaExponencial);
      }
   }
}
