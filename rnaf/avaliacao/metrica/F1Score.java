package rnaf.avaliacao.metrica;

import rnaf.RedeNeuralFloat;

public class F1Score extends Metrica{
   
   @Override
   public float calcular(RedeNeuralFloat rede, float[][] entrada, float[][] saida){
      int[][] matrizConfusao = matrizConfusao(rede, entrada, saida);
      float f1score = f1score(matrizConfusao);
      return f1score;
   }


   private float f1score(int[][] matrizConfusao){
      int nClasses = matrizConfusao.length;

      float[] precisao = new float[nClasses];
      float[] recall = new float[nClasses];

      for(int i = 0; i < nClasses; i++){
         int vp = matrizConfusao[i][i];//verdadeiro positivo
         int fp = 0;//falso positivo
         int fn = 0;//falso negativo


         for(int j = 0; j < nClasses; j++){
            if(j != i){
               fp += matrizConfusao[j][i];
               fn += matrizConfusao[i][j];
            }
         }

         //formulas da precisão e recall
         if((vp + fp) > 0) precisao[i] = vp / (float)(vp + fp);
         else precisao[i] = 0;

         if((vp + fn) > 0) recall[i] = vp / (float)(vp + fn);
         else recall[i] = 0;
      }

      float somaF1 = 0.0f;
      for(int i = 0; i < nClasses; i++){
         double f1Classe = 2 * (precisao[i] * recall[i]) / (precisao[i] + recall[i]);
         somaF1 += f1Classe;
      }

      return somaF1;
   }


   
   private int[][] matrizConfusao(RedeNeuralFloat rede, float[][] entradas, float[][] saidas){
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
