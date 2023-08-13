package rna.avaliacao;

import rna.RedeNeural;

public class EntropiaCruzada extends FuncaoPerda{

   @Override
   public double calcular(RedeNeural rede, double[][] entrada, double[][] saida){  
      double[] dadosEntrada = new double[entrada[0].length];
      double[] dadosSaida = new double[saida[0].length];
      double[] saidaRede = new double[rede.obterCamadaSaida().obterQuantidadeNeuronios()];
  
      double perda = 0.0;
      double epsilon = 1e-9;//evitar log 0
      for(int i = 0; i < entrada.length; i++){//percorrer amostras
         //preencher dados de entrada e saída
         //método nativo mais eficiente
         System.arraycopy(entrada[i], 0, dadosEntrada, 0, entrada[i].length);
         System.arraycopy(saida[i], 0, dadosSaida, 0, saida[i].length);
  
         rede.calcularSaida(dadosEntrada);
         saidaRede = rede.obterSaidas();
         
         double perdaExemplo = 0.0;
         for(int k = 0; k < saidaRede.length; k++){
            double previsto = saidaRede[k];
            double real = dadosSaida[k];
            
            //fórmula da entropia cruzada para cada neurônio de saída
            perdaExemplo += (-real * Math.log(previsto + epsilon));
         }
         
         perda += perdaExemplo;
      }
  
      perda /= entrada.length;

      return perda;
   }
}
