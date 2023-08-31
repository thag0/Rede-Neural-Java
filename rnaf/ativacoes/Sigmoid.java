package rnaf.ativacoes;

public class Sigmoid extends FuncaoAtivacao{

   @Override
   public float ativar(float x){
      return (float) (1 / (1 + Math.exp(-x)) );
   }


   @Override
   public float derivada(float x){
      float sig = (float) (1 / (1 + Math.exp(-x)) );
      return (sig * (1-sig));
   }
}
