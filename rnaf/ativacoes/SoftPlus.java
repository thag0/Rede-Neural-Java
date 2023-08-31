package rnaf.ativacoes;

public class SoftPlus extends FuncaoAtivacao{

   @Override
   public float ativar(float x){
      return (float) Math.log(1 + Math.exp(x));
   }


   @Override
   public float derivada(float x){
      return (float) (1 / (1 + Math.exp(-x)));
   }
}
