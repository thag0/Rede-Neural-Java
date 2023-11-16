package rna.avaliacao.perda;

public class EntropiaCruzadaBinaria extends Perda{
   double eps = 1e-7;

   private void verificarDimensoes(int tamPrevisto, int tamReal){
      if(tamPrevisto != tamReal){
         throw new IllegalArgumentException(
            "Dimensões de dados previstos (" + tamPrevisto + 
            ") diferente da dimensão dos dados reais (" + tamReal + 
            ")"
         );
      }
   }

   @Override
   public double calcular(double[] previsto, double[] real){
      verificarDimensoes(previsto.length, real.length);

      double ecb = 0;
      for(int i = 0; i < real.length; i++){
         ecb += (-(real[i] * Math.log(previsto[i] + eps)) + (1 - real[i]) * (Math.log(1 - previsto[i] + eps)));
      }

      return ecb;
   }

   @Override
   public double[] derivada(double[] previsto, double[] real){
      verificarDimensoes(previsto.length, real.length);

      //também não econtrei ainda uma boa resposta de como calcular isso
      double[] derivadas = new double[previsto.length];
      int n = previsto.length;

      for(int i = 0; i < n; i++){
         derivadas[i] = real[i] - previsto[i];
         // gradientes[i] = (1 / n) * (1 / previsto[i]) * (previsto[i] - real[i]);
      }
      return derivadas;
   }
}
