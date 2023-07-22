# Rede-Neural-Java

<p><strong>Modelo de rede neural artificial multilayer perceptron criado por mim.</strong></p>

![rede](https://github.com/thag0/Rede-Neural-Java/assets/91092364/f5338b8b-c708-45b5-b4fc-68cf499a7d97)

*Exemplo de estrutura da rede*

<p>
Minha ideia é criar uma biblioteca ou framework baseado nesse modelo apresentado, tentando torná-lo o mais flexível possível para todo problema que possa ser resolvido com uso das Redes Neurais Artificiais.
</p>

<p>
Ainda tenho ideias de implementações futuras para a melhora desse modelo, principalmente na hora de treinar usando o algoritmo backpropagation, formas mais elaboradas e variadas de funções de ativação, arquiteturas mais flexíveis e por aí vai.
</p>

<p>
No geral o modelo já pode ser usado em aplicações mas que não usem o método de treino por enquanto, uma alternativa que fiz foi implementar o treino usando uma técnica 
conhecida como Diferenças Finitas, além disso o algoritmo já conta com algumas opções de modificação de hiperparâmetros e funções de ativação, além de opções de 
salvamento e leitura para arquivos externos.
</p>

# Criando uma instância da rede
Para criar a rede, é preciso informar a estrutura que ela irá assumir, nesse modelo para criarmos uma rede precisamos informar a arquitetura dela passando um array contendo os elementos de cada camada. É obrigatório que exista pelo menos uma camada de entrada, uma camada oculta e uma camada de saída e pelo menos um neuronio em cada camada, como mostrado no exemplo a seguir:
```
int[] arquitetura = {1, 2, 3, 4};
RedeNeural rede = new RedeNeural(arquitetura);
```

# Configurações suportadas
Após instanciar a rede, podem ser usadas funções de configuração simples para mudar o comportamento do modelo, essas são as configurações disponíveis atualmente:
``` 
rede.configurarAlcancePesos(1.0);
rede.configurarBias(true);
rede.configurarTaxaAprendizagem(0.01);
```
<p>
A única excessão a essa regra é a configuração da função de ativação das camadas, nela é preciso que o modelo esteja compilado previamente. Existem duas formas de configurar as funções de ativação, em uma é definido apenas o valor da função que será usada e ela será aplicada em todas as camadas, em outra precisamos específicar a camada que queremos configurar a função de ativação, como mostrado no exemplo:
</p>

```
rede.configurarFuncaoAtivacao(2); //configurando a função de ativação de todas as camadas
rede.configurarFuncaoAtivacao(rede.obterCamadaSaida(), 2); //configurando a função de ativação de uma camada específica, por exemplo a saída
```

# Compilação

Depois de ter instanciado a rede num objeto, e ter feito ou não as configurações iniciais, o modelo precisa ser compilado da seguinte forma:
``` 
rede.compilar();
 ```

# Treino e uso
Com o modelo criado e compilado, podemos usá-lo para fazer predições com a função de calcular saída:
``` 
rede.calcularSaida(dados);
```
*É importante destacar que o modelo recebe um array/vetor com os dados para a entrada, e que esses dados devem ser do tipo double*

<p>
O modelo criado pode ser treinado usando uma técnica de diferenças finitas, ela não é nada eficiente se comparada com o backpropagation mas funciona bem em modelos simples. Nele é preciso informar algumas informações que são: entrada dos dados de treino, saída dos dados de treino (classes/classifiçaões), um valor de perturbação que deve ser pequeno, quantidade de épocas de treino e o custo mínimo desejado, respectivamente.
</p>

``` 
rede.diferencaFinita(dadosEntrada, dadosSaida, 0.001, 1000, 0.001);
```
*-Para o treino, tanto os dados de entrada e saída devem ser matrizes bidimesionais do tipo double*

Após ter calculado a saída, para obter a saída dos neurônios, pode ser usado o método que devolve o valor de saída de cada neuônio da última camada da rede:
```
int[] saidaRede = rede.obterSaida();
```

# Salvando e lendo arquivos
Uma opção que fiz foi serializar a rede num arquivo externo, consequentemente também fiz uma funcionalidade de leitura para arquivos externos, a leitura e salvamento preserva toda a arquitetura da rede e o mais importante, seus pesos.

Para salvar a rede num arquivo externo, é necessário especificar o caminho onde o arquivo será salvo, esse caminho deve incluir tanto o nome do arquivo quanto a extensão dele
```
String caminho = "./rede-salva.dat";
rede.salvarArquivoRede(caminho);
```

Para ler o arquivo salvo, é necessário informar o caminho onde o arquivo está localizado, o caminho deve incluir o nome e a extensão do arquivo.
```
String caminho = "./rede-salva.dat";
rede.lerArquivoRede(caminho);
```

Bom uso!
