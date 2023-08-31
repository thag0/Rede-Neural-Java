package rnaf.avaliacao.perda;

import rnaf.RedeNeuralFloat;

public class ErroMedioAbsoluto extends FuncaoPerda{

   @Override
   public float calcular(RedeNeuralFloat rede, float[][] entrada, float[][] saida){
      float[] dadosEntrada = new float[entrada[0].length];
      float[] dadosSaida = new float[saida[0].length];
      float erroMedio = 0;

      for(int i = 0; i < entrada.length; i++){//percorrer linhas dos dados
         System.arraycopy(entrada[i], 0, dadosEntrada, 0, entrada[i].length);
         System.arraycopy(saida[i], 0, dadosSaida, 0, saida[i].length);

         rede.calcularSaida(dadosEntrada);
         float[] saidaRede = rede.obterSaidas();

         for(int k = 0; k < saidaRede.length; k++){
            erroMedio += Math.abs(dadosSaida[k] - saidaRede[k]);
         }
      }

      erroMedio /= entrada.length;
      
      return erroMedio;
   }
}
