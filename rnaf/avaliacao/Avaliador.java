package rnaf.avaliacao;

import rnaf.RedeNeuralFloat;
import rnaf.avaliacao.metrica.Acuracia;
import rnaf.avaliacao.metrica.F1Score;
import rnaf.avaliacao.metrica.MatrizConfusao;
import rnaf.avaliacao.perda.EntropiaCruzada;
import rnaf.avaliacao.perda.EntropiaCruzadaBinaria;
import rnaf.avaliacao.perda.ErroMedioAbsoluto;
import rnaf.avaliacao.perda.ErroMedioQuadrado;


/**
 * Interface para os métodos de avaliação e desempenho da rede neural.
 */
public class Avaliador{
   RedeNeuralFloat rede;

   //perda
   EntropiaCruzada entropiaCruzada = new EntropiaCruzada();
   EntropiaCruzadaBinaria entropiaCruzadaBinaria = new EntropiaCruzadaBinaria();
   ErroMedioQuadrado erroMedioQuadrado = new ErroMedioQuadrado();

   //métrica
   Acuracia acuracia = new Acuracia();
   ErroMedioAbsoluto erroMedioAbsoluto = new ErroMedioAbsoluto();
   MatrizConfusao matrizConfusao = new MatrizConfusao();
   F1Score f1Score = new F1Score();

   public Avaliador(RedeNeuralFloat rede){
      this.rede = rede;
   }


   /**
    * Calcula o erro médio quadrado da rede neural em relação aos dados de entrada e saída fornecidos.
    * @param entrada dados de entrada.
    * @param saida dados de saída contendo os resultados respectivos para as entradas.
    * @return erro médio quadrado da rede em relação ao dados fornecidos (custo/perda).
    */
   public float erroMedioQuadrado(float[][] entrada, float[][] saida){
      return erroMedioQuadrado.calcular(this.rede, entrada, saida);
   }


   /**
    * Calcula o erro médio absoluto entre as saídas previstas pela rede neural e os valores reais.
    * @param entrada dados de entrada.
    * @param saida dados de saída contendo os resultados respectivos para as entradas.
    * @return A precisão da rede neural em forma de probabilidade.
    */
   public float erroMedioAbsoluto(float[][] entrada, float[][] saida){
      return erroMedioAbsoluto.calcular(this.rede, entrada, saida);
   }


   /**
    * Calcula a precisão da rede neural em relação aos dados de entrada e saída fornecidos (classificação).
    * @param entrada dados de entrada.
    * @param saida dados de saída contendo os resultados respectivos para as entradas.
    * @return A acurácia da rede neural em forma de probabilidade.
    */
   public float acuracia(float[][] entrada, float[][] saida){
      return acuracia.calcular(this.rede, entrada, saida);
   }


   /**
    * Calcula a entropia cruzada entre as saídas previstas pela rede neural
    * e as saídas reais fornecidas.
    * @param entrada dados de entrada.
    * @param saida dados de saída contendo os resultados respectivos para as entradas.
    * @return entropia cruzada da rede em relação ao dados fornecidos (custo/perda).
    */
   public float entropiaCruzada(float[][] entrada, float[][] saida){  
      return entropiaCruzada.calcular(this.rede, entrada, saida);
   }


   /**
    * Calcula a entropia cruzada binária entre as saídas previstas pela rede neural
    * e as saídas reais fornecidas.
    * @param entrada Os dados de entrada para os quais a rede neural calculará as saídas.
    * @param saida As saídas reais correspondentes aos dados de entrada.
    * @return valor da entropia cruzada binária.
    */
   public float entropiaCruzadaBinaria(float[][] entrada, float[][] saida){
      return entropiaCruzadaBinaria.calcular(this.rede, entrada, saida);
   }

   /**
    * Calcula a matriz de confusão para avaliar o desempenho da rede em classificação.
    * <p>
    *    A matriz de confusão mostra a contagem de amostras que foram classificadas de forma correta 
    *    ou não em cada classe. As linhas representam as classes reais e as colunas as classes previstas pela rede.
    * </p>
    * @param entradas matriz com os dados de entrada 
    * @param saidas matriz com os dados de saída
    * @return matriz de confusão para avaliar o desempenho do modelo.
    * @throws IllegalArgumentException se o modelo não foi compilado previamente.
    */
   public int[][] matrizConfusao(float[][] entradas, float[][] saidas){
      return matrizConfusao.calcularMatriz(this.rede, entradas, saidas);
   }


   /**
    * Calcula o F1-Score ponderado para o modelo de rede neural em relação às entradas e saídas fornecidas.
    *
    * O F1-Score é uma métrica que combina a precisão e o recall para avaliar o desempenho de um modelo
    * de classificação. Ele é especialmente útil quando se lida com classes desbalanceadas ou quando se
    * deseja equilibrar a precisão e o recall.
    *
    * @param entradas matriz com os dados de entrada 
    * @param saidas matriz com os dados de saída
    * @return f1-score ponderado para o modelo em relação aos dados de entrada e saída.
    */
   public float f1Score(float[][] entradas, float[][] saidas){
      return f1Score.calcular(this.rede, entradas, saidas);
   }
}
