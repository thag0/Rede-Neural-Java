package rna;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Random;

import rna.otimizadores.AdaGrad;
import rna.otimizadores.Adam;
import rna.otimizadores.GradientDescent;
import rna.otimizadores.Otimizador;
import rna.otimizadores.RMSProp;
import rna.otimizadores.SGD;


/**
 * Modelo de Rede Neural Multilayer Perceptron baseado em feedforward criado do zero. Possui um conjunto de camadas 
 * e cada camada possui um conjunto de neurônios artificiais.
 * <p>
 *    O modelo pode ser usado para problemas de regressão e classificação, contando com algoritmos de treino 
 *    atualmente baseados no backpropagation com adição da ideia de momentum na atualização dos pesos.
 * </p>
 * Possui opções de configuração tanto para hiperparâmetros como taxa de aprendizagem e momentun, quanto para
 * funções de ativações de camadas individuais, valor de alcance máximo e mínimo na aleatorização dos pesos iniciais e 
 * otimizadores que serão usados durante o treino. 
 * <p>
 *    Após configurar as propriedades da rede, o modelo precisará ser compilado para efetivamente poder ser utilizado.
 * </p>
 * @author Thiago Barroso, acadêmico de Engenharia da Computação pela Universidade Federal do Pará, Campus Tucuruí. Ano de 2023.
 */
public class RedeNeural implements Cloneable, Serializable{
   //estrutura 
   public Camada entrada;
   public Camada[] ocultas;
   public Camada saida;
   private int[] arquitetura;
   
   //parâmertos importantes
   private double TAXA_APRENDIZAGEM = 0.01;
   private double TAXA_MOMENTUM = 0;
   private int BIAS = 1;
   private double alcancePeso = 1.0;
   private boolean modeloCompilado = false;
   private Otimizador otimizadorAtual = new SGD();//otimizador padrão
   private boolean nesterov = false;// acelerador de nesterov
   
   Random random = new Random();//treino embaralhado

   private boolean calcularHistoricoErro = false;
   private boolean calcularHistoricoCusto = false;
   ArrayList<Double> historicoErro = new ArrayList<>();
   ArrayList<Double> historicoCusto = new ArrayList<>();


   //TODO
   //Implementar formas melhores de treinar com uma grande quantidade de dados.
   //Formas mais elaboradas de otmizadores.
   //Suporte ao treino da rede usando argmax ou softmax na saída


   /**
    * <p>
    *    Cria uma instância de rede neural artificial. A arquitetura da rede será baseada de acordo com cada posição do array,
    *    cada valor contido nele representará a quantidade de neurônios da camada correspondente.
    * </p> 
    * <p>
    *    A camada de entrada deverá ser especificada pelo indice 0, a camada de saída 
    *    será representada pelo último valor do array e as camadas ocultas serão representadas pelos valores intermediários.
    * </p>
    * <p>
    *   Os valores de todos os parâmetros pedidos <strong>NÃO devem</strong> ser menores que 1.
    * </p>
    * <p>
    *    Após instanciar o modelo, é necessário compilar por meio da função <pre>compilar()</pre>
    * </p>
    * <p>
    *    Certifique-se de configurar as propriedades da rede por meio das funções de configuração fornecidas
    *    como, alcance dos pesos iniciais, taxa de aprendizagem e uso de bias. Caso não seja usada nenhuma 
    *    das funções de configuração, a rede será compilada com os valores padrão.
    * </p>
    * @author Thiago Barroso, acadêmico de Engenharia da Computação pela Universidade Federal do Pará, Campus Tucuruí. Ano de 2023.
    * @param arquitetura modelo de arquitetura específico da rede.
    * @throws IllegalArgumentException se o array de arquitetura não possuir, pelo menos, três elementos.
    * @throws IllegalArgumentException se os valores fornecidos forem menores que um.
    */
   public RedeNeural(int[] arquitetura){
      if(arquitetura.length < 3) throw new IllegalArgumentException("A arquitetura da rede não pode conter menos de três elementos");
      
      for(int i = 0; i < arquitetura.length; i++){
         if(arquitetura[i] < 1) throw new IllegalArgumentException("Os valores fornecidos devem ser maiores ou iguais a um.");
      }

      int quantidadeOcultas = arquitetura.length-2;//evitar problemas
      this.arquitetura = new int[1 + quantidadeOcultas + 1];
      this.arquitetura[0] = arquitetura[0];
      for(int i = 0; i < quantidadeOcultas; i++) this.arquitetura[i+1] = arquitetura[i+1];
      this.arquitetura[this.arquitetura.length-1] = arquitetura[arquitetura.length-1];
   }


   /**
    * <p>
    *    Cria uma instância de rede neural artificial. A arquitetura da rede será baseada de acordo com os valores 
    *    de número de neurônios fornecidos.
    * </p>
    * <p>
    *   Os valores de todos os parâmetros pedidos <strong>NÃO devem</strong> ser menores que 1.
    * </p>
    * <p>
    *    Após instanciar o modelo, é necessário compilar por meio da função <pre>compilar()</pre>
    * </p>
    * <p>
    *    Certifique-se de configurar as propriedades da rede por meio das funções de configuração fornecidas
    *    como, alcance dos pesos iniciais, taxa de aprendizagem e uso de bias. Caso não seja usada nenhuma 
    *    das funções de configuração, a rede será compilada com os valores padrão.
    * </p>
    * @param nEntrada número de neurônios da camada de entrada.
    * @param nOcultas número de neurônios das camadas ocultas.
    * @param nSaida número de neurônios da camada de saída.
    * @param qOcultas quantidade de camadas ocultas.
    * @author Thiago Barroso, acadêmico de Engenharia da Computação pela Universidade Federal do Pará, Campus Tucuruí. Ano de 2023.
    * @throws IllegalArgumentException se algum dos valores fornecidos for menor que 1.
    */
   public RedeNeural(int nEntrada, int nOcultas, int nSaida, int qOcultas){
      if(nEntrada < 1 || nOcultas < 1 || nSaida < 1 || qOcultas < 1){
         throw new IllegalArgumentException("Os valores fornecidos devem ser maiores ou iguais a 1.");
      }

      this.arquitetura = new int[1 + qOcultas + 1];

      this.arquitetura[0] = nEntrada;
      for(int i = 1; i < this.arquitetura.length-1; i++){
         this.arquitetura[i] = nOcultas;
      }
      this.arquitetura[this.arquitetura.length-1] = nSaida;
   }


   /**
    * Define o valor máximo e mínimo na hora de aleatorizar os pesos da rede 
    * para a compilação, os novos valores não podem ser menores ou iguais a zero.
    * <p>
    *    O valor padrão de alcance é 1.
    * </p>
    * @param alcancePesos novo valor máximo e mínimo.
    * @throws IllegalArgumentException se o novo valor for menor ou igual a zero.
    */
   public void configurarAlcancePesos(double alcancePesos){
      if(alcancePesos <= 0){
         throw new IllegalArgumentException("Os novos valores de alcance dos pesos não podem ser menores ou iguais a zero.");
      }
      this.alcancePeso = alcancePesos;
   }


   /**
    * Define se a rede neural usará um neurônio adicional como bias nas camadas da rede.
    * O bias não é adicionado na camada de saída.
    * <p>
    *    O valor padrão para uso do bias é true.
    * </p>
    * @param usarBias novo valor para o uso do bias.
    */
   public void configurarBias(boolean usarBias){
      if(usarBias) this.BIAS = 1;
      else this.BIAS = 0;
   }


   /**
    * Define o novo valor de taxa de aprendizagem da rede. O valor é usado durante o método de treino.
    * O valor da taxa de aprendizagem difine "o quanto a rede vai aprender com o erro durante o treinamento".
    * Certifique-se de não usar valores muito altos ou muito baixos para não gerar resultados inesperados 
    * durante o treino.
    * <p>
    *    O valor padrão é 0.1.
    * </p>
    * @param taxaAprendizagem novo valor de taxa de aprendizagem.
    * @throws IllegalArgumentException caso o novo valor de taxa de aprendizagem seja igual a zero.
    */
   public void configurarTaxaAprendizagem(double taxaAprendizagem){
      if(taxaAprendizagem <= 0){
         throw new IllegalArgumentException("O valor da nova taxa de aprendizagem não pode ser menor ou igual a zero.");
      }
      this.TAXA_APRENDIZAGEM = taxaAprendizagem;
   }


   /**
    * Define o novo valor do momentum que vai ser usado no treinamento da rede.
    * <p>
    *    O momentum é uma técnica utilizada no treinamento de redes neurais para acelerar a convergência e
    *    melhorar a estabilidade das atualizações dos pesos. Ele introduz um termo de momento nas atualizações
    *    dos pesos, permitindo que as atualizações tenham inércia, acumulando uma fração dos gradientes das
    *    iterações anteriores.
    * </p>
    * Normalmente esse valor fica entre 0 e 1, onde 0 significa que o momentum não terá efeito  e 1 
    * significa que o momentum terá o máximo de inércia, acumulando totalmente os gradientes anteriores. 
    * <p>
    *    O valor padrão é 0, onde o momentum não é aplicado no treino.
    * </p>
    * @param momentum novo valor de momentum.
    * @throws IllegalArgumentException se o valor de momentum for menor que zero.
    */
   public void configurarMomentum(double momentum){
      if(momentum < 0){
         throw new IllegalArgumentException("O valor de momentum não pode ser menor que zero.");
      }
      this.TAXA_MOMENTUM = momentum;
   }


   /**
    * Configura a função de ativação da camada correspondente, a função de ativação padrão é a ReLU. É preciso
    * compilar o modelo previamente para poder configurar suas funções de ativação.
    * <p>
    *    segue a lista das funções disponíveis:
    * </p>
    * <ul>
    *    <li>1 - ReLU.</li>
    *    <li>2 - Sigmoid.</li>
    *    <li>3 - Tangente Hiperbólica.</li>
    *    <li>4 - Leaky ReLU.</li>
    *    <li>5 - ELU.</li>
    *    <li>6 - Swish.</li>
    *    <li>7 - GELU.</li>
    *    <li>8 - Linear.</li>
    *    <li>9 - Seno.</li>
    *    <li>10 - Argmax.</li>
    *    <li>11 - Softmax.</li>
    * </ul>
    * @param camada camada que será configurada.
    * @param funcaoAtivacao valor relativo a lista de ativações disponíveis.
    * @throws IllegalArgumentException se o modelo não foi compilado previamente.
    */
   public void configurarFuncaoAtivacao(Camada camada, int funcaoAtivacao){
      modeloValido();
      if(camada.equals(this.entrada)){
         throw new IllegalArgumentException("Não é possível configurar função de ativação para a camada de entrada.");
      } 
      
      camada.configurarAtivacao(funcaoAtivacao);
   }


   /**
    * Configura a função de ativação de todas as camadas da rede, a função de ativação padrão é a ReLU. É preciso
    * compilar o modelo previamente para poder configurar suas funções de ativação
    * <p>
    *    segue a lista das funções disponíveis:
    * </p>
    * <ul>
    *    <li>1 - ReLU.</li>
    *    <li>2 - Sigmoid.</li>
    *    <li>3 - Tangente Hiperbólica.</li>
    *    <li>4 - Leaky ReLU.</li>
    *    <li>5 - ELU.</li>
    *    <li>6 - Swish.</li>
    *    <li>7 - GELU.</li>
    *    <li>8 - Linear.</li>
    *    <li>9 - Seno.</li>
    *    <li>10 - Argmax.</li>
    *    <li>11 - Softmax.</li>
    * </ul>
    * @param funcaoAtivacao valor relativo a lista de ativações disponíveis.
    * @throws IllegalArgumentException se o modelo não foi compilado previamente.
    */
   public void configurarFuncaoAtivacao(int funcaoAtivacao){
      modeloValido();
      
      for(Camada camada : this.ocultas) camada.configurarAtivacao(funcaoAtivacao);
      this.saida.configurarAtivacao(funcaoAtivacao);
   }


   /**
    * Configura o otimizador usado durante o treino da rede neural.
    * <p>
    *    O otimizador padrão é o SGD (Gradiente Descendente Estocástico).
    * </p>
    * <p>
    *    Os otimizadores disponíveis são:
    * </p>
    * <ul>
    *    <li>1 - Backpropagation: Método clássico de retropropagação de erro para treinamento de redes neurais.</li>
    *    <li>2 - SGD (Gradiente Descendente Estocástico): Atualiza os pesos usando o conjunto de treino embaralhado.</li>
    *    <li>3 - AdaGrad: Um otimizador que adapta a taxa de aprendizado para cada parâmetro da rede com base em iterações anteriores.</li>
    *    <li>4 - RMSProp: Um otimizador que utiliza a média móvel dos quadrados dos gradientes acumulados para ajustar a taxa de aprendizado.</li>
    *    <li>5 - Adam: Um otimizador que combina o AdaGrad e o Momentum para convergência rápida e estável.</li>
    * </ul>
    * @param otimizador valor do novo otimizador.
    * @throws IllegalArgumentException se o valor fornecido do otimizador estiver fora da lista dos disponíveis.
    */
   public void configurarOtimizador(int otimizador){
      switch(otimizador){
         case 1: this.otimizadorAtual = new GradientDescent(); break;
         case 2: this.otimizadorAtual = new SGD(); break;
         case 3: this.otimizadorAtual = new AdaGrad(); break;
         case 4: this.otimizadorAtual = new RMSProp(); break;
         case 5: this.otimizadorAtual = new Adam(); break;
         default: throw new IllegalArgumentException("Valor fornecido do otimizador é inválido.");
      }
   }


   /**
    * Configura o otimizador usado durante o treino da rede neural.
    * <p>
    *    O otimizador padrão é o SGD (Gradiente Descendente Estocástico).
    * </p>
    * <p>
    *    Os otimizadores disponíveis são:
    * </p>
    * <ul>
    *    <li>1 - Backpropagation: Método clássico de retropropagação de erro para treinamento de redes neurais.</li>
    *    <li>2 - SGD (Gradiente Descendente Estocástico): Atualiza os pesos usando o conjunto de treino embaralhado.</li>
    *    <li>3 - AdaGrad: Um otimizador que adapta a taxa de aprendizado para cada parâmetro da rede com base em iterações anteriores.</li>
    *    <li>4 - RMSProp: Um otimizador que utiliza a média móvel dos quadrados dos gradientes acumulados para ajustar a taxa de aprendizado.</li>
    *    <li>5 - Adam: Um otimizador que combina o AdaGrad e o Momentum para convergência rápida e estável.</li>
    * </ul>
    * @param otimizador valor do novo otimizador.
    * @param nesterov configura se o otimizador vai usar o acelerador de Nesterov (Por enquanto só pro SGD)
    * @throws IllegalArgumentException se o valor fornecido do otimizador estiver fora da lista dos disponíveis.
    */
   public void configurarOtimizador(int otimizador, boolean nesterov){
      switch(otimizador){
         case 1: this.otimizadorAtual = new GradientDescent(); break;
         case 2: this.otimizadorAtual = new SGD(nesterov); break;
         case 3: this.otimizadorAtual = new AdaGrad(); break;
         case 4: this.otimizadorAtual = new RMSProp(); break;
         case 5: this.otimizadorAtual = new Adam(); break;
         default: throw new IllegalArgumentException("Valor fornecido do otimizador é inválido.");
      }

      this.nesterov = nesterov;
   }


   /**
    * Define se durante o processor de treinamento, a rede vai salvar dados relacionados ao 
    * erro médio de cada época.
    * <p>
    *    O valor padrão é false.
    * </p>
    * @param historicoErro se verdadeiro, a rede armazenara o histórico de erros de cada época.
    */
   public void configurarHistoricoErros(boolean historicoErro){
      this.calcularHistoricoErro = historicoErro;
   }


   /**
    * Define se durante o processor de treinamento, a rede vai salvar dados relacionados a 
    * função de custo de cada época.
    * <p>
    *    Calcular o custo é uma operação que pode ser computacionalmente cara, então deve ser
    *    bem avaliado querer ativar ou não esse recurso.
    * </p>
    * <p>
    *    O valor padrão é false.
    * </p>
    * @param historicoCusto se verdadeiro, a rede armazenara o histórico de custo de cada época.
    */
   public void configurarHistoricoCusto(boolean historicoCusto){
      this.calcularHistoricoCusto = historicoCusto;
   }


   /**
    * Compila o modelo de rede inicializando as camadas, neurônios e pesos respectivos, 
    * baseado nos valores fornecidos. Antes da compilação é possível
    * informar alguns valores ajustáveis na inicialização da rede, como:
    * <ul>
    *    <li>Valor máximo e mínimo para os pesos gerados aleatoriamente.</li>
    *    <li>Funções de ativação para as camadas ocultas e para a camada de saída.</li>
    *    <li>Neurônios adicionais nas camadas atuando como bias.</li>
    *    <li>Taxa de aprendizagem.</li>
    *    <li>Taxa de momentum.</li>
    *    <li>Otimizador.</li>
    *    <li>Habilitar histórico de erros e custos durante o treino.</li>
    * </ul>
    * <p>
    *    Caso nenhuma configuração seja feita, a rede será inicializada com os valores padrão. 
    * </p>
    * Após a compilação o modelo está pronto para ser usado, mas deverá ser treinado.
    * <p>
    *    Para treinar o modelo deve-se fazer uso da função função {@code treinar()} informando os 
    *    dados necessários para a rede.
    * </p>
    * <p>
    *    Para usar as predições da rede basta usar a função {@code calcularSaida()} informando os
    *    dados necessários. Após a predição pode-se obter o resultado da rede por meio da função 
    *    {@code obterSaidas()};
    * </p>
    */
   public void compilar(){
      //adicionando bias como neuronio adicional nas camadas
      for(int i = 0; i < arquitetura.length-1; i++){
         arquitetura[i] += BIAS;
      }

      //passar as informações do bias paras as camadas
      boolean temBias = (this.BIAS == 1) ? true : false;
      
      //inicializar camada de entrada
      entrada = new Camada(temBias);
      entrada.neuronios = new Neuronio[arquitetura[0]];
      for(int i = 0; i < entrada.neuronios.length; i++){
         //pesos e entradas nao importam na camada de entrada
         entrada.neuronios[i] = new Neuronio(1, alcancePeso);
      }

      //inicializar camadas ocultas
      int quantidadeOcultas = this.arquitetura.length-2;
      ocultas = new Camada[quantidadeOcultas];
      for(int i = 0; i < this.ocultas.length; i++){// percorrer ocultas
         Camada novaOculta = new Camada(temBias);
         novaOculta.neuronios = new Neuronio[arquitetura[i+1]];
      
         // inicializar neuronios da camada
         for (int j = 0; j < novaOculta.neuronios.length; j++){
            if (i == 0) novaOculta.neuronios[j] = new Neuronio(arquitetura[0], alcancePeso);// neuronios da entrada
            else novaOculta.neuronios[j] = new Neuronio(arquitetura[i], alcancePeso);// neuronios da oculta anterior
         }
         ocultas[i] = novaOculta;
      }

      //inicializar camada de saída
      saida = new Camada(false);
      saida.neuronios = new Neuronio[arquitetura[arquitetura.length-1]];
      for(int i = 0; i < this.saida.neuronios.length; i++){
         // neuronios da ultima oculta
         saida.neuronios[i] = new Neuronio(arquitetura[arquitetura.length-2], alcancePeso);
      }

      modeloCompilado = true;//modelo pode ser usado
   }


   /**
    * Verifica se o modelo já foi compilado para evitar problemas de uso indevido, bem como componentes nulos.
    * @throws IllegalArgumentException se o modelo não foi compilado.
    */
   private void modeloValido(){
      if(!this.modeloCompilado){
         throw new IllegalArgumentException("O modelo ainda não foi compilado");
      }
   }


   /**
    * Propaga os dados de entrada pela rede neural pelo método de feedforward.
    * @param dados dados usados para a camada de entrada.
    * @throws IllegalArgumentException se o modelo não foi compilado previamente.
    * @throws IllegalArgumentException se o tamanho dos dados de entrada for diferente do tamanho dos neurônios de entrada, excluindo o bias.
    */
   public void calcularSaida(double[] dados){
      modeloValido();
      
      if(dados.length != (this.entrada.neuronios.length-BIAS)){
         throw new IllegalArgumentException("As dimensões dos dados de entrada com os neurônios de entrada da rede não são iguais");
      }

      //carregar dados na camada de entrada
      for(int i = 0; i < (this.entrada.neuronios.length-BIAS); i++){
         this.entrada.neuronios[i].saida = dados[i];
      }
      
      //ocultas
      for(int i = 0; i < this.ocultas.length; i++){
         if(i == 0) this.ocultas[i].ativarNeuronios(this.entrada);
         else this.ocultas[i].ativarNeuronios(this.ocultas[i-1]);
      }

      //saída
      this.saida.ativarNeuronios(this.ocultas[this.ocultas.length-1]);
   }

   
   /**
    * Calcula a precisão da rede neural com base nos dados fornecidos.
    * A precisão é calculada como a média do erro absoluto entre a saída prevista pela rede e a saída fornecida.
    * Esse método pode ser adequado para tarefas de regressão, mas não é uma boa abordagem em problemas de classificação
    * ou quando as saídas são valores discretos ou categóricos.
    * @param dados matriz com os dados de entrada.
    * @param saida matriz com os dados de saída.
    * @return precisão obtida com base nos dados fornecidos, um valor entre 0 e 1, onde 1 representa a máxima precisão.
    * @throws IllegalArgumentException se o modelo não foi compilado previamente.
    * @throws IllegalArgumentException se a quantidade de linhas dos dados fornecidos for diferente da quantidade de linhas das saídas fornecidas.
    * @throws IllegalArgumentException se o tamanho dos dados de entrada for diferente do tamanho dos neurônios de entrada, excluindo o bias.
    * @throws IllegalArgumentException se o tamanho dos dados de saída for diferente do tamanho dos neurônios de saída.
    */
   public double calcularPrecisao(double[][] dados, double[][] saida){
      modeloValido();

      if(dados.length != saida.length){
         throw new IllegalArgumentException("A quantidade de linhas de dados e saídas são diferentes");
      }
      if(dados[0].length != (this.entrada.neuronios.length - BIAS)){
         throw new IllegalArgumentException("Incompatibilidade entre os dados de entrada e os neurônios de entrada da rede");
      }
      if(saida[0].length != this.saida.neuronios.length){
         throw new IllegalArgumentException("Incompatibilidade entre os dados de saída e os neurônios de saída da rede");
      }

      double[] dadosEntrada = new double[dados[0].length];
      double[] dadosSaida = new double[saida[0].length];
      double erroMedio = 0;

      for(int i = 0; i < dados.length; i++){ // Percorrer linhas dos dados
         for(int j = 0; j < dados[i].length; j++){ // Preencher dados de entrada
            dadosEntrada[j] = dados[i][j];
         }
         for(int j = 0; j < saida[i].length; j++){ // Preencher dados de saída desejada
            dadosSaida[j] = saida[i][j];
         }

         this.calcularSaida(dadosEntrada);

         for(int k = 0; k < this.saida.neuronios.length; k++){
            erroMedio += Math.abs(dadosSaida[k] - this.saida.neuronios[k].saida);
         }
      }

      erroMedio /= dados.length;
      return (1 - erroMedio); // Converter em um valor relativo a porcentagem
   }
   

   /**
    * Calcula a função de custo baseada nos dados de entrada e na saída esperada para eles por meio do erro médio quadrado.
    * @param dados matriz de dados de entrada.
    * @param saida matriz dos dados de saída.
    * @return valor de custo da rede.
    * @throws IllegalArgumentException se o modelo não foi compilado previamente.
    * @throws IllegalArgumentException se a quantidade de linhas dos dados fornecidos for diferente da quantidade de linhas das saídas fornecidas.
    * @throws IllegalArgumentException se o tamanho dos dados de entrada for diferente do tamanho dos neurônios de entrada, excluindo o bias.
    * @throws IllegalArgumentException se o tamanho dos dados de saída for diferente do tamanho dos neurôniosde de saída.
    */
   public double funcaoDeCusto(double[][] dados, double[][] saida){
      modeloValido();
      
      if(dados.length != saida.length){
         throw new IllegalArgumentException("A quantidade de linhas de dados e saídas são diferentes");
      }
      if(dados[0].length != (this.entrada.neuronios.length-BIAS)){
         throw new IllegalArgumentException("Incompatibilidade entre os dados de entrada e os neurônios de entrada da rede");
      }
      if(saida[0].length != this.saida.neuronios.length){
         throw new IllegalArgumentException("Incompatibilidade entre os dados de saída e os neurônios de saída da rede");
      }

      double[] dados_entrada = new double[dados[0].length];//tamanho das colunas da entrada
      double[] dados_saida = new double[saida[0].length];//tamanho de colunas da saída
      
      int i, j, k;
      double diferenca;
      double custo = 0.0;
      for(i = 0; i < dados.length; i++){//percorrer as linhas da entrada
         for(j = 0; j < (this.entrada.neuronios.length - BIAS); j++){//passar os dados para a entrada da rede
            dados_entrada[j] = dados[i][j];
         }
         for(j = 0; j < this.saida.neuronios.length; j++){//passar os dados de saída desejada para o vetor
            dados_saida[j] = saida[i][j];
         }

         //calcular saída com base nos dados passados
         this.calcularSaida(dados_entrada);

         //calcular custo com base na saída
         for(k = 0; k < this.saida.neuronios.length; k++){
            diferenca = dados_saida[k] - this.saida.neuronios[k].saida;
            custo += (diferenca*diferenca);
         }
      }

      custo /= dados.length;

      return custo;
   }


   /**
    * <p>
    *    Treina a rede de acordo com as configurações predefinidas.
    * </p>
    * Certifique-se de configurar adequadamente o modelo para obter os 
    * melhores resultados.
    * @param dados dados de entrada do treino (features).
    * @param saida dados de saída correspondente a entrada (class).
    * @param epochs quantidade de épocas.
    * @throws IllegalArgumentException se o modelo não foi compilado previamente.
    * @throws IllegalArgumentException se o tamanho dos dados de entrada for diferente do tamanho dos dados de saída.
    * @throws IllegalArgumentException se o tamanho dos dados de entrada for diferente do tamanho dos neurônios de entrada, excluindo o bias.
    * @throws IllegalArgumentException se o tamanho dos dados de saída for diferente do tamanho dos neurôniosde de saída.
    * @throws IllegalArgumentException se o valor de épocas for menor que um.
    */
   public void treinar(double[][] entradas, double[][] saidas, int epochs){
      modeloValido();

      if(entradas.length != saidas.length){
         throw new IllegalArgumentException("A quantidade de dados de entrada e saída são diferentes.");
      }
      if(entradas[0].length != (this.entrada.neuronios.length-BIAS)){
         throw new IllegalArgumentException("O tamanho dos dados de entrada não corresponde ao tamanho dos neurônios de entrada da rede, com exceção dos bias");
      }
      if(saidas[0].length != this.saida.neuronios.length){
         throw new IllegalArgumentException("O tamanho dos dados de saída não corresponde ao tamanho dos neurônios de saída da rede");
      }
      if(epochs < 1){
         throw new IllegalArgumentException("O valor de epochs não pode ser menor que um");
      }

      if(otimizadorAtual.getClass().equals(rna.otimizadores.GradientDescent.class)){
         treino(entradas, saidas, epochs, false);
      }else treino(entradas, saidas, epochs, true);
   }


   /**
    * Treina a rede com a técnica do gradiente estocástico, onde embaralhamos os dados de entrada para tornar o treino "aleatório" mas que tende a
    * convergir mais rápido.
    * @param dados matriz de dados de entrada. Cada linha representa um exemplo de entrada.
    * @param saida matriz de dados de saída esperados. Cada linha representa o valor de saída correspondente ao exemplo de entrada.
    * @param epochs número de épocas de treinamento. Uma época é um ciclo completo de treinamento em que todos os exemplos de treinamento são apresentados para a rede.
    * @param embaralhar define se os índices dos dados serão embaralhados para aplicar o gradiente estocástico.
    */
   private void treino(double[][] entradas, double[][] saidas, int epochs, boolean embaralhar){
      double[] dadosEntrada = new double[entradas[0].length];//tamanho de colunas da entrada
      double[] dadosSaida = new double[saidas[0].length];//tamanho de colunas da saída
      
      int[] indices = new int[entradas.length];
      for(int i = 0; i < indices.length; i++) indices[i] = i;

      //transformar a rede numa lista de camdas pra facilitar minha vida
      ArrayList<Camada> redec = new ArrayList<>();
      redec.add(this.entrada);
      for(Camada camada : this.ocultas) redec.add(camada);
      redec.add(this.saida);

      int i, j, k;
      double erroMedio;//salvar no historico
      for(i = 0; i < epochs; i++){//quantidade de épocas
         if(embaralhar) embaralharDados(entradas, saidas);

         erroMedio = 0;
         for(j = 0; j < entradas.length; j++){//percorrer amostras
            //preencher dados de entrada e saída
            for(k = 0; k < entradas[0].length; k++){
               dadosEntrada[k] = entradas[j][k];
            }
            for(k = 0; k < saidas[0].length; k++){
               dadosSaida[k] = saidas[j][k];
            }

            calcularSaida(dadosEntrada);
            backpropagation(redec, dadosEntrada, dadosSaida);

            if(calcularHistoricoErro){
               erroMedio = 0;
               for(k = 0; k < this.saida.neuronios.length; k++){
                  erroMedio += this.saida.neuronios[k].erro;
               }
            }
            
            otimizadorAtual.atualizar(redec, this.TAXA_APRENDIZAGEM, this.TAXA_MOMENTUM);
         }

         if(calcularHistoricoErro){
            erroMedio /= (this.entrada.neuronios.length *this.saida.neuronios.length);
            historicoErro.add(erroMedio);
         }
         if(calcularHistoricoCusto){
            historicoCusto.add(funcaoDeCusto(entradas, saidas));
         }
      }
   }


   /**
    * Dedicado para treino em lote e multithread em implementações futuras.
    * @param dados conjunto de dados completo.
    * @param inicio índice de inicio do lote.
    * @param fim índice final do lote.
    * @return lote contendo os dados de acordo com os índices fornecidos.
    */
    @SuppressWarnings("unused")
   private double[][] obterSubMatriz(double[][] dados, int inicio, int fim){
      if(inicio < 0 || fim > dados.length || inicio >= fim){
         throw new IllegalArgumentException("Índices de início ou fim inválidos.");
      }

      int linhas = fim - inicio;
      int colunas = dados[0].length;
      double[][] subMatriz = new double[linhas][colunas];

      for(int i = 0; i < linhas; i++){
         for(int j = 0; j < colunas; j++){
            subMatriz[i][j] = dados[inicio + i][j];
         }
      }

      return subMatriz;
   }


   /**
    * Embaralha os dados da matriz usando o algoritmo Fisher-Yates.
    * @param entradas matriz com os dados de entrada.
    * @param saidas matriz com os dados de saída.
    */
   private void embaralharDados(double[][] entradas, double[][] saidas){
      int linhas = entradas.length;
  
      //evitar muitas inicializações
      double tempDados[];
      double tempSaidas[];
      int i, indiceAleatorio;

      for(i = linhas - 1; i > 0; i--){
         indiceAleatorio = random.nextInt(i + 1);
  
         tempDados = entradas[i];
         entradas[i] = entradas[indiceAleatorio];
         entradas[indiceAleatorio] = tempDados;

         tempSaidas = saidas[i];
         saidas[i] = saidas[indiceAleatorio];
         saidas[indiceAleatorio] = tempSaidas;
      }
   }


   /**
    * Retropropaga o erro da rede neural de acordo com os dados de entrada e saída esperados, 
    * @param entrada array com os dados de entrada das amostras.
    * @param saida array com as saídas esperadas das amostras.
    * @throws IllegalArgumentException se o modelo não foi compilado previamente.
    * @throws IllegalArgumentException se o tamanho dos dados de entrada for diferente do tamanho dos neurônios de entrada, excluindo o bias.
    * @throws IllegalArgumentException se o tamanho dos dados de saída for diferente do tamanho dos neurônios de saída da rede.
    */
   private void backpropagation(ArrayList<Camada> redec, double[] entrada, double[] saida){
      modeloValido();

      if(entrada.length != (this.entrada.neuronios.length-BIAS)){
         throw new IllegalArgumentException("O tamanho dos dados de entrada não corresponde ao tamanho dos neurônios de entrada da rede, com exceção dos bias");
      }
      if(saida.length != this.saida.neuronios.length){
         throw new IllegalArgumentException("O tamanho dos dados de saída não corresponde ao tamanho dos neurônios de saída da rede");
      }

      //erro da saída
      for(int i = 0; i < this.saida.neuronios.length; i++){
         Neuronio neuronio = this.saida.neuronios[i];
         neuronio.erro = ((saida[i] - neuronio.saida) * this.saida.funcaoAtivacaoDx(neuronio.somatorio));
      }

      double somaErros = 0.0;
      //começar da ultima oculta
      for(int i = redec.size()-2; i >= 1; i--){// percorrer camadas ocultas de trás pra frente
         
         Camada camadaAtual = redec.get(i);
         int qNeuronioAtual = camadaAtual.neuronios.length;
         if(redec.get(i).temBias) qNeuronioAtual -= 1;
         for (int j = 0; j < qNeuronioAtual; j++){//percorrer neurônios da camada atual
         
            Neuronio neuronio = camadaAtual.neuronios[j];
            somaErros = 0.0;
            for(Neuronio neuronioProximo : redec.get(i+1).neuronios){//percorrer neurônios da camada seguinte
               somaErros += neuronioProximo.pesos[j] * neuronioProximo.erro;
            }
            neuronio.erro = somaErros * camadaAtual.funcaoAtivacaoDx(neuronio.somatorio);
         }
      }
   }


   /**
    * Método alternativo no treino da rede neural usando diferenciação finita (finite difference), que calcula a "derivada" da função de custo levando
    * a rede ao mínimo local dela. É importante encontrar um bom balanço entre a taxa de aprendizagem da rede e o valor de perturbação usado.
    * <p>
    *    Vale ressaltar que esse método é mais lento e menos eficiente que o backpropagation, em arquiteturas de rede maiores e que tenha uma grande 
    *    volume de dados de treino ou para problemas mais complexos ele pode demorar muito para convergir ou simplemente não funcionar como esperado.
    * </p>
    * <p>
    *    Ainda sim pode ser uma abordagem válida.
    * </p>
    * @param treinoEntrada matriz com os dados de entrada 
    * @param treinoSaida matriz com os dados de saída
    * @param eps valor de perturbação
    * @param epochs número de épocas do treinamento
    * @param custoMinimo valor de custo desejável, o treino será finalizado caso o valor de custo mínimo seja atingido. Caso o custo mínimo seja zero, o treino
    * irá continuar até o final das épocas fornecidas
    * @throws IllegalArgumentException se o modelo não foi compilado previamente.
    * @throws IllegalArgumentException se a quantidade de linhas dos dados fornecidos for diferente da quantidade de linhas das saídas fornecidas.
    * @throws IllegalArgumentException se o tamanho dos dados de entrada do treino for diferente da quantidade de neurônios de entrada da rede, excluindo o bias.
    * @throws IllegalArgumentException se o tamanho dos dados de saída do treino for diferente da quantidade de neurônios da saída da rede.
    * @throws IllegalArgumentException se o valor de perturbação for igual a zero.
    * @throws IllegalArgumentException se o valor de épocas for menor que um.
    * @throws IllegalArgumentException se o valor de custo mínimo for menor que zero.
    */
   public void diferencaFinita(double[][] treinoEntrada, double[][] treinoSaida, double eps, int epochs, double custoMinimo){
      modeloValido();

      if(treinoEntrada.length != treinoSaida.length){
         throw new IllegalArgumentException("A quantidade de linhas de dados e saídas são diferentes");
      }
      if(treinoEntrada[0].length != (this.entrada.neuronios.length-BIAS)){
         throw new IllegalArgumentException("Incompatibilidade entre os dados de entrada e os neurônios de entrada da rede.");
      }
      if(treinoSaida[0].length != this.saida.neuronios.length){
         throw new IllegalArgumentException("Incompatibilidade entre os dados de saída e os neurônios de saída da rede");
      }
      if(eps == 0){
         throw new IllegalArgumentException("O valor de perturbação não pode ser igual a zero.");
      }
      if(epochs < 1){
         throw new IllegalArgumentException("O valor de epochs não pode ser menor que um.");
      }
      if(custoMinimo < 0){
         throw new IllegalArgumentException("O valor de custo mínimo não pode ser negativo.");
      }

      RedeNeural redeG = this.clone();//copia da rede para guardar os valores de gradiente
      
      ArrayList<Camada> camadasRede = new ArrayList<Camada>();//copia da rede para camadas
      ArrayList<Camada> camadasGradiente = new ArrayList<Camada>();//copia da rede gradiente para camadas
      
      //colocando a rede de forma sequencial
      camadasRede.add(this.entrada);
      for(Camada camada : this.ocultas) camadasRede.add(camada);
      camadasRede.add(this.saida);
      
      //colocando a rede gradiente de forma sequencial
      camadasGradiente.add(redeG.entrada);
      for(Camada camada : redeG.ocultas) camadasGradiente.add(camada);
      camadasGradiente.add(redeG.saida);

      for(int epocas = 0; epocas < epochs; epocas++){
         double custo = this.funcaoDeCusto(treinoEntrada, treinoSaida);
         if(custo < custoMinimo) break;

         double valorAnterior = 0;

         for(int i = 0; i < camadasRede.size(); i++){//percorrer camadas da rede
            for(int j = 0; j < camadasRede.get(i).neuronios.length; j++){//percorrer neuronios da camada
               for(int k = 0; k < camadasRede.get(i).neuronios[j].pesos.length; k++){//percorrer pesos do neuronio
                  valorAnterior = camadasRede.get(i).neuronios[j].pesos[k];
                  camadasRede.get(i).neuronios[j].pesos[k] += eps;
                  camadasGradiente.get(i).neuronios[j].pesos[k] = ((funcaoDeCusto(treinoEntrada, treinoSaida) - custo)/eps);//derivada da função de custo
                  camadasRede.get(i).neuronios[j].pesos[k] = valorAnterior;
               }
            }
         }

         //atualizar pesos
         for(int i = 0; i < camadasRede.size(); i++){
            for(int j = 0; j < camadasRede.get(i).neuronios.length; j++){
               for(int k = 0; k < camadasRede.get(i).neuronios[j].pesos.length; k++){
                  camadasRede.get(i).neuronios[j].pesos[k] -= TAXA_APRENDIZAGEM * camadasGradiente.get(i).neuronios[j].pesos[k];
               }
            }
         }
      }

   }


   /**
    * Informa o valor do hiperparâmetro de traxa de aprendizagem da Rede Neural.
    * @return valor de taxa de aprendizagem da rede.
    */
   public double obterTaxaAprendizagem(){
      return this.TAXA_APRENDIZAGEM;
   }


   /**
    * Informa o valor do hiperparâmetro de traxa de momentum da Rede Neural.
    * @return valor de taxa de momentum da rede.
    */
   public double obterTaxaMomentum(){
      return this.TAXA_MOMENTUM;
   }


   /**
    * Informa qual tipo de otmizador está sendo usado para o treio da Rede Neural.
    * @return otimizador atual da rede.
    */
   public Otimizador obterOtimizador(){
      return this.otimizadorAtual;
   }


   /**
    * Retorna a camada de entrada da rede.
    * @return camada de entrada.
    * @throws IllegalArgumentException se o modelo não foi compilado previamente.
    */
   public Camada obterCamadaEntrada(){
      modeloValido();
      return this.entrada;
   }


   /**
    * @return quantiade de camadas ocultas da rede.
    * @throws IllegalArgumentException se o modelo não foi compilado previamente.
    */
   public int obterQuantidadeOcultas(){
      modeloValido();
      return this.ocultas.length;
   }


   /**
    * Retorna a camada oculta correspondente ao índice fornecido.
    * @param indice índice da busca.
    * @return camada oculta baseada na busca.
    * @throws IllegalArgumentException se o modelo não foi compilado previamente.
    * @throws IllegalArgumentException se o índice estiver fora do alcance do tamanho das camadas ocultas.
    */
   public Camada obterCamadaOculta(int indice){
      modeloValido();
      if((indice < 0) || (indice > this.ocultas.length-1)){
         throw new IllegalArgumentException("O índice fornecido está fora do alcance das camadas disponíveis");
      }
   
      return this.ocultas[indice];
   }


   /**
    * Retorna a camada de saída da rede.
    * @return camada de saída.
    * @throws IllegalArgumentException se o modelo não foi compilado previamente.
    */
   public Camada obterCamadaSaida(){
      modeloValido();
      return this.saida;
   }


   /**
    * Copia os dados de saída de cada neurônio da camada de saída da rede neural para um vetor.
    * A ordem de cópia é crescente, do primeiro neurônio da saída ao último.
    * @return vetor com os dados das saídas da rede.
    * @throws IllegalArgumentException se o modelo não foi compilado previamente.
    */
   public double[] obterSaidas(){
      modeloValido();

      double saida[] = new double[this.saida.neuronios.length];
      for(int i = 0; i < this.saida.neuronios.length; i++){
         saida[i] = this.saida.neuronios[i].saida;
      }

      return saida;
   }


   /**
    * Cria um array que representa a estrutura da Rede Neural. Nele cada elemento indica uma camada 
    * da rede e cada valor contido nesse elementos indica a quantidade de neurônios daquela camada
    * correspondente.
    * <p>
    *    Os valores podem sofrer alteração caso a rede possua o bias adicionado na hora da compilação, 
    *    incluindo um neurônio a mais em cada elementos do array.
    * </p>
    * @return array com a arquitetura da rede.
    * @throws IllegalArgumentException se o modelo não foi compilado previamente.
    */
   public int[] obterArquitetura(){
      modeloValido();
      return this.arquitetura;
   }


   /**
    * Exibe as informações importantes da rede neural como:
    * <ul>
    *    <li>Otimizador atual.</li>
    *    <li>Valor da taxa de aprendizagem.</li>
    *    <li>Valor da taxa de momentum.</li>
    *    <li>Contém bias como neurônio adicional.</li>
    *    <li>Função de ativação de todas as camadas ocultas.</li>
    *    <li>Função de ativação da camada de saída.</li>
    *    <li>Arquitetura da rede.</li>
    * </ul>
    * @return buffer formatado contendo as informações.
    * @throws IllegalArgumentException se o modelo não foi compilado previamente.
    */
   public String obterInformacoes(){
      modeloValido();

      String buffer = "";
      String espacamento = "    ";
      System.out.println("\nInformações " + this.getClass().getSimpleName() + " = [");

      //otimizador
      buffer += espacamento + "Otimizador: " + this.otimizadorAtual.getClass().getSimpleName();
      if(nesterov) buffer += " (Acelerador de Nesterov)";
      buffer += "\n";

      //hiperparâmetros
      buffer += espacamento + "Taxa de aprendizgem: " + TAXA_APRENDIZAGEM + "\n";
      buffer += espacamento + "Taxa de momentum: " + TAXA_MOMENTUM + "\n";

      //bias
      if(this.BIAS == 1) buffer += espacamento + "Bias = " + "true\n\n";
      else buffer += espacamento + "Bias: " + "false\n\n";

      for(int i = 0; i < this.ocultas.length; i++){
         buffer += espacamento + "Ativação oculta " + i + " = " + this.ocultas[i].obterAtivacao() + "\n";
      }
      buffer += espacamento + "Ativação saída = " + this.saida.obterAtivacao() + "\n";

      //arquitetura
      buffer += "\n" + espacamento + "arquitetura = {" + this.arquitetura[0];
      for(int i = 0; i < this.ocultas.length; i++) buffer += ", " + this.arquitetura[i+1];
      buffer += ", " + this.arquitetura[this.arquitetura.length-1] + "}";

      buffer += "\n]\n";

      return buffer;
   }


   /**
    * @return lista contendo o histórico de erros durante o treinamento da rede.
    * @throws IllegalArgumentException se não foi habilitado previamente o cálculo do histórico de erros.
    */
   public ArrayList<Double> obterHistoricoErro(){
      if(!calcularHistoricoErro){
         throw new IllegalArgumentException("Deve ser habilitado o cálculo do histórico de erros para obter os resultados.");
      }
      return this.historicoErro;
   }


   /**
    * @return lista contendo o histórico de custos durante o treinamento da rede.
    * @throws IllegalArgumentException se não foi habilitado previamente o cálculo do histórico de custos.
    */
   public ArrayList<Double> obterHistoricoCusto(){
      if(!calcularHistoricoCusto){
         throw new IllegalArgumentException("Deve ser habilitado o cálculo do histórico de custos para obter os resultados.");
      }
      return this.historicoCusto;
   }

   
   /**
    * Clona a instância da rede.
    * @throws IllegalArgumentException se o modelo não foi compilado previamente.
    * @return Clone da rede
    */
   @Override
   public RedeNeural clone(){
      modeloValido();

      try{
         RedeNeural clone = (RedeNeural) super.clone();

         //dados importantes
         clone.TAXA_APRENDIZAGEM = this.TAXA_APRENDIZAGEM;
         clone.TAXA_MOMENTUM = this.TAXA_MOMENTUM;
         clone.BIAS = this.BIAS;
         clone.arquitetura = this.arquitetura;

         //entrada
         clone.entrada = cloneCamada(this.entrada);

         //ocultas
         int quantidadeOcultas = this.arquitetura.length-2;
         clone.ocultas = new Camada[quantidadeOcultas];
         for(int i = 0; i < quantidadeOcultas; i++){
            clone.ocultas[i] = cloneCamada(this.ocultas[i]);
         }

         //saída
         clone.saida = cloneCamada(this.saida);

         return clone;
      }catch(CloneNotSupportedException e){
         throw new RuntimeException(e);
      }
   }


   /**
    * Clona uma instância de camada da rede neural.
    * @param camada camada original
    * @return clone da camada fornecida.
    */
   private Camada cloneCamada(Camada camada){
      Camada clone = new Camada(camada.temBias);
      clone.neuronios = new Neuronio[camada.neuronios.length];
      clone.ativacao = camada.ativacao;
      clone.temBias = camada.temBias;

      for (int i = 0; i < camada.neuronios.length; i++) {
         clone.neuronios[i] = cloneNeuronio(camada.neuronios[i], camada.neuronios[i].pesos.length, camada.neuronios[i].pesos);
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
      clone.momentum = neuronio.momentum;
      clone.acumuladorGradiente = neuronio.acumuladorGradiente;
      clone.acumuladorSegundaOrdem = neuronio.acumuladorSegundaOrdem;

      return clone;
   }


   /**
    * Salva a classe da rede em um arquivo especificado, o caminho não leva em consideração
    * o formato, de preferência deve ser .dat, caso seja especificado apenas o nome, o arquivo
    * será salvo no mesmo diretório que o arquivo principal.
    * @param caminho caminho de destino do arquivo que será salvo.
    */
   public void salvarArquivoRede(String caminho){
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
   public static RedeNeural lerArquivoRede(String caminho){
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


   public String toString(){
      modeloValido();

      String buffer = "";
      String espacamento = "   ";
      String espacamentoDuplo = espacamento + espacamento;
      String espacamentoTriplo = espacamento + espacamento + espacamento;
      
      buffer += "\nArquitetura " + this.getClass().getSimpleName() + " = [\n";

      //ocultas
      for(int i = 0; i < this.ocultas.length; i++){
         buffer += espacamento + "Oculta " + i + " = [\n";
         for(int j = 0; j < this.ocultas[i].neuronios.length-BIAS; j++){
            
            buffer += espacamento + espacamento + "n" + j + " = [\n";
            
            for(int k = 0; k < this.ocultas[i].neuronios[j].pesos.length; k++){
               if(k == this.ocultas[i].neuronios[j].pesos.length-1 && (this.BIAS == 1)){
                  buffer += espacamentoTriplo + "pb" + " = " + this.ocultas[i].neuronios[j].pesos[k] + "\n";
               
               }else{
                  buffer += espacamentoTriplo + "p" + k + " = " + this.ocultas[i].neuronios[j].pesos[k] + "\n";
               }
            }
            buffer += espacamentoDuplo + "]\n";
         }
         buffer += espacamento + "]\n\n";
      }

      //saida
      buffer += espacamento + "Saída = [\n";
      for(int i = 0; i < this.saida.neuronios.length; i++){
         for(int j = 0; j < this.saida.neuronios[i].pesos.length; j++){
            if(j == this.saida.neuronios[i].pesos.length-1 && (this.BIAS == 1)){
               buffer += espacamentoDuplo + "pb" + " = " + this.saida.neuronios[i].pesos[j] + "\n";
            
            }else buffer += espacamentoDuplo + "p" + j + " = " + this.saida.neuronios[i].pesos[j] + "\n";
         }
      }
      buffer += espacamento + "]\n";

      buffer += "]\n";

      return buffer;
   }
}
