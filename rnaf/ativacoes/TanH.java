package rnaf.ativacoes;

public class TanH extends FuncaoAtivacao{

   @Override
   public float ativar(float x){
      return (float) Math.tanh(x);
   }


   @Override
   public float derivada(float x){
      float tanh = (float) Math.tanh(x);
      return (1 - (tanh * tanh));
   }
}
