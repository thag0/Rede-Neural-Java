package rna.otimizadores;

import rna.estrutura.Camada;
import rna.estrutura.Neuronio;

/**
 * Classe que implementa o algoritmo de Descida do Gradiente para otimização de redes neurais.
 * Atualiza diretamente os pesos da rede com base no gradiente.
 */
public class GD extends Otimizador{

   /**
    * Valor de taxa de aprendizagem do otimizador.
    */
   private double taxaAprendizagem;

   /**
    * Inicializa uma nova instância de otimizador da <strong> Descida do Gradiente </strong>
    * usando os valores de hiperparâmetros fornecidos.
    * @param tA valor de taxa de aprendizagem.
    */
   public GD(double tA){
      this.taxaAprendizagem = tA;
   }

   /**
    * Inicializa uma nova instância de otimizador da <strong> Descida do Gradiente </strong>.
    * <p>
    *    Os hiperparâmetros do GD serão inicializados com os valores padrão, que são:
    * </p>
    * {@code taxaAprendizagem = 0.01}
    */
   public GD(){
      this(0.01);
   }

   @Override
   public void inicializar(int parametros){
      
   }

   /**
    * Aplica o algoritmo do Gradiente descendente para cada peso da rede neural.
    * <p>
    *    O Gradiente descendente funciona usando a seguinte expressão:
    * </p>
    * <pre>
    *    p[i] -= g[i] * tA
    * </pre>
    * Onde:
    * <p>
    *    {@code p} - peso que será atualizado.
    * </p>
    *    {@code g} - gradiente correspondente a conexão do peso que será
    *    atualizado.
    * </p>
    * <p>
    *    {@code tA} - taxa de aprendizagem.
    * </p>
    */
    @Override
   public void atualizar(Camada[] redec){
      for(Camada camada : redec){
         for(Neuronio neuronio : camada.neuronios()){   
            for(int i = 0; i < neuronio.pesos.length; i++){
               neuronio.pesos[i] -= neuronio.gradientes[i] * taxaAprendizagem;
            }
         }
      } 
   }

   @Override
   public String info(){
      String buffer = "";

      String espacamento = "    ";
      buffer += espacamento + "TaxaAprendizagem: " + this.taxaAprendizagem + "\n";

      return buffer;
   }
   
}
