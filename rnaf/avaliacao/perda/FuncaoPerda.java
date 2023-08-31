package rnaf.avaliacao.perda;

import rnaf.RedeNeuralFloat;


/**
 * Classe genérica para criação de funções de perda.
 * <p>
 *    Novas funções de perda devem implementar o método {@code calcular()}.
 * </p>
 */
public abstract class FuncaoPerda{

   /**
    * Calcula a função de perda configurada.
    * @param rede rede neural.
    * @param entrada dados de entrada.
    * @param saida dados de saída relativos a entrada.
    * @return valor de perda de acordo com a função configurada
    */
   public float calcular(RedeNeuralFloat rede, float[][] entrada, float[][] saida){
      throw new java.lang.UnsupportedOperationException("É necessário implementar a função de perda.");
   }
}
