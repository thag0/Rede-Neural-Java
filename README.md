# Rede-Neural-Java

<p><strong>Modelo de rede neural artificial multilayer perceptron criado por mim.</strong></p>

![rede](https://github.com/thag0/Rede-Neural-Java/assets/91092364/edb670b9-a9a8-460b-bdab-e4f2da884937)

*Exemplo de estrutura da rede*

<p>
Minha ideia é criar uma biblioteca ou framework baseado nesse modelo apresentado, tentando torná-lo o mais flexível possível para todo problema que possa ser resolvido com uso das Redes Neurais Artificiais.
</p>

<p>
Ainda tenho ideias de implementações futuras para a melhora desse modelo, como formas mais elaboradas e variadas de funções de ativação, arquiteturas mais flexíveis, outros tipos de otimizadores e por aí vai. Talvez tentar encaixar a ideia de redes neurais convolucionais no futuro para abrir mais possibilidades ainda de uso.
</p>

<p>
O modelo já pode ser usado em aplicações e possui um método de treino que pode ser flexibilizado com uso de alguns otimizadores, uma alternativa que fiz foi implementar o treino usando uma técnica conhecida como Diferenças Finitas (foi a primeira técnica de treino que usei e decidi manter no código), além disso o algoritmo já conta com algumas opções de modificação de hiperparâmetros e funções de ativação, além de opções de salvamento e leitura para arquivos externos.
</p>

# Criando uma instância da rede
Para criar a rede, é preciso informar a estrutura que ela irá assumir, nesse modelo para criarmos uma rede precisamos informar a arquitetura dela passando um array contendo os elementos de cada camada. É obrigatório que exista pelo menos uma camada de entrada, uma camada oculta e uma camada de saída e pelo menos um neuronio em cada camada, como mostrado no exemplo a seguir:
```
int[] arquitetura = {1, 2, 3, 4};
RedeNeural rede = new RedeNeural(arquitetura);
```

# Configurações suportadas
Após instanciar a rede, podem ser usadas funções de configuração simples para mudar o comportamento do modelo, dentre elas temos:

- Configurar o alcance dos pesos iniciais da rede (que são aleatórios), o valor de alcance é espelhado para um valor negativo também, então se passarmos por exemplo 1.0, o valor aleatório gerado estará num intervalo entre -1.0 e 1.0;
- Configurar uso do bias como neurônio adicional, se por algum motivo precise que a rede não tenha bias na sua arquiterura, pode ser facilmente removido;
- Configurar o otimizador que vai ser usado na hora de treinar a rede, isso ainda to pesquisando e testando mais, o mais confiável que eu uso e sei melhor como funciona é o SGD, por isso ele já vem por padrão.
- Configurar um hitórico de perda (ou custo) da rede durante o treinamento, por enquanto só to usando erro médio quadrado e entropia cruzada nesse cáclulo mas a função é mais pra ter um feedback da evolução da rede.

Segue um exemplo com as configurações usando a biblioteca:
``` 
rede.configurarAlcancePesos(1.0);
rede.configurarBias(true);
rede.configurarOtimizador(new Adam());
rede.configurarHistoricoCusto(true);
```
<p>
Existem quatro formas de configurar as funções de ativação, na primeira é definido apenas o valor da função que será usada e ela será aplicada em todas as camadas, na segunda precisamos específicar a camada que queremos configurar a função de ativação, na terceira e quarta temos a mesma ideia com o adicional de fornecer instâncias de função de ativação, com isso funções que possuem parâmetros podem ser mais flexíveis para o uso.
</p>

```
//configurando a função de ativação de todas as camadas
rede.configurarFuncaoAtivacao(2);
rede.configurarFuncaoAtivacao(new LeakyReLU(0.1));

//configurando a função de ativação de uma camada específica, por exemplo a saída
rede.configurarFuncaoAtivacao(rede.obterCamadaSaida(), 2);
rede.configurarFuncaoAtivacao(rede.obterCamadaSaida(), new Softmax());
```

# Compilação

Depois de ter instanciado a rede num objeto, e ter feito ou não as configurações iniciais, o modelo precisa ser compilado da seguinte forma:
``` 
rede.compilar();
 ```
*Algumas configurações iniciais podem depender da compilação prévia do modelo.*

# Treino e uso
Com o modelo criado e compilado, podemos usá-lo para fazer predições com a função de calcular saída:
``` 
rede.calcularSaida(dados);
```
*É importante destacar que o modelo recebe um array/vetor com os dados para a entrada, e que esses dados devem ser do tipo double*

<p>
Para treinar a rede apenas precisamos informar os dados de entrada, saída e a quantidade de épocas. Cada otimizador possui seu modo de corrigir os pesos, e alguns deles possuem suporte para serem inicialziados com configurações de hiperparâmetros ajustáveis, permitindo mais opções de adaptação ao problema.
</p>
<p>
Exemplo de uso:
</p>

``` 
rede.treinar(dadosEntrada, dadosSaida, epocas);

```
*É importante separar os dados de treino e teste para evitar o overfitting da rede*

<p>
O modelo criado pode ser treinado também usando uma técnica de diferenças finitas, ela não é nada eficiente (tanto que preferi não incluir no métodos de treino) mas funciona bem em modelos simples e com conjuntos de dados menores. Nela é preciso informar algumas informações que são: entrada dos dados de treino, saída dos dados de treino (classes/classificações), um valor de perturbação que deve ser pequeno, quantidade de épocas de treino e o custo mínimo desejado, respectivamente.
</p>

``` 
rede.diferencaFinita(dadosEntrada, dadosSaida, 0.001, 1000, 0.001);
```
*Para o treino, tanto os dados de entrada e saída devem ser matrizes bidimesionais do tipo double*

Após ter calculado a saída, para obter a saída dos neurônios, pode ser usado o método que devolve o valor de saída de cada neuônio da última camada da rede:
```
double[] saida = rede.obterSaidas();
```

Também pode-se obter as saídas da rede fornecendo uma matriz com os dados de entrada e usando o método de cálcular saída fornecendo todas as entradas desejadas.

```
double[][] dadosEntrada = ...
double[][] = rede.calcularSaida(dadoEntrada);
```

# Salvando e lendo arquivos
Uma opção que fiz foi serializar a rede num arquivo externo, consequentemente também fiz uma funcionalidade de leitura para arquivos externos, a leitura e salvamento preserva toda a arquitetura da rede e o mais importante, seus pesos.

Para salvar a rede num arquivo externo, é necessário especificar o caminho onde o arquivo será salvo, esse caminho deve incluir tanto o nome do arquivo quanto a extensão dele. O arquivo deve ser salvo no formato .txt e é acessado por meio da classe <strong> Serializador </strong> disponível em <pre>rna.serializacao.Serializador</pre>

<p>
 Exemplo de salvamento.
</p>

```
String caminho = "./rede-salva.txt";
Serializador.salvar(rede, caminho);
```

Para ler o arquivo salvo, é necessário informar o caminho onde o arquivo está localizado, o caminho deve incluir o nome e a extensão do arquivo.
```
String caminho = "./rede-salva.dat";
RedeNeural rede = Serializador.ler(caminho);
```

Decidi não implementar mais usando a interface Serializable do java porque em alguns casos o arquivo de rede se tornava maior que o próprio dataset que ela foi treinada.

Bom uso!
