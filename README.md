# Rede-Neural-Java

<p><strong>Modelo de rede neural artificial multilayer perceptron criado por mim.</strong></p>

<p>A ideia é criar uma biblioteca ou framework baseado nesse modelo apresentado, tentando torná-lo o mais flexível 
possível para todo problema que possa ser resolvido com uso das Redes Neurais Artificiais.</p>

<p>Ainda tenho ideias de implementações futuras para a melhora desse modelo, principalmente na hora de treinar usando 
o algoritmo backpropagation, formas mais elaboradas de funções de ativação como a softmax e a argmax, retornar os dados de 
saída usando vetor e por aí vai.</p>

<p>No geral o modelo já pode ser usado em aplicações mas que não usem o método de treino incluido(porque não funciona bem 
  ainda e foi criado numa estrutura diferente da atual), principalmente em estruturas de evolução genética(que foi o uso que 
  fiz até o momento), já conta com algumas opções de modificação de hiperparâmetros e funções de ativação, além de opções de 
  salvamento e leitura para arquivos externos.</p>

# Exemplo de uso
Para criar a rede, é preciso informar a estrutura que ela irá assumir, nesse modelo para criarmos uma rede precisamos informar a quantidade de neurônios de cada camada e a quantidade de camadas ocultas, como por exemplo, uma rede com 1 neurônio para a camada de entrada, 2 neurônios para as camadas ocultas, 3 neurônios para a camada de saída e 4 camadas ocultas:
``` 
RedeNeural rede = new RedeNeural(1, 2, 3, 4);
```
Após instanciar a rede, podem ser usadas funções de configuração simples para mudar o comportamento do modelo, como por exemplo, definir as funções de ativação usadas:
``` 
rede.configurarFuncaoAtivacao(1, 2);
```
Além de definir o valor de geração dos pesos aleatórios na hora da compilação:
``` 
rede.configurarAlcancePesos(1.0);
```
Bem como o uso do bias como neurônio adicional em cada camada, com exceção da saída:
``` 
rede.configurarBias(true);
```
Por fim também configurar o valor da taxa de aprendizagem da rede, que será usado no algoritmo de treino:
``` 
rede.configurarTaxaAprendizagem(0.01);
```
Depois de ter instanciado a rede num objeto, e ter feito ou não as configurações iniciais, o modelo precisa ser compilado:
``` 
rede.compilar();
```
Agora que o modelo foi criado, pode ser usado para fazer as suas predições com a função de calcular saída:
``` 
rede.calcularSaída(dados);
```
*É importante destacar que o modelo recebe um array/vetor com os dados para a entrada, e que esses dados devem ser do tipo double*
Bom uso.
