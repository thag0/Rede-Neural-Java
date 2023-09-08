package rna.otimizadores;

import java.util.ArrayList;

import rna.Camada;
import rna.Neuronio;


/**
 * Implementação do algoritmo de otimização Nadam.
 * <p>
 *    O algoritmo ajusta os pesos da rede neural usando o gradiente descendente 
 *    com momento e a estimativa adaptativa de momentos de primeira e segunda ordem.
 * </p>
 * O adicional do Nadam é usar o acelerador de nesterov durante a correção dos
 * pesos da rede.
 */
public class Nadam extends Otimizador{

   /**
    * Usado para evitar divisão por zero.
    */
   private double epsilon;

   /**
    * decaimento do momentum.
    */
   private double beta1;

   /**
    * decaimento do momentum de segunda ordem.
    */
   private double beta2;

   /**
    * Contador de iterações.
    */
   double interacoes = 1;


   /**
    * Inicializa uma nova instância de otimizador Nadam usando os valores 
    * de hiperparâmetros fornecidos.
    * @param epsilon usado para evitar a divisão por zero.
    * @param beta1 decaimento do momento de primeira ordem.
    * @param beta2 decaimento da segunda ordem.
    */
   public Nadam(double epsilon, double beta1, double beta2){
      this.epsilon = epsilon;
      this.beta1 = beta1;
      this.beta2 = beta2;
   }


   /**
    * Inicializa uma nova instância de otimizador Nadam.
    * <p>
    *    Os hiperparâmetros do Nadam serão inicializados com os valores padrão, que são:
    * </p>
    * {@code epsilon = 1e-7}
    * <p>
    *    {@code beta1 = 0.9}
    * </p>
    * <p>
    *    {@code beta2 = 0.999}
    * </p>
    */
   public Nadam(){
      this(1e-7, 0.9, 0.999);
   }


   @Override
   public void atualizar(ArrayList<Camada> redec, double taxaAprendizagem, double momentum){
      double momentumCorrigido, segundaOrdemCorrigida;

      //pre alocações
      Camada camada;
      Neuronio neuronio;

      for(int i = 1; i < redec.size(); i++){//percorrer rede

         camada = redec.get(i);
         int nNeuronios = camada.quantidadeNeuronios() - (camada.temBias() ? 1 : 0);
         for(int j = 0; j < nNeuronios; j++){//percorrer neurônios da camada atual

            double interBeta1 = (1 - Math.pow(beta1, interacoes));
            double interBeta2 = (1 - Math.pow(beta2, interacoes));

            neuronio = camada.neuronio(j);
            for(int k = 0; k < neuronio.pesos.length; k++){//percorrer pesos do neurônio atual
               neuronio.momentum[k] = (beta1 * neuronio.momentum[k]) + ((1 - beta1) * neuronio.gradiente[k]);
               neuronio.acumuladorSegundaOrdem[k] = (beta2 * neuronio.acumuladorSegundaOrdem[k]) + ((1 - beta2) * neuronio.gradiente[k] * neuronio.gradiente[k]);
               
               momentumCorrigido = (beta1 * neuronio.momentum[k] + ((1 - beta1) * neuronio.gradiente[k]))  / interBeta1;
               segundaOrdemCorrigida = (beta2 * neuronio.acumuladorSegundaOrdem[k] + ((1 - beta2) * neuronio.gradiente[k] * neuronio.gradiente[k])) / interBeta2;
               neuronio.pesos[k] += (taxaAprendizagem * momentumCorrigido) / (Math.sqrt(segundaOrdemCorrigida) + epsilon);
               
               interacoes++;
            }
         }
      }
   }
}
