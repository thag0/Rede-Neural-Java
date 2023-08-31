package rnaf.avaliacao.metrica;

import rnaf.RedeNeuralFloat;

public class Acuracia extends Metrica{

   @Override
   public float calcular(RedeNeuralFloat rede, float[][] entrada, float[][] saida){
      int qAmostras = entrada.length;
      int acertos = 0;
      float acuracia;
      float[] dadosEntrada = new float[entrada[0].length];
      float[] dadosSaida = new float[saida[0].length];

      for(int i = 0; i < qAmostras; i++){
         //preencher dados de entrada e saída
         dadosEntrada = entrada[i];
         dadosSaida = saida[i];

         rede.calcularSaida(dadosEntrada);

         int indiceCalculado = super.indiceMaiorValor(rede.obterSaidas());
         int indiceEsperado = super.indiceMaiorValor(dadosSaida);

         if(indiceCalculado == indiceEsperado){
            acertos++;
         }
      }

      acuracia = (float)acertos / qAmostras;

      return acuracia;
   }
}
