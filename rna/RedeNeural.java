package rna;


public class RedeNeural{
   public Camada entrada;
   public Camada[] ocultas;
   public Camada saida;
   
   public int qtdNeuroniosEntrada;
   public int qtdNeuroniosOcultas;
   public int qtdNeuroniosSaida;

   public int qtdCamadasOcultas;

   int BIAS = 1;
   double TAXA_APRENDIZAGEM = 0.1;


   public RedeNeural(int qtdNeuroniosEntrada, int qtdNeuroniosOcultas, int qtdNeuroniosSaida, int qtdCamadasOcultas){
      this.qtdNeuroniosEntrada = qtdNeuroniosEntrada;
      this.qtdNeuroniosOcultas = qtdNeuroniosOcultas;
      this.qtdNeuroniosSaida = qtdNeuroniosSaida;

      this.qtdCamadasOcultas = qtdCamadasOcultas;

      criarRede();
   }


   private void criarRede(){
      //inicializar camada de entrada
      entrada = new Camada();
      entrada.neuronios = new Neuronio[qtdNeuroniosEntrada];
      for(int i = 0; i < qtdNeuroniosEntrada; i++){
         entrada.neuronios[i] = new Neuronio(qtdNeuroniosOcultas);
      }

      //inicializar camadas ocultas
      ocultas = new Camada[qtdCamadasOcultas];
      for (int i = 0; i < qtdCamadasOcultas; i++) {
         Camada novaOculta = new Camada();
         novaOculta.neuronios = new Neuronio[qtdNeuroniosOcultas];
      
         for (int j = 0; j < qtdNeuroniosOcultas; j++) {
            if (i == (qtdCamadasOcultas-1)){
               novaOculta.neuronios[j] = new Neuronio(qtdNeuroniosSaida);
            
            }else{
               novaOculta.neuronios[j] = new Neuronio(qtdNeuroniosOcultas);
            }
         }
         ocultas[i] = novaOculta;
      }

      //inicializar camada de saída
      saida = new Camada();
      saida.neuronios = new Neuronio[qtdNeuroniosSaida];
      for(int i = 0; i < qtdNeuroniosSaida; i++){
         saida.neuronios[i] = new Neuronio(qtdNeuroniosSaida);
      }

   }


   public void calcularSaida(RedeNeural rede, double[] dados){
      //entrada
      for(int i = 0; i < rede.qtdNeuroniosEntrada; i++){
         rede.entrada.neuronios[i].saida = dados[i];
      }

      //ocultas
      for(int camadaOculta = 0; camadaOculta < rede.qtdCamadasOcultas; camadaOculta++){//percorrer camadas ocultas

         Camada camadaAtual = rede.ocultas[camadaOculta];
         Camada camadaAnterior;
         if(camadaOculta == 0) camadaAnterior = rede.entrada;
         else camadaAnterior = rede.ocultas[camadaOculta-1];

         for(int neuronio = 0; neuronio < camadaAtual.neuronios.length; neuronio++){
            double soma = 0.0;
            for(int i = 0; i < camadaAnterior.neuronios.length; i++){
               soma += camadaAnterior.neuronios[i].saida * camadaAnterior.neuronios[i].pesos[neuronio];
            }
            soma += BIAS;
            camadaAtual.neuronios[neuronio].saida = sigmoid(soma);
         }
      }

      //saída
      for(int neuronioSaida = 0; neuronioSaida < rede.saida.neuronios.length; neuronioSaida++){
         Camada oculta = rede.ocultas[rede.qtdCamadasOcultas-1];
         double soma = 0.0;

         for(int neuronioOculta = 0; neuronioOculta < oculta.neuronios.length; neuronioOculta++){
            soma += oculta.neuronios[neuronioOculta].saida * oculta.neuronios[neuronioOculta].pesos[neuronioSaida]; 
         }
         soma += BIAS;
         rede.saida.neuronios[neuronioSaida].saida = reluDx(soma);
      }
   }

   
   public void treinar(RedeNeural rede, double[] entrada, double classe) {
      // Calcular a saída da rede
      calcularSaida(rede, entrada);
   
      // Atualizar os pesos da camada de saída
      for (int neuronioSaida = 0; neuronioSaida < rede.saida.neuronios.length; neuronioSaida++) {
         double erro = classe - rede.saida.neuronios[neuronioSaida].saida;
   
         for (int neuronioOculta = 0; neuronioOculta < rede.ocultas[rede.qtdCamadasOcultas-1].neuronios.length; neuronioOculta++) {
            double ajuste = rede.ocultas[rede.qtdCamadasOcultas-1].neuronios[neuronioOculta].saida * erro;
            rede.ocultas[rede.qtdCamadasOcultas-1].neuronios[neuronioOculta].pesos[neuronioSaida] += TAXA_APRENDIZAGEM * ajuste;
         }
      }
   
      // Atualizar os pesos das camadas ocultas
      for (int camadaOculta = rede.qtdCamadasOcultas - 1; camadaOculta >= 0; camadaOculta--) {
         Camada camadaAtual = rede.ocultas[camadaOculta];
         Camada camadaPosterior;
         if (camadaOculta == rede.qtdCamadasOcultas - 1) {
            camadaPosterior = rede.saida;
         } else {
            camadaPosterior = rede.ocultas[camadaOculta + 1];
         }
   
         for (int neuronioAtual = 0; neuronioAtual < camadaAtual.neuronios.length; neuronioAtual++) {
            double somaErro = 0.0;
            for (int neuronioPosterior = 0; neuronioPosterior < camadaPosterior.neuronios.length; neuronioPosterior++) {
               somaErro += camadaPosterior.neuronios[neuronioPosterior].pesos[neuronioAtual] *
                           (classe - camadaPosterior.neuronios[neuronioPosterior].saida);
            }
   
            double delta = somaErro;
   
            for (int i = 0; i < camadaAtual.neuronios[neuronioAtual].pesos.length; i++) {
               double ajuste = entrada[i] * delta;
               camadaAtual.neuronios[neuronioAtual].pesos[i] += TAXA_APRENDIZAGEM * ajuste;
            }
         }
      }
   }   


   //funções de ativação
   public double relu(double valor){
      if(valor < 0) return 0;
      return valor;
   }


   public double reluDx(double valor){
      if(valor < 0) return 0;
      return 1;     
   }


   public double sigmoid(double valor){
      return 1 / (1 + Math.exp(-valor));
   }
}
