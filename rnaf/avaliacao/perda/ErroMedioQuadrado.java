package rnaf.avaliacao.perda;

import rnaf.RedeNeuralFloat;

public class ErroMedioQuadrado extends FuncaoPerda{

   @Override
   public float calcular(RedeNeuralFloat rede, float[][] entrada, float[][] saida){  
      float[] dadosEntrada = new float[entrada[0].length];//tamanho das colunas da entrada
      float[] dadosSaida = new float[saida[0].length];//tamanho de colunas da saída
      float[] saidaRede = new float[rede.obterCamadaSaida().obterQuantidadeNeuronios()];
      
      float diferenca;
      float perda = 0.0f;
      for(int i = 0; i < entrada.length; i++){//percorrer as linhas da entrada
         //preencher dados de entrada e saída
         //método nativo parece ser mais eficiente
         System.arraycopy(entrada[i], 0, dadosEntrada, 0, entrada[i].length);
         System.arraycopy(saida[i], 0, dadosSaida, 0, saida[i].length);

         rede.calcularSaida(dadosEntrada);
         saidaRede = rede.obterSaidas();

         //esperado - predito
         for(int j = 0; j < saidaRede.length; j++){
            diferenca = dadosSaida[j] - saidaRede[j];
            perda += (diferenca * diferenca);
         }
      }

      perda /= entrada.length;

      return perda;      
   }
}
