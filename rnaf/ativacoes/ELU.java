package rnaf.ativacoes;

public class ELU extends FuncaoAtivacao{
   private float alfa = 0.01f;


   public ELU(float alfa){
      this.alfa = alfa;
   }


   public ELU(){
      
   }

   
   @Override
   public float ativar(float x){
      return (x > 0) ? x : (float) (alfa * (Math.exp(x) - 1));
   }


   @Override
   public float derivada(float x){
      return (x > 0) ? 1f : (float) (alfa * Math.exp(x));
   }  
}
