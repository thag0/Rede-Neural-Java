package rna;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class RedeNeural implements Cloneable, Serializable{
   public Camada entrada;
   public Camada[] ocultas;
   public Camada saida;
   
   public int qtdNeuroniosEntrada;
   public int qtdNeuroniosOcultas;
   public int qtdNeuroniosSaida;
   public int qtdCamadasOcultas;

   private double alcancePeso = 100;
   int BIAS = 1;
   double TAXA_APRENDIZAGEM = 0.1;

   //padronizar uso das funções de ativação
   private final int ativacaoRelu = 1;
   private final int ativacaoReluDx = 2;
   private final int ativacaoSigmoid = 3;
   private final int ativacaoSigmoidDx = 4;
   private final int ativacaoTanH = 5;
   private final int ativacaoTanHDx = 6;
   private final int ativacaoLeakyRelu = 7;
   
   private int funcaoAtivacao = ativacaoRelu;
   private int funcaoAtivacaoSaida = ativacaoReluDx;

   int i, j, k;//contadores

   /**
    * <p>Cria uma instância de rede neural artificial. A arquitetura da rede se 
    * baseia em uma camada de entrada, várias camadas ocultas mas com o mesmo número 
    * de neurônios cada, e uma camada de saída.</p>
    * os valores de todos os parâmetros pedidos <strong>NÃO</strong> devem
    * ser menores que 1.
    * @author Thiago Barroso
    * @param qtdNeuroniosEntrada quantidade de neurônios na camada de entrada
    * @param qtdNeuroniosOcultas quantidade de neurônios das camadas ocultas
    * @param qtdNeuroniosSaida quantidade de neurônios na camada de saída
    * @param qtdCamadasOcultas quantidade de camadas ocultas
    */
   public RedeNeural(int qtdNeuroniosEntrada, int qtdNeuroniosOcultas, int qtdNeuroniosSaida, int qtdCamadasOcultas){
      if(qtdNeuroniosEntrada < 1 || qtdNeuroniosOcultas < 1 || qtdNeuroniosSaida < 1 || qtdCamadasOcultas < 1){
         throw new IllegalArgumentException("Os valores devem ser maiores ou iguais a um.");
      }

      this.qtdNeuroniosEntrada = qtdNeuroniosEntrada;
      this.qtdNeuroniosOcultas = qtdNeuroniosOcultas;
      this.qtdNeuroniosSaida = qtdNeuroniosSaida;
      this.qtdCamadasOcultas = qtdCamadasOcultas;

      criarRede();
   }


   //instancia os neuronios e as camadas
   private void criarRede(){
      //inicializar camada de entrada
      entrada = new Camada();
      entrada.neuronios = new Neuronio[qtdNeuroniosEntrada];
      for(int i = 0; i < qtdNeuroniosEntrada; i++){
         entrada.neuronios[i] = new Neuronio(qtdNeuroniosOcultas, alcancePeso);
      }

      //inicializar camadas ocultas
      ocultas = new Camada[qtdCamadasOcultas];
      for (int i = 0; i < qtdCamadasOcultas; i++) {
         Camada novaOculta = new Camada();
         novaOculta.neuronios = new Neuronio[qtdNeuroniosOcultas];
      
         for (int j = 0; j < qtdNeuroniosOcultas; j++) {
            if (i == (qtdCamadasOcultas-1)){
               novaOculta.neuronios[j] = new Neuronio(qtdNeuroniosSaida, alcancePeso);
            
            }else{
               novaOculta.neuronios[j] = new Neuronio(qtdNeuroniosOcultas, alcancePeso);
            }
         }
         ocultas[i] = novaOculta;
      }

      //inicializar camada de saída
      saida = new Camada();
      saida.neuronios = new Neuronio[qtdNeuroniosSaida];
      for(int i = 0; i < qtdNeuroniosSaida; i++){
         saida.neuronios[i] = new Neuronio(qtdNeuroniosSaida, alcancePeso);
      }
   }


   /**
    * Propaga os dados de entrada pela rede neural pelo método de feedforward.
    * @param dados dados usados para a camada de entrada
    */
   public void calcularSaida(double[] dados){
      if(dados.length != this.entrada.neuronios.length){
         throw new IllegalArgumentException("As dimensões dos dados de entrada com os neurônios de entrada da rede não são iguais");
      }

      //entrada
      for(i = 0; i < this.qtdNeuroniosEntrada; i++){
         this.entrada.neuronios[i].saida = dados[i];
      }

      //ocultas
      double soma = 0.0;
      for(i = 0; i < this.qtdCamadasOcultas; i++){//percorrer camadas ocultas

         Camada camadaAtual = this.ocultas[i];
         Camada camadaAnterior;
         if(i == 0) camadaAnterior = this.entrada;
         else camadaAnterior = this.ocultas[i-1];

         for(j = 0; j < camadaAtual.neuronios.length; j++){//percorrer cada neuronio da camada atual
            //saída é o somatorio dos pesos com os valores dos neuronios
            //aplicado na função de ativação
            soma = 0.0;
            for(k = 0; k < camadaAnterior.neuronios.length; k++){
               soma += camadaAnterior.neuronios[k].saida * camadaAnterior.neuronios[k].pesos[j];
            }
            camadaAtual.neuronios[j].entrada = soma;
            soma += BIAS;
            camadaAtual.neuronios[j].saida = funcaoAtivacao(soma);
         }
      }

      //saída
      for(i = 0; i < this.saida.neuronios.length; i++){
         soma = 0.0;
         for(j = 0; j < (this.ocultas[this.qtdCamadasOcultas-1].neuronios.length); j++){
            soma += (
               this.ocultas[this.qtdCamadasOcultas-1].neuronios[j].saida *
               this.ocultas[this.qtdCamadasOcultas-1].neuronios[j].pesos[i]
            ); 
         }
         this.saida.neuronios[i].entrada = soma;
         this.saida.neuronios[i].saida = funcaoAtivacaoSaida(soma);
      }
   }

   
   //adaptar para multiplas saídas
   //separar os parametros para dados e classes
   public double calcularPrecisao(double[][] dados){
      double precisao = 0;

      double[] dados_treino = new double[dados[0].length-1];
      double[] classe_treino = new double[1];
      int acertosTotais = 0;
      int acertosSaida = 0;

      for(int i = 0; i < dados.length; i++){
         dados_treino[0] = dados[i][0];
         dados_treino[1] = dados[i][1];
         dados_treino[2] = dados[i][2];
         classe_treino[0]= dados[i][3];

         this.calcularSaida(dados_treino);

         for(int j = 0; j < this.saida.neuronios.length; j++){
            if(this.saida.neuronios[j].saida == classe_treino[j]){
               acertosSaida ++;
            }
         }
         
         if(acertosSaida == (this.saida.neuronios.length)) acertosTotais++;

      }

      precisao = (double) acertosTotais/dados.length;
      return (precisao*100);
   }


   /**
    * em teste.
    */
   public void treinar(double[][] dados, double[] saida, int epochs){
      double[] dados_treino = new double[dados[0].length];
      double[] saida_treino = new double[1];

      int i, j, k;
      for(i = 0; i < epochs; i++){
         
         for(j = 0; j < dados[0].length; j++){//percorrer as linhas dos dados
            for(k = 0; k < dados.length; k++){//percorrer as colunas dos dados
               dados_treino[k] = dados[j][k];
            }
            saida_treino[0] = saida[j];
            backpropagation(dados_treino, saida_treino);
         }
      }
   }


   /**
    * em teste,
    * as vezes gera NaN nos erros.
    * @param dados
    * @param saidaEsperada
    */
   public void backpropagation(double[] dados, double[] saidaEsperada){
      if(saidaEsperada.length != this.saida.neuronios.length){
         System.out.println("imcompatibilidade de dimensões");
         return;
      }

      //calcular saída para aplicar o erro
      this.calcularSaida(dados);

      //CALCULANDO OS ERRROS DAS CAMADAS
      //calcular erros da saída
      for(i = 0; i < this.saida.neuronios.length; i++){
         this.saida.neuronios[i].erro = (this.saida.neuronios[i].saida - saidaEsperada[i]) * funcaoAtivacaoSaidaDx(this.saida.neuronios[i].saida);
      }

      //propagar o erros para as camadas ocultas
      for(i = (this.ocultas.length-1); i >= 0; i--){
         Camada camadaAtual = this.ocultas[i];
         Camada proximaCamada;
         if(i == (this.ocultas.length-1)) proximaCamada = this.saida;
         else proximaCamada = this.ocultas[i+1];

         //percorrer neuronios da camada atual
         for(j = 0; j < camadaAtual.neuronios.length; j++){

            double erro = 0.0;    
            for(k = 0; k < proximaCamada.neuronios.length; k++){
               erro += (proximaCamada.neuronios[k].erro * camadaAtual.neuronios[j].pesos[k]);
            }
            camadaAtual.neuronios[j].erro = erro * funcaoAtivacaoDx(camadaAtual.neuronios[j].saida);
         }
      }

      //não precisa calcular erros da entrada
      //são apenas os dados 

      //ATUALIZANDO OS PESOS --------------------------------
      //atualização dos pesos da saída
      for(i = 0; i < this.saida.neuronios.length; i++){
         Neuronio neuronio = this.saida.neuronios[i];
         for(j = 0; j < neuronio.pesos.length; j++){
            double gradiente = neuronio.erro * neuronio.entrada;
            neuronio.pesos[j] -= (TAXA_APRENDIZAGEM * gradiente);
         }
      }

      //atualização dos pesos das camadas ocultas
      for(i = this.ocultas.length - 1; i >= 0; i--){
         Camada camadaAtual = this.ocultas[i];

         for(j = 0; j < camadaAtual.neuronios.length; j++){
            Neuronio neuronio = camadaAtual.neuronios[j];

            for(k = 0; k < neuronio.pesos.length; k++){
               double gradiente = neuronio.erro * neuronio.entrada;
               neuronio.pesos[k] -= TAXA_APRENDIZAGEM * gradiente;
            }
         }
      }
   }


   /**
    * Define a função de ativação que a rede usará nos neurônios das camadas ocultas e na camada
    * de saída, por padrão será usado ReLu e ReLu derivada, respectivamente.
    * Segue a lista das funções disponíveis:
    * <ul>
    *    <li> 1 - ReLu. </li>
    *    <li> 2 - ReLu derivada. </li>
    *    <li> 3 - Sigmoide. </li>
    *    <li> 4 - Sigmoid derivada .</li>
    *    <li> 5 - Tangente hiperbólica. </li>
    *    <li> 6 - Tangente hiperbólica derivada. </li>
    *    <li> 7 - Leaky ReLu. </li>
    * </ul>
    * @param ocultas função de ativação das camadas ocultas
    * @param saida função de ativação da ultima camada oculta para a saída
    */
   public void configurarFuncaoAtivacao(int ocultas, int saida){
      if(ocultas < 1 || ocultas > 7) funcaoAtivacao = 1;
      if(saida < 1 || saida > 7) funcaoAtivacaoSaida = 2;

      funcaoAtivacao = ocultas;
      funcaoAtivacaoSaida = saida;
   }


   /**
    * Configura o valor de alcance dos pesos das ligações de cada neurônio da rede. O valor
    * padrão de alcance dos pesos é de 100, significa dizer que os pesos gerados podem variar entre -100 e 100.
    * <p> O valor de alcance <strong>NÃO</strong> deve ser menor ou igual a zero. </p>
    * @param alcancePeso novo valor de alcance dos pesos
    */
   public void configurarAlcancePeso(double alcancePeso){
      this.alcancePeso = alcancePeso;
   }


   //FUNÇÕES DE ATIVAÇÃO
   private double funcaoAtivacao(double valor){
      if(funcaoAtivacao == ativacaoRelu) return relu(valor);
      if(funcaoAtivacao == ativacaoReluDx) return reluDx(valor);
      if(funcaoAtivacao == ativacaoSigmoid) return sigmoid(valor);
      if(funcaoAtivacao == ativacaoSigmoidDx) return sigmoidDx(valor);
      if(funcaoAtivacao == ativacaoTanH) return tanH(valor);
      if(funcaoAtivacao == ativacaoTanHDx) return tanHDx(valor);
      if(funcaoAtivacao == ativacaoLeakyRelu) return leakyRelu(valor);

      else return valor;
   }


   private double funcaoAtivacaoSaida(double valor){
      if(funcaoAtivacaoSaida == ativacaoRelu) return relu(valor);
      if(funcaoAtivacaoSaida == ativacaoReluDx) return reluDx(valor);
      if(funcaoAtivacaoSaida == ativacaoSigmoid) return sigmoid(valor);
      if(funcaoAtivacaoSaida == ativacaoSigmoidDx) return sigmoidDx(valor);
      if(funcaoAtivacaoSaida == ativacaoTanH) return tanH(valor);
      if(funcaoAtivacaoSaida == ativacaoTanHDx) return tanHDx(valor);
      if(funcaoAtivacaoSaida == ativacaoLeakyRelu) return leakyRelu(valor);

      else return valor;
   }


   private double funcaoAtivacaoDx(double valor){
      if(funcaoAtivacao == ativacaoRelu) return reluDx(valor);
      if(funcaoAtivacao == ativacaoSigmoid) return sigmoidDx(valor);
      if(funcaoAtivacao == ativacaoTanH) return tanHDx(valor);

      return valor;
   }


   private double funcaoAtivacaoSaidaDx(double valor){
      if(funcaoAtivacaoSaida == ativacaoRelu) return reluDx(valor);
      if(funcaoAtivacaoSaida == ativacaoReluDx) return reluDx(valor);
      if(funcaoAtivacaoSaida == ativacaoSigmoid) return sigmoidDx(valor);
      if(funcaoAtivacaoSaida == ativacaoTanH) return tanHDx(valor);

      return valor;
   }


   private double relu(double valor){
      if(valor < 0) return 0;
      return valor;
   }


   private double reluDx(double valor){
      if(valor < 0) return 0;
      return 1;     
   }


   private double sigmoid(double valor){
      return (1 / (1 + Math.exp(-valor)));
   }


   private double sigmoidDx(double valor){
      return (sigmoid(valor) * (1-sigmoid(valor)));
   }


   private double tanH(double valor){
      return Math.tanh(valor);
   }


   private double tanHDx(double valor){
      double resultado = Math.tanh(valor);
      return (1 - Math.pow(resultado, 2));
   }


   private double leakyRelu(double valor){
      if(valor > 0) return valor;
      else return ((0.001) * valor);
   }


   //implementar função de ativação argmax para a saída da rede
   @SuppressWarnings("unused")
   private void argmax(){
      double maiorValor = 0;
      int i, indiceMaiorSaida = 0;

      for(i = 0; i < this.saida.neuronios.length; i++){
         if(i == 0){
            maiorValor = this.saida.neuronios[i].entrada;
            indiceMaiorSaida = i;
         
         }else if(this.saida.neuronios[i].entrada > maiorValor){
            maiorValor = this.saida.neuronios[i].entrada;
            indiceMaiorSaida = i;
         }
      }

      for(i = 0; i < this.saida.neuronios.length; i++){
         if(i == indiceMaiorSaida) this.saida.neuronios[i].saida = 1;
         else this.saida.neuronios[i].saida = 0;
      }

   }



   /**
    * Clona a instância da rede.
    * @return Clone da rede
    */
   @Override
   public RedeNeural clone(){
      try {
         RedeNeural clone = (RedeNeural) super.clone();

         // Clonar dados importantes
         clone.qtdNeuroniosEntrada = this.qtdNeuroniosEntrada;
         clone.qtdNeuroniosOcultas = this.qtdNeuroniosOcultas;
         clone.qtdNeuroniosSaida = this.qtdNeuroniosSaida;
         clone.qtdCamadasOcultas = this.qtdCamadasOcultas;
         clone.BIAS = this.BIAS;
         clone.TAXA_APRENDIZAGEM = this.TAXA_APRENDIZAGEM;

         // Clonar camada de entrada
         clone.entrada = cloneCamada(this.entrada);

         // Clonar camadas ocultas
         clone.ocultas = new Camada[qtdCamadasOcultas];
         for (int i = 0; i < qtdCamadasOcultas; i++) {
            clone.ocultas[i] = cloneCamada(this.ocultas[i]);
         }

         // Clonar camada de saída
         clone.saida = cloneCamada(this.saida);

         return clone;
      } catch (CloneNotSupportedException e){
         throw new RuntimeException(e);
      }
   }


   private Camada cloneCamada(Camada camada){
      Camada clone = new Camada();
      clone.neuronios = new Neuronio[camada.neuronios.length];

      for (int i = 0; i < camada.neuronios.length; i++) {
         clone.neuronios[i] = cloneNeuronio(camada.neuronios[i], camada.neuronios[i].qtdLigacoes, camada.neuronios[i].pesos);
      }

      return clone;
   }


   private Neuronio cloneNeuronio(Neuronio neuronio, int qtdLigacoes, double[] pesos){
      Neuronio clone = new Neuronio(neuronio.pesos.length, this.alcancePeso);

      double pesosClone[] = new double[qtdLigacoes];

      for(int i = 0; i < pesos.length; i++){
         pesosClone[i] = pesos[i];
      }

      clone.pesos = pesosClone;
      clone.qtdLigacoes = qtdLigacoes;

      return clone;
   }

   /**
    * Salva a classe da rede em um arquivo especificado, o caminho não leva em consideração
    * o formato, de preferência deve ser .dat, caso seja especificado apenas o nome, o arquivo
    * será salvo no mesmo diretório que o arquivo principal.
    * @param caminho caminho de destino do arquivo que será salvo.
    */
   public void salvarRedeArquivo(String caminho){
      try{
         FileOutputStream arquivo = new FileOutputStream(caminho);
         ObjectOutputStream objeto = new ObjectOutputStream(arquivo);

         objeto.writeObject(this);
         objeto.close();
         arquivo.close();

      }catch(Exception e){
         e.printStackTrace();
      }
   }


   /**
    * Lê um arquivo de rede neural no caminho especificado, o caminho não leva em consideração
    * o formato, logo precisa ser especificado.
    * @param caminho caminho do arquivo de rede salvo
    * @return Rede lida pelo arquivo.
    */
   public RedeNeural lerRedeArquivo(String caminho){
      RedeNeural rede = null;

      try{
         FileInputStream arquivo = new FileInputStream(caminho);
         ObjectInputStream objeto = new ObjectInputStream(arquivo);

         rede = (RedeNeural) objeto.readObject();
         objeto.close();
         arquivo.close();

      }catch(Exception e){
         e.printStackTrace();
      }

      return rede;
   }
}