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
   
   private int qtdNeuroniosEntrada;
   private int qtdNeuroniosOcultas;
   private int qtdNeuroniosSaida;
   private int qtdCamadasOcultas;

   private int BIAS = 0;
   private double alcancePeso = 100;
   private double TAXA_APRENDIZAGEM = 0.1;

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
    * <p>
    *    Cria uma instância de rede neural artificial. A arquitetura da rede se 
    *    baseia em uma camada de entrada, várias camadas ocultas mas com o mesmo 
    *    número de neurônios cada, e uma camada de saída.
    * </p>
    * os valores de todos os parâmetros pedidos <strong>NÃO devem</strong>
    * ser menores que 1.
    * <p>
    *    Após instanciar o modelo, é necessário compilar por meio da função "compilar()", certifique-se 
    *    de configurar as propriedades da rede por meio das funções de configuração fornecidas como, alcance
    *    dos pesos iniciais, funções de ativação e quantidade de bias. Caso não seja usada nenhuma das funções 
    *    de configuração, a rede será compilada com os valores padrão.
    * </p>
    * @author Thiago Barroso, acadêmico de Engenharia da Computação pela Universidade Federal do Pará, Campus Tucuruí.
    * @param qtdNeuroniosEntrada quantidade de neurônios na camada de entrada.
    * @param qtdNeuroniosOcultas quantidade de neurônios das camadas ocultas.
    * @param qtdNeuroniosSaida quantidade de neurônios na camada de saída.
    * @param qtdCamadasOcultas quantidade de camadas ocultas.
    * @throws IllegalArgumentException se os valores fornecidos forem menores que um.
    */
   public RedeNeural(int qtdNeuroniosEntrada, int qtdNeuroniosOcultas, int qtdNeuroniosSaida, int qtdCamadasOcultas){
      if(qtdNeuroniosEntrada < 1 || qtdNeuroniosOcultas < 1 || qtdNeuroniosSaida < 1 || qtdCamadasOcultas < 1){
         throw new IllegalArgumentException("Os valores fornecidos devem ser maiores ou iguais a um.");
      }

      this.qtdNeuroniosEntrada = qtdNeuroniosEntrada;
      this.qtdNeuroniosOcultas = qtdNeuroniosOcultas;
      this.qtdNeuroniosSaida = qtdNeuroniosSaida;
      this.qtdCamadasOcultas = qtdCamadasOcultas;
   }


   /**
    * Define o valor máximo e mínimo na hora de aleatorizar os pesos da rede 
    * para a compilação, os novos valores não podem ser menores ou iguais a zero.
    * <p>O valor padrão de alcance é 100.</p>
    * @param alcancePesos novo valor máximo e mínimo.
    * @throws IllegalArgumentException se o novo valor for menor ou igual a zero.
    */
   public void configurarAlcancePesos(double alcancePesos){
      if(alcancePesos <= 0) throw new IllegalArgumentException("Os novos valores de alcance dos pesos não podem ser menores ou iguais a zero.");
      this.alcancePeso = alcancePesos;
   }


   /**
    * Define a quantidade de neurônios adicionais que atuarão como viés da rede, eles não são
    * considerados como parte dos dados de entrada.
    * <p>O valor padrão para o bias é 0.</p>
    * @param qtdBias novo valor para a quantidade de bias.
    * @throws IllegalArgumentException se o novo valor for menor que zero.
    */
   public void configurarBias(int qtdBias){
      if(qtdBias < 0) throw new IllegalArgumentException("O novo valor do bias não pode ser menor que zero.");
      this.BIAS = qtdBias;
   }


   /**
    * Define a função de ativação que a rede usará nos neurônios das camadas ocultas 
    * e na camada de saída.
    * <p>Os valores padrão são 1 e 2.</p>
    * Funções de ativação disponíveis:
    * <ul>
    *    <li> 1 - ReLU. </li>
    *    <li> 2 - ReLU derivada. </li>
    *    <li> 3 - Sigmoide. </li>
    *    <li> 4 - Sigmoid derivada .</li>
    *    <li> 5 - Tangente hiperbólica. </li>
    *    <li> 6 - Tangente hiperbólica derivada. </li>
    *    <li> 7 - Leaky ReLU. </li>
    * </ul>
    * @param ocultas função de ativação das camadas ocultas.
    * @param saida função de ativação da ultima camada oculta para a saída.
    * @throws IllegalArgumentException se os valores fornecidos forem menores que 1 ou maiores que 7.
    */
   public void configurarFuncaoAtivacao(int ocultas, int saida){
      if((ocultas < 1) || (ocultas > 7) || (saida < 1) || (saida > 7)) throw new IllegalArgumentException("Os valores fornecidos não podem ser menores que 1, nem maiores que 7");

      funcaoAtivacao = ocultas;
      funcaoAtivacaoSaida = saida;
   }


   /**
    * Define o novo valor de taxa de aprendizagem da rede. O valor é usado durante o método de treino.
    * Certifique-se de não usar valores muito altos ou muito baixos para não gerar valores inesperados 
    * durante o treino.
    * <p>O valor padrão é 0.1</p>
    * @param taxaAprendizagem novo valor de taxa de aprendizagem.
    * @throws IllegalArgumentException caso o novo valor de taxa de aprendizagem seja igual a zero.
    */
   public void configurarTaxaAprendizagem(double taxaAprendizagem){
      if(taxaAprendizagem == 0){
         throw new IllegalArgumentException("O valor da nova taxa de aprendizagem não pode ser igual a zero.");
      }
      this.TAXA_APRENDIZAGEM = taxaAprendizagem;
   }


   /**
    * Compila o modelo de rede baseado nos valores fornecidos. Antes da compilação é possível
    * informar alguns valores ajustáveis na inicialização da rede, como:
    * <ul>
    *    <li>Valor máximo e mínimo para os pesos gerados aleatoriamente.</li>
    *    <li>Funções de ativação para as camadas ocultas e para a camada de saída.</li>
    *    <li>Quantidade de neurônios atuando como bias.</li>
    *    <li>Taxa de aprendizagem.</li>
    * </ul>
    * <p>
    *    Caso nenhuma configuração seja feita, a rede será inicializada com os valores padrão. 
    * </p>
    * Após a compilação o modelo está pronto para ser usado.
    */
   public void compilar(){
      //inicializar camada de entrada
      int QTD_NEURONIOS_ENTRADA = qtdNeuroniosEntrada + BIAS;
      int QTD_NEURONIOS_OCULTAS = qtdNeuroniosOcultas + BIAS;
      
      entrada = new Camada();
      entrada.neuronios = new Neuronio[QTD_NEURONIOS_ENTRADA];//BIAS como neuronio adicional
      for(int i = 0; i < entrada.neuronios.length; i++){
         entrada.neuronios[i] = new Neuronio(QTD_NEURONIOS_OCULTAS, alcancePeso);
      }

      //inicializar camadas ocultas
      ocultas = new Camada[qtdCamadasOcultas];
      for (int i = 0; i < qtdCamadasOcultas; i++){
         Camada novaOculta = new Camada();
         novaOculta.neuronios = new Neuronio[QTD_NEURONIOS_OCULTAS];
      
         for (int j = 0; j < novaOculta.neuronios.length; j++){
            if (i == (qtdCamadasOcultas-1)){
               novaOculta.neuronios[j] = new Neuronio(qtdNeuroniosSaida, alcancePeso);
            
            }else{
               novaOculta.neuronios[j] = new Neuronio(QTD_NEURONIOS_OCULTAS, alcancePeso);
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
    * @param dados dados usados para a camada de entrada.
    * @throws IllegalArgumentException se o tamanho dos dados de entrada for diferente do tamanho dos neurônios de entrada, sem contar os bias.
    */
   public void calcularSaida(double[] dados){
      if(dados.length != (this.entrada.neuronios.length-BIAS)){
         throw new IllegalArgumentException("As dimensões dos dados de entrada com os neurônios de entrada da rede não são iguais");
      }

      //entrada
      for(i = 0; i < (this.entrada.neuronios.length-BIAS); i++){
         this.entrada.neuronios[i].saida = dados[i];
      }

      //ocultas
      double soma = 0.0;
      for(i = 0; i < this.qtdCamadasOcultas; i++){//percorrer camadas ocultas

         Camada camadaAtual = this.ocultas[i];
         Camada camadaAnterior;
         if(i == 0) camadaAnterior = this.entrada;
         else camadaAnterior = this.ocultas[i-1];

         for(j = 0; j < camadaAtual.neuronios.length-BIAS; j++){//percorrer cada neuronio da camada atual
            //saída é o somatorio dos pesos com os valores dos neuronios
            //aplicado na função de ativação
            soma = 0.0;
            for(k = 0; k < camadaAnterior.neuronios.length; k++){
               soma += camadaAnterior.neuronios[k].saida * camadaAnterior.neuronios[k].pesos[j];
            }
            camadaAtual.neuronios[j].entrada = soma;
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

   
   /**
    * Calcula a precisão de saída da rede de acordo com os dados fornecidos.
    * O cálculo é feito comparando diretamente o valor de saída da rede com a saída fornecida, então
    * o uso desse método pode não ser apropriado para aplicações onde as saídas são valores contínuos.
    * @param dados matriz com os dados de entrada.
    * @param saida matriz com os dados de saída.
    * @return precisão obtida com base nos dados fornecidos.
    * @throws IllegalArgumentException se o tamanho dos dados de entrada for diferente do tamanho dos neurônios de entrada, sem contar os bias.
    * @throws IllegalArgumentException se o tamanho dos dados de saída for diferente do tamanho dos neurôniosde de saída.
    */
   public double calcularPrecisao(double[][] dados, double[][] saida){
      if(dados[0].length != this.entrada.neuronios.length-BIAS){
         throw new IllegalArgumentException("Incompatibilidade entre os dados de entrada e os neurônios de entrada da rede");
      }
      if(saida[0].length != this.saida.neuronios.length){
         throw new IllegalArgumentException("Incompatibilidade entre os dados de saída e os neurônios de saída da rede");
      }

      double[] dadosEntrada = new double[dados[0].length];//tamanho das colunas dos dados de entrada
      double[] dadosSaida = new double[saida[0].length];// tamanho das colunas dos dados de saída
      double precisao = 0;
      int acertosTotais = 0;
      int acertosSaida = 0;

      for(int i = 0; i < dados.length; i++){//percorrer linhas dos dados
         for(int j = 0; j < dados[0].length; j++){//preencher dados de entrada
            dadosEntrada[j] = dados[i][j];
         }
         for(int j = 0; j < saida[0].length; j++){//preencher dados de saída desejada
            dadosSaida[j] = saida[i][j];
         }

         this.calcularSaida(dadosEntrada);
         for(int k = 0; k < this.saida.neuronios.length; k++){
            if(this.saida.neuronios[k].saida == dadosSaida[k]) acertosSaida ++;
         }
         if(acertosSaida == this.saida.neuronios.length) acertosTotais++;
      }

      precisao = (double)(acertosTotais/dados.length);
      return precisao;
   }


   /**
    * Calcula a função de custo baseada nos dados de entrada e na saída esperada para eles por meio do erro médio quadrado.
    * @param dados matriz de dados de entrada.
    * @param saida matriz dos dados de saída.
    * @return valor de custo da rede.
    * @throws IllegalArgumentException se o tamanho dos dados de entrada for diferente do tamanho dos neurônios de entrada, sem contar os bias.
    * @throws IllegalArgumentException se o tamanho dos dados de saída for diferente do tamanho dos neurôniosde de saída.
    */
   public double funcaoDeCusto(double[][] dados, double[][] saida){
      if(dados[0].length != this.entrada.neuronios.length-BIAS){
         throw new IllegalArgumentException("Incompatibilidade entre os dados de entrada e os neurônios de entrada da rede");
      }
      if(saida[0].length != this.saida.neuronios.length){
         throw new IllegalArgumentException("Incompatibilidade entre os dados de saída e os neurônios de saída da rede");
      }

      double custo = 0.0;
      double[] dados_entrada = new double[dados[0].length];//tamanho das colunas da entrada
      double[] dados_saida = new double[saida[0].length];//tamanho de colunas da saída

      for(int i = 0; i < dados.length; i++){//percorrer as linhas da entrada
         for(int j = 0; j < this.entrada.neuronios.length; j++){//passar os dados para a entrada da rede
            dados_entrada[j] = dados[i][j];
         }
         for(int j = 0; j < this.saida.neuronios.length; j++){//passar os dados de saída desejada para o vetor
            dados_saida[j] = saida[i][j];
         }

         //calcular saída com base nos dados passados
         this.calcularSaida(dados_entrada);

         //calcular custo com base na saída
         for(int k = 0; k < this.saida.neuronios.length; k++){
            custo += Math.pow((dados_saida[k] - this.saida.neuronios[k].saida), 2);
         }
      }

      custo /= dados.length;

      return custo;
   }


   /**
    * <p><strong>Em teste<strong></p>
    * Treina a rede com uso do método Backpropagation.
    * @param dados matriz de dados de entrada.
    * @param saida matriz de dados de saída.
    * @param epochs quantidade de épocas do treino.
    * @throws IllegalArgumentException se o tamanho dos dados de entrada for diferente do tamanho dos neurônios de entrada, sem contar os bias.
    * @throws IllegalArgumentException se o tamanho dos dados de saída for diferente do tamanho dos neurôniosde de saída.
    * @throws IllegalArgumentException se o valor de epochs for menor que um.
    */
   public void treinar(double[][] dados, double[][] saida, int epochs){
      if(dados[0].length != this.entrada.neuronios.length-BIAS){
         throw new IllegalArgumentException("Incompatibilidade entre os dados de entrada e os neurônios de entrada da rede");
      }
      if(saida[0].length != this.saida.neuronios.length){
         throw new IllegalArgumentException("Incompatibilidade entre os dados de saída e os neurônios de saída da rede");
      }
      if(epochs < 1){
         throw new IllegalArgumentException("O valor de epochs não pode ser menor que um");
      }

      double[] dadosEntrada = new double[dados[0].length];//tamanho de colunas da entrada
      double[] dadosSaida = new double[saida[0].length];//tamanho de colunas da saída

      for(int i = 0; i < epochs; i++){//quantidade de épocas

         for(int j = 0; j < dados.length; j++){//percorrer linhas dos dados

            for(int k = 0; k < dados[0].length; k++){//preencher dados de entrada
               dadosEntrada[k] = dados[j][k];
            }
            for(int k = 0; k < dadosSaida.length; k++){//preencher dados de saída
               dadosSaida[k] = saida[j][k];
            }

            this.backpropagation(dadosEntrada, dadosSaida);//aplicar treino
         }      
      }
   }


   /**
    * <strong>Em teste</strong>
    * Retropropaga o erro da rede de acorodo com o dado aplicado e a saída esperada, depois
    * corrige os pesos com a técnica de gradiente descendente.
    * @param dados array com os dados de entrada.
    * @param saidaEsperada array com as saídas esperadas
    * @throws IllegalArgumentException se o tamanho dos dados de entrada for diferente do tamanho dos neurônios de entrada, sem contar os bias.
    * @throws IllegalArgumentException se o tamanho dos dados de saída for diferente do tamanho dos neurôniosde de saída.
    */
   public void backpropagation(double[] dados, double[] saidaEsperada){
      if(dados.length != (this.entrada.neuronios.length-BIAS)){
         throw new IllegalArgumentException("O tamanho dos dados de entrada não corresponde ao tamaho dos neurônios de entrada da rede, com exceção dos bias");
      }
      if(saidaEsperada.length != this.saida.neuronios.length){
         throw new IllegalArgumentException("O tamanho dos dados de saída não corresponde ao tamaho dos neurônios de saída da rede");
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


   //FUNÇÕES DE ATIVAÇÃO---------------------------
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