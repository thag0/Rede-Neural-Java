package rna;

import java.util.Random;

public class Neuronio{
   public double[] pesos;
   public double saida;

   public int qtdLigacoes;
   private Random random = new Random();

   public Neuronio(int qtdLigacoes){
      this.qtdLigacoes = qtdLigacoes;

      pesos = new double[qtdLigacoes];
      for(int i = 0; i < qtdLigacoes; i++){
         pesos[i] = ((random.nextDouble()*2) -1)*10;//-10 a 10
      }

      this.saida = 0;
   }
}
