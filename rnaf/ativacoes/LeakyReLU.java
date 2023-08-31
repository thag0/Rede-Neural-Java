package rnaf.ativacoes;

public class LeakyReLU extends FuncaoAtivacao{
   private float alfa = 0.01f;


   public LeakyReLU(){

   }


   public LeakyReLU(float alfa){
      this.alfa = alfa;
   }


   @Override
   public float ativar(float x){
      return (x > 0) ? x : (alfa * x);
   }


   @Override
   public float derivada(float x){
      return (x > 0) ? 1f : (alfa);
   }   
}
