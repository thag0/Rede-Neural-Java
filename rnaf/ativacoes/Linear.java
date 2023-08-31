package rnaf.ativacoes;

public class Linear extends FuncaoAtivacao{

   @Override
   public float ativar(float x){
      return x;
   }


   @Override
   public float derivada(float x){
      return 1f;
   }
}
