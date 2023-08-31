package rnaf.avaliacao.metrica;

import rnaf.RedeNeuralFloat;


/**
 * Classe genérica para cálculos de métricas de avaliação da rede neural.
 * <p>
 *    Novas métricas devem implementar o método {@code calcular()}.
 * </p>
 */
abstract class Metrica{

   /**
    * Calcula a métrica de avaliação configurada.
    * @param rede rede neural.
    * @param entrada dados de entrada.
    * @param saida dados de saída relativos a entrada.
    * @return valor de avaliação de acordo com a métrica configurada
    */
   public float calcular(RedeNeuralFloat rede, float[][] entrada, float[][] saida){
      throw new java.lang.UnsupportedOperationException("É necessário implementar a métrica de avaliação da rede.");
   }


   /**
    * Calcula a métrica de avaliação configurada.
    * @param rede rede neural.
    * @param entrada dados de entrada.
    * @param saida dados de saída relativos a entrada.
    * @return valor de avaliação de acordo com a métrica configurada
    */
   public int[][] calcularMatriz(RedeNeuralFloat rede, float[][] entrada, float[][] saida){
      throw new java.lang.UnsupportedOperationException("É necessário implementar a métrica de avaliação da rede.");
   }


   /**
    * <p>
    *    Auxiliar.
    * </p>
    * Encontra o índice com o maior valor contido no array fornecido
    * @param dados array contendo os dados
    * @return índice com o maior valor contido nos dados.
    */
   protected int indiceMaiorValor(float[] dados){
      int indiceMaiorValor = 0;
      double maiorValor = dados[0];
  
      for(int i = 1; i < dados.length; i++){
         if (dados[i] > maiorValor) {
            maiorValor = dados[i];
            indiceMaiorValor = i;
         }
      }
  
      return indiceMaiorValor;
   }
}
