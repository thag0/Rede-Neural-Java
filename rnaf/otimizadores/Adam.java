package rnaf.otimizadores;

import java.util.ArrayList;

import rnaf.Camada;
import rnaf.Neuronio;

/**
 * Implementação do algoritmo de otimização Adam.
 * O algoritmo ajusta os pesos da rede neural usando o gradiente descendente com momento
 * e a estimativa adaptativa de momentos de primeira e segunda ordem.
 */
public class Adam extends Otimizador{
   //parâmetros do adam
   private float epsilon = 1e-8f;//evitar divisão por zero
   private float beta1 = 0.9f;//decaimento do momento
   private float beta2 = 0.999f;//decaimento da segunda ordem


   @Override
   public void atualizar(ArrayList<Camada> redec, float taxaAprendizagem, float momentum){
      double t = 1; //contador de iterações
      float momentumCorrigido, segundaOrdemCorrigida;
      for(int i = 1; i < redec.size(); i++){//percorrer rede

         Camada camada = redec.get(i);
         int nNeuronios = camada.obterQuantidadeNeuronios();
         nNeuronios -= (camada.temBias()) ? 1 : 0;
         for(int j = 0; j < nNeuronios; j++){//percorrer neurônios da camada atual

            Neuronio neuronio = camada.neuronio(j);
            for(int k = 0; k < neuronio.pesos.length; k++){//percorrer pesos do neurônio atual
               //atualização do momentum
               neuronio.momentum[k] = (beta1 * neuronio.momentum[k]) + ((1 - beta1) * neuronio.gradiente[k]);
               // Atualização do acumulador da segunda ordem
               neuronio.acumuladorSegundaOrdem[k] = (beta2 * neuronio.acumuladorSegundaOrdem[k]) + ((1 - beta2) * neuronio.gradiente[k] * neuronio.gradiente[k]);
               //bias corrigido pelo momento
               momentumCorrigido = (float) (neuronio.momentum[k] / (1 - Math.pow(beta1, t)));
               //bias corrigido pela segunda ordem
               segundaOrdemCorrigida = (float) (neuronio.acumuladorSegundaOrdem[k] / (1 - Math.pow(beta2, t)));
               //atualização dos pesos usando o Adam
               neuronio.pesos[k] += taxaAprendizagem * momentumCorrigido / (Math.sqrt(segundaOrdemCorrigida) + epsilon);
            }
         }
      }
   }

}
