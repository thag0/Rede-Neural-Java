package rnaf.ativacoes;

public class ReLU extends FuncaoAtivacao{
   
   @Override
   public float ativar(float x){
      return (x > 0) ? x : 0f;
   }


   @Override
   public float derivada(float x){
      return (x > 0) ? 1f : 0f;
   }
}
