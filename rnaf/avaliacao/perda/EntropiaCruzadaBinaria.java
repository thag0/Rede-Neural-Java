package rnaf.avaliacao.perda;

import rnaf.RedeNeuralFloat;

public class EntropiaCruzadaBinaria extends FuncaoPerda{

   @Override
   public float calcular(RedeNeuralFloat rede, float[][] entrada, float[][] saida){
      float[] dadosEntrada = new float[entrada[0].length];
      float[] dadosSaida = new float[saida[0].length];
      float[] saidaRede = new float[rede.obterCamadaSaida().obterQuantidadeNeuronios()];

      int nAmostras = entrada.length;
      float perda = 0.0f;

      for(int i = 0; i < nAmostras; i++){
         //preencher dados de entrada e saída
         //método nativo mais eficiente
         System.arraycopy(entrada[i], 0, dadosEntrada, 0, entrada[i].length);
         System.arraycopy(saida[i], 0, dadosSaida, 0, saida[i].length);
         
         rede.calcularSaida(dadosEntrada);
         saidaRede = rede.obterSaidas();

         float perdaExemplo = 0.0f;
         for(int j = 0; j < dadosSaida.length; j++){
            float previsto = saidaRede[j];
            float real = dadosSaida[j];
            
            //formula desse diabo
            perdaExemplo += -((real * Math.log(previsto)) + ((1 - real) * Math.log(1 - previsto)));
         }

         perda += perdaExemplo;
      }

      perda /= nAmostras;

      return perda;
   }
}
