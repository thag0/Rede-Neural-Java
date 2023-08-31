package rnaf.ativacoes;

public class GELU extends FuncaoAtivacao{

   @Override
   public float ativar(float x){
      return (float) (0.5 * x * (1.0 + Math.tanh(Math.sqrt(2.0 / Math.PI) * (x + 0.044715 * Math.pow(x, 3)))));   
   }


   @Override
   public float derivada(float x){
      float cdf = (float) (0.5 * (1.0 + Math.tanh(Math.sqrt(2.0 / Math.PI) * (x + 0.044715 * Math.pow(x, 3)))));
      return (float) (0.5 * (1.0 + cdf + x * Math.exp(-Math.pow(x, 2) / 2.0) / Math.sqrt(2.0 * Math.PI)));
   }
}
