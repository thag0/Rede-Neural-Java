package rnaf.treinamento;

import java.util.ArrayList;

import rnaf.Camada;
import rnaf.Neuronio;
import rnaf.RedeNeuralFloat;
import rnaf.otimizadores.Otimizador;


/**
 * Classe dedicada para lidar com o treinamento em lotes da rede neural.
 */
public class TreinoLote{
   AuxiliaresTreino auxiliarTreino = new AuxiliaresTreino();

   public boolean calcularHistoricoCusto = false;
   ArrayList<Float> historicoCusto;//salvar mesmo historico de custo


   /**
    * Implementação do treino em lote.
    * @param historicoCusto
    */
   public TreinoLote(ArrayList<Float> historicoCusto){
      this.historicoCusto = historicoCusto;
   }


   public void treino(RedeNeuralFloat rede, Otimizador otimizador, float[][] entradas, float[][] saidas, int epochs, boolean embaralhar, int tamanhoLote){
      ArrayList<Camada> redec = redeParaCamadas(rede);

      for(int i = 0; i < epochs; i++){
         if(embaralhar) auxiliarTreino.embaralharDados(entradas, saidas);

         for(int j = 0; j < entradas.length; j += tamanhoLote){
            int fimIndice = Math.min(j + tamanhoLote, entradas.length);
            float[][] entradaLote = auxiliarTreino.obterSubMatriz(entradas, j, fimIndice);
            float[][] saidaLote = auxiliarTreino.obterSubMatriz(saidas, j, fimIndice);

            auxiliarTreino.zerarGradientesAcumulados(redec);//reiniciar gradiente do lote
            for(int k = 0; k < entradaLote.length; k++){
               float[] entrada = entradaLote[k];
               float[] saida = saidaLote[k];

               rede.calcularSaida(entrada);
               backpropagationLote(redec, rede.obterTaxaAprendizagem(), saida);
            }
            //normalizar gradientes para enviar pro otimizador
            calcularMediaGradientesLote(redec, entradaLote.length);
            otimizador.atualizar(redec, rede.obterTaxaAprendizagem(), rede.obterTaxaMomentum());
         }

         //feedback de avanço da rede
         if(calcularHistoricoCusto){
            if(rede.obterCamadaSaida().temSoftmax()) historicoCusto.add(rede.avaliador.entropiaCruzada(entradas, saidas));
            else historicoCusto.add(rede.avaliador.erroMedioQuadrado(entradas, saidas));
         }
      }
   }


   /**
    * Serializa a rede no formato de lista de camadas pra facilitar (a minha vida)
    * o manuseio e generalização das operações.
    * @param rede Rede Neural
    * @return lista de camadas da rede neural.
    */
   private ArrayList<Camada> redeParaCamadas(RedeNeuralFloat rede){
      ArrayList<Camada> redec = new ArrayList<>();

      redec.add(rede.obterCamadaEntrada());
      for(int i = 0; i < rede.obterQuantidadeOcultas(); i++){
         redec.add(rede.obterCamadaOculta(i));
      }
      redec.add(rede.obterCamadaSaida());

      return redec;
   }


   /**
    * Retropropaga o erro da rede neural de acordo com os dados de entrada e saída esperados e calcula
    * os gradientes acumulados de cada lote.
    * @param redec Rede Neural em formato de lista de camadas.
    * @param taxaAprendizagem valor de taxa de aprendizagem da rede neural.
    * @param saidas array com as saídas esperadas das amostras.
    */
   private void backpropagationLote(ArrayList<Camada> redec, float taxaAprendizagem, float[] saidas){
      auxiliarTreino.calcularErroSaida(redec, saidas);
      auxiliarTreino.calcularErroOcultas(redec);
      calcularGradientesAcumulados(redec, taxaAprendizagem);
   }


   /**
    * Método exclusivo para separar o cálculo dos gradientes em lote das conexões de cada
    * neurônio dentro da rede.
    * @param redec Rede Neural em formato de lista de camadas.
    * @param taxaAprendizagem valor de taxa de aprendizagem da rede neural.
    */
   private void calcularGradientesAcumulados(ArrayList<Camada> redec, float taxaAprendizagem){
      //percorrer rede, excluindo camada de entrada
      for(int i = 1; i < redec.size(); i++){ 
         
         Camada camadaAtual = redec.get(i);
         Camada camadaAnterior = redec.get(i-1);

         //não precisa e nem faz diferença calcular os gradientes dos bias
         int nNeuronios = camadaAtual.obterQuantidadeNeuronios();
         nNeuronios -= (camadaAtual.temBias()) ? 1 : 0;
         for(int j = 0; j < nNeuronios; j++){//percorrer neurônios da camada atual
            
            Neuronio neuronio = camadaAtual.neuronio(j);
            for(int k = 0; k < neuronio.pesos.length; k++){//percorrer pesos do neurônio atual
               neuronio.gradienteAcumulado[k] += taxaAprendizagem * neuronio.erro * camadaAnterior.neuronio(k).saida;
            }
         }
      }
   }


   /**
    * Método exlusivo para separar a forma de calcular a média dos gradientes do lote.
    * @param redec Rede Neural em formato de lista de camadas.
    * @param tamanhoLote tamanho do lote.
    */
   private void calcularMediaGradientesLote(ArrayList<Camada> redec, int tamanhoLote){
      for(int i = 1; i < redec.size(); i++){ 
         
         Camada camadaAtual = redec.get(i);
         int nNeuronios = camadaAtual.obterQuantidadeNeuronios();
         nNeuronios -= (camadaAtual.temBias()) ? 1 : 0;
         for(int j = 0; j < nNeuronios; j++){//percorrer neurônios da camada atual
            
            Neuronio neuronio = camadaAtual.neuronio(j);
            for(int k = 0; k < neuronio.pesos.length; k++){//percorrer pesos do neurônio atual
               neuronio.gradiente[k] = (neuronio.gradienteAcumulado[k] / tamanhoLote);
            }
         }
      }
   }
}
