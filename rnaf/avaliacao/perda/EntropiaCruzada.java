package rnaf.avaliacao.perda;

import rnaf.RedeNeuralFloat;

public class EntropiaCruzada extends FuncaoPerda{

   @Override
   public float calcular(RedeNeuralFloat rede, float[][] entrada, float[][] saida){  
      float[] dadosEntrada = new float[entrada[0].length];
      float[] dadosSaida = new float[saida[0].length];
      float[] saidaRede = new float[rede.obterCamadaSaida().obterQuantidadeNeuronios()];
  
      float perda = 0.0f;
      float epsilon = 1e-10f;//evitar log 0
      for(int i = 0; i < entrada.length; i++){//percorrer amostras
         //preencher dados de entrada e saída
         //método nativo mais eficiente
         System.arraycopy(entrada[i], 0, dadosEntrada, 0, entrada[i].length);
         System.arraycopy(saida[i], 0, dadosSaida, 0, saida[i].length);
  
         rede.calcularSaida(dadosEntrada);
         saidaRede = rede.obterSaidas();
         
         double perdaExemplo = 0.0;
         for(int j = 0; j < saidaRede.length; j++){
            double previsto = saidaRede[j];
            double real = dadosSaida[j];
            
            //fórmula da entropia cruzada para cada neurônio de saída
            perdaExemplo += (-real * Math.log(previsto + epsilon));
         }
         
         perda += perdaExemplo;
      }
  
      perda /= entrada.length;

      return perda;
   }
}
