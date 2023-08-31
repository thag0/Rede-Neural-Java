package rnaf.ativacoes;

public class Swish extends FuncaoAtivacao{
   
   @Override
   public float ativar(float x){
      return (x * sigmoid(x));
   }


   @Override
   public float derivada(float x){
      float sig = sigmoid(x);
      return sig + (x * sig * (1 -sig));
   }


   private float sigmoid(float x){
      return (float) (1 / (1 + Math.exp(-x)));
   }
}
