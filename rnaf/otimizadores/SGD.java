package rnaf.otimizadores;

import java.util.ArrayList;

import rnaf.Camada;
import rnaf.Neuronio;

/**
 * Classe que implementa o otimizador Gradiente Descentente Estocástico.
 */
public class SGD extends Otimizador{
   boolean nesterov = false;


   /**
    * Otimizador usando gradiente descendente estocástico com momentum.
    * Atualiza os pesos usando o gradiente e momentum para ajudar a otimizar o aprendizado.
    */
   public SGD(boolean nesterov){
      this.nesterov = nesterov;
   }
      
      
   /**
    * Otimizador usando gradiente descendente estocástico com momentum.
    * Atualiza os pesos usando o gradiente e momentum para ajudar a otimizar o aprendizado.
    */
   public SGD(){

   }


   @Override
   public void atualizar(ArrayList<Camada> redec, float taxaAprendizagem, float momentum){
      for(int i = 1; i < redec.size(); i++){//percorrer rede 
         
         Camada camada = redec.get(i);
         int nNeuronios = camada.obterQuantidadeNeuronios();
         nNeuronios -= (camada.temBias()) ? 1 : 0;
         for(int j = 0; j < nNeuronios; j++){//percorrer neurônios da camada atual
            
            Neuronio neuronio = camada.neuronio(j);
            for(int k = 0; k < neuronio.pesos.length; k++){//percorrer pesos do neurônio atual
               if(nesterov){
                  float momentumAnterior = neuronio.momentum[k];
                  neuronio.pesos[k] += momentum * momentumAnterior;
                  neuronio.momentum[k] = momentum * momentumAnterior + neuronio.gradiente[k];
                  neuronio.pesos[k] -= taxaAprendizagem * neuronio.momentum[k];
                  
               }else{
                  neuronio.momentum[k] = (momentum * neuronio.momentum[k]) + neuronio.gradiente[k];
                  neuronio.pesos[k] += neuronio.momentum[k];
               }
            }
         }
      } 
   }
   
}
