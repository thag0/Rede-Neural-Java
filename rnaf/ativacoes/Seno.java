package rnaf.ativacoes;

public class Seno extends FuncaoAtivacao{

   @Override
   public float ativar(float x){
      return (float) Math.sin(x);
   }


   @Override
   public float derivada(float x){
      return (float) Math.cos(x);
   } 
}
