package rnaf.treinamento;

import java.util.ArrayList;
import java.util.Random;

import rnaf.Camada;
import rnaf.Neuronio;

/**
 * Operadores auxiliares para o treino da rede neural;
 */
class AuxiliaresTreino{
   Random random = new Random();

   
   public AuxiliaresTreino(){

   }


   /**
    * Método exclusivo para separar a forma de calcular os erros da camada de saída.
    * Dando suporte não apenas para problemas de regressão.
    * <p>
    *    Isso ainda ta em teste para problemas de classificação, para regressão funciona normalmente.
    * </p>
    * @param redec Rede Neural em formato de lista de camadas.
    * @param saidas array com as saídas esperadas.
    */
   public void calcularErroSaida(ArrayList<Camada> redec, float[] saidas){
      Camada saida = redec.get(redec.size()-1);
      
      if(saida.temArgmax()){//classificação
         int indiceMaior = indiceMaiorValor(saidas);
         for(int i = 0; i < saida.obterQuantidadeNeuronios(); i++){
            Neuronio neuronio = saida.neuronio(i);
            if(i == indiceMaior) neuronio.erro = 1 - neuronio.saida;
            else neuronio.erro = 0 - neuronio.saida;
         }

      }else if(saida.temSoftmax()){//classificação
         for(int i = 0; i < saida.obterQuantidadeNeuronios(); i++){
            Neuronio neuronio = saida.neuronio(i);
            neuronio.erro = (saidas[i] - neuronio.saida);
         }
      
      }else{//regressão
         for(int i = 0; i < saida.obterQuantidadeNeuronios(); i++){
            Neuronio neuronio = saida.neuronio(i);
            neuronio.erro = ((saidas[i] - neuronio.saida) * saida.funcaoAtivacaoDx(neuronio.somatorio));
         }
      }
   }


   /**
    * Método exclusivo para separar a forma de calcular os erros das camadas ocultas
    * da rede neural.
    * @param redec Rede Neural em formato de lista de camadas.
    */
   public void calcularErroOcultas(ArrayList<Camada> redec){
      //começar da ultima oculta
      for(int i = redec.size()-2; i >= 1; i--){// percorrer camadas ocultas de trás pra frente
         
         Camada camadaAtual = redec.get(i);
         int qNeuronioAtual = camadaAtual.obterQuantidadeNeuronios();
         if(camadaAtual.temBias()) qNeuronioAtual -= 1;
         for (int j = 0; j < qNeuronioAtual; j++){//percorrer neurônios da camada atual
         
            Neuronio neuronio = camadaAtual.neuronio(j);
            float somaErros = 0.0f;
            for(Neuronio neuronioProximo : redec.get(i+1).neuronios()){//percorrer neurônios da camada seguinte
               somaErros += neuronioProximo.pesos[j] * neuronioProximo.erro;
            }
            neuronio.erro = somaErros * camadaAtual.funcaoAtivacaoDx(neuronio.somatorio);
         }
      }
   }


   /**
    * Embaralha os dados da matriz usando o algoritmo Fisher-Yates.
    * @param entradas matriz com os dados de entrada.
    * @param saidas matriz com os dados de saída.
    */
   public void embaralharDados(float[][] entradas, float[][] saidas){
      int linhas = entradas.length;
      int colEntrada = entradas[0].length;
      int colSaida = saidas[0].length;
  
      //evitar muitas inicializações
      float tempEntradas[] = new float[colEntrada];
      float tempSaidas[] = new float[colSaida];
      int i, idAleatorio;

      for(i = linhas - 1; i > 0; i--){
         idAleatorio = random.nextInt(i + 1);

         //trocar entradas
         System.arraycopy(entradas[i], 0, tempEntradas, 0, colEntrada);
         System.arraycopy(entradas[idAleatorio], 0, entradas[i], 0, colEntrada);
         System.arraycopy(tempEntradas, 0, entradas[idAleatorio], 0, colEntrada);

         //trocar saídas
         System.arraycopy(saidas[i], 0, tempSaidas, 0, colSaida);
         System.arraycopy(saidas[idAleatorio], 0, saidas[i], 0, colSaida);
         System.arraycopy(tempSaidas, 0, saidas[idAleatorio], 0, colSaida); 
      }
   }


   /**
    * Dedicado para treino em lote e multithread em implementações futuras.
    * @param dados conjunto de dados completo.
    * @param inicio índice de inicio do lote.
    * @param fim índice final do lote.
    * @return lote contendo os dados de acordo com os índices fornecidos.
    */
   public float[][] obterSubMatriz(float[][] dados, int inicio, int fim){
      if(inicio < 0 || fim > dados.length || inicio >= fim){
         throw new IllegalArgumentException("Índices de início ou fim inválidos.");
      }

      int linhas = fim - inicio;
      int colunas = dados[0].length;
      float[][] subMatriz = new float[linhas][colunas];

      for(int i = 0; i < linhas; i++){
         System.arraycopy(dados[inicio + i], 0, subMatriz[i], 0, colunas);
      }

      return subMatriz;
   }


   /**
    * Encontra o índice com o maior valor contido no array fornecido
    * @param dados array contendo os dados
    * @return índice com o maior valor contido nos dados.
    */
   public int indiceMaiorValor(float[] dados){
      int indiceMaiorValor = 0;
      float maiorValor = dados[0];
  
      for(int i = 1; i < dados.length; i++){
         if(dados[i] > maiorValor){
            maiorValor = dados[i];
            indiceMaiorValor = i;
         }
      }
  
      return indiceMaiorValor;
   }


   /**
    * Zera todos os gradientes para o cálculo do gradiente em lote.
    * @param redec rede neural no formade de lista de camadas.
    */
   public void zerarGradientesAcumulados(ArrayList<Camada> redec){
      for(int i = 1; i < redec.size(); i++){ 
         
         Camada camadaAtual = redec.get(i);
         int nNeuronios = camadaAtual.obterQuantidadeNeuronios();
         nNeuronios -= (camadaAtual.temBias()) ? 1 : 0;
         for(int j = 0; j < nNeuronios; j++){//percorrer neurônios da camada atual
            
            Neuronio neuronio = camadaAtual.neuronio(j);
            for(int k = 0; k < neuronio.pesos.length; k++){//percorrer pesos do neurônio atual
               neuronio.gradienteAcumulado[k] = 0;
            }
         }
      }
   }
}
