package rnaf.otimizadores;

import java.util.ArrayList;

import rnaf.Camada;


/**
 * Classe genérica para implementações de otimizadores do treino da rede neural.
 * <p>
 *      O otimizador já deve levar em consideração que os gradientes foram calculados previamente.
 * </p>
 * <p>
 *      Novos otimizadores devem implementar o método {@code atualizar()}, que atualizará os pesos
 *      da rede neural de acordo com seu próprio algoritmo.
 * </p>
 */
public abstract class Otimizador{

   /**
    * Treina a rede neural de acordo com o otimizador configurado.
    * @param redec Rede Neural em formato de lista de camadas.
    * @param taxaAprendizagem valor de taxa de aprendizagem da Rede Neural.
    * @param momentum valor de taxa de momentum da Rede Neural.
    */
    public void atualizar(ArrayList<Camada> redec, float taxaAprendizagem, float momentum){
        throw new java.lang.UnsupportedOperationException("Método de atualização do otimizador não foi implementado.");
    }
}
