package rnaf.avaliacao.metrica;

import rnaf.RedeNeuralFloat;

public class MatrizConfusao extends Metrica{

   @Override
   public int[][] calcularMatriz(RedeNeuralFloat rede, float[][] entradas, float[][] saidas){
      int nClasses = saidas[0].length;
      int[][] matriz = new int[nClasses][nClasses];

      float[] entrada = new float[entradas[0].length];
      float[] saida = new float[saidas[0].length];
      float[] saidaRede = new float[rede.obterCamadaSaida().obterQuantidadeNeuronios()];

      for(int i = 0; i < entradas.length; i++){
         System.arraycopy(entradas[i], 0, entrada, 0, entradas[i].length);
         System.arraycopy(saidas[i], 0, saida, 0, saidas[i].length);

         rede.calcularSaida(entrada);
         saidaRede = rede.obterSaidas();

         int real = super.indiceMaiorValor(saida);
         int previsto = super.indiceMaiorValor(saidaRede);

         matriz[real][previsto]++;
      }

      return matriz;
   } 
}
