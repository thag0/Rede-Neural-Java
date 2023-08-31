package rnaf.otimizadores;

import java.util.ArrayList;

import rnaf.Camada;
import rnaf.Neuronio;

/**
 * Implementa o treino da rede neural usando o algoritmo RMSProp (Root Mean Square Propagation).
 *
 * Ele é uma adaptação do Gradiente Descendente Estocástico (SGD) que ajuda a lidar com a
 * oscilação do gradiente, permitindo que a taxa de aprendizado seja adaptada para cada parâmetro 
 * individualmente.
 */
public class RMSProp extends Otimizador{
   float epsilon = 1e-9f;//evitar divisão por zero
   float taxaDecaimento = 0.9f;//fator de decaimento do RMSprop


   @Override
   public void atualizar(ArrayList<Camada> redec, float taxaAprendizagem, float momentum){
      for(int i = 1; i < redec.size(); i++){//percorrer rede

         Camada camada = redec.get(i);
         int nNeuronios = camada.obterQuantidadeNeuronios();
         nNeuronios -= (camada.temBias()) ? 1 : 0;
         for(int j = 0; j < nNeuronios; j++){//percorrer neurônios da camada atual

            Neuronio neuronio = camada.neuronio(j);
            for(int k = 0; k < neuronio.pesos.length; k++){//percorrer pesos do neurônio atual
               neuronio.acumuladorGradiente[k] = (taxaDecaimento * neuronio.acumuladorGradiente[k]) + ((1 - taxaDecaimento) * neuronio.gradiente[k] * neuronio.gradiente[k]);
               neuronio.pesos[k] += (taxaAprendizagem / Math.sqrt(neuronio.acumuladorGradiente[k] + epsilon)) * neuronio.gradiente[k];
            }
         }
      }
   }
}
