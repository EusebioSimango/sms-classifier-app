# Protótipo actual (Fases 1–4 e extensões)

## Objectivo

Permitir, num dispositivo Android de teste, pedir permissões de SMS, ler mensagens já existentes na caixa de entrada, receber SMS novos e apresentá-los agrupados por remetente, **sem envio para a rede**. O identificador da aplicação é `mz.feng.smsclassifier`.

## Arquitectura

Adopta-se MVVM simples, como previsto no plano, sem *framework* de injecção de dependências (Hilt). A interface `SmsRepository` é implementada por Room + SQLCipher; os ecrãs não dependem da persistência.

```
SmsReceiver → SmsParser → SmsRepository.addIncoming → Room/SQLCipher
SmsReceiver → WorkManager (sem PII) → InboxSync
InboxViewModel → InboxSync / SmsRepository.messages
Compose (InboxRoute) → PermissionGateScreen | MessagesHomeScreen | CategoriesScreen
                       | CategoryDetailScreen | SearchScreen | SettingsScreen | SenderThreadScreen
```

| Peça | Responsabilidade |
| --- | --- |
| `SmsParser` | Transforma o `Intent` `SMS_RECEIVED` num `IncomingSms`. |
| `InboxSmsReader` | Lê a caixa de entrada do sistema (`Telephony.Sms.Inbox`), no máximo 100 mensagens. |
| `RoomSmsRepository` | Persiste e observa SMS na base cifrada. |
| `InboxSync` | Importa a caixa do sistema para o repositório. |
| `SmsRepository` | Contrato: observar, acrescentar um SMS, *upsert* em lote. |
| `InboxViewModel` | Permissão, carregamento, remetente seleccionado; deriva conversas com `groupBySender`. |
| `InboxRoute` | Encadeia os três ecrãs. |

O `SmsReceiver` está exportado no manifesto e protegido com `android.permission.BROADCAST_SMS`, de modo a só o sistema poder enviar o *broadcast*. Não é necessário ser a aplicação de SMS predefinida (`RoleManager`) para receber `SMS_RECEIVED`.

## Pré-processamento de texto (Fase 3)

O pacote `mz.feng.smsclassifier.nlp` não depende do Android, o que permite testá-lo na JVM e reproduzi-lo em Python.

```
texto → TextNormalizer → Tokenizer → filtro de stopwords → lista de tokens
```

| Passo | O que faz | Exemplo |
| --- | --- | --- |
| `TextNormalizer` | Minúsculas, sem acentos; URL, valores (`MT`, `MZN`), telefones MZ e números passam a marcadores; junta marcas com hífen. | `Recebeu 1.500,00MT` → `recebeu __money__` |
| `Tokenizer` | Divide por espaço e descarta *tokens* de um carácter. | `pacote __num__ gb x` → `pacote`, `__num__`, `gb` |
| `Stopwords` | Remove palavras funcionais lidas de `stopwords_pt_mz.txt`; mantém sinais de fraude como `nao` e `urgente`. | `nao partilhe o seu pin` → `nao`, `partilhe`, `pin` |

Os marcadores reduzem o vocabulário (cada valor ou número diferente deixaria de ser uma palavra nova) e evitam que dados pessoais, como números de telefone, entrem no modelo. O treino da Fase 4 tem de aplicar exactamente as mesmas regras. Os pares de entrada e saída em `app/src/test/resources/nlp/preprocessing_cases.tsv` servem de teste de paridade entre Kotlin e Python.

## Classificação local (Fase 4)

O modelo foi treinado em Python (scikit-learn) com um *dataset* académico, separado das mensagens do telemóvel, e exportado para ONNX (`app/src/main/assets/models/modelo_sms_mocambique.onnx`, 380 KB). O ficheiro `.pkl` é um objecto Python serializado e não pode ser lido em Kotlin; serve apenas para retreinar e reexportar.

| Aspecto | Valor |
| --- | --- |
| Pipeline | `TfidfVectorizer` (1 e 2 gramas, 6 048 termos) + `LinearSVC` |
| Entrada | Texto em bruto do SMS, forma `[N, 1]` |
| Classes | Bancário, Carteira móvel, Jogos e apostas, Mensagens suspeitas, Operadora, Outros, Pessoal, Publicidade |
| Versão | `sms-mz-2026.10.10`, gravada em cada SMS |
| Motor | ONNX Runtime para Android 1.31 (pacote completo, por causa da operação `com.microsoft.Tokenizer`) |

```
SmsReceiver / InboxSync → Room (SMS gravado, sem categoria)
PersistSmsWorker / InboxViewModel → ClassifyPendingSms → OnnxSmsClassifier
                                                     ↘ heurística, se o modelo falhar
ClassifyPendingSms → Room (categoria, confiança, versão do modelo)
```

- A classificação corre num passo separado, depois de o SMS estar gravado. Uma falha do modelo nunca impede guardar a mensagem.
- São reclassificados os SMS sem versão ou com uma versão diferente da actual. Assim, ao trocar o modelo, as mensagens antigas são reclassificadas automaticamente, e as que ficaram com a heurística voltam a ser tentadas.
- A **confiança** é o *softmax* das margens do `LinearSVC`. Indica a força relativa da escolha entre as 8 classes, mas não é uma probabilidade calibrada.
- A base passou para a versão 2 com uma migração que só acrescenta a coluna `modelVersion`, sem perder os SMS já guardados.

O pré-processamento da Fase 3 **não** é aplicado antes da inferência: o grafo ONNX já inclui o seu próprio TF-IDF, treinado sobre o texto em bruto. Dar-lhe texto transformado produziria termos que o modelo nunca viu. O pacote `nlp` fica preparado para um retreino com marcadores e *stopwords*.

## O que está fora do plano original e porquê

1. **Lista da caixa de entrada.** O entregável da Fase 1 era apenas o SMS *novo* no Logcat. A leitura com `READ_SMS` importa até 100 mensagens para a base local (Fase 2).
2. **Agrupamento por remetente.** A Fase 5 prevê agrupamento por **categoria** (resultado do classificador). O fio por remetente (por exemplo «Vodacom») é um extra de usabilidade. Remetentes com nomes distintos no Android (`Vodacom` versus `VM`) aparecem como conversas separadas.

## Privacidade e segurança já aplicadas

Alinhado com o processamento local (RNF do plano) e com o contexto académico:

- Sem permissão `INTERNET`.
- `android:allowBackup="false"`.
- `android:usesCleartextTraffic="false"`.
- `FLAG_SECURE` na `MainActivity` (o ecrã recente e as capturas do sistema não mostram o conteúdo das mensagens; ferramentas de captura do computador também podem falhar — comportamento esperado).
- O receptor **não** regista remetente, corpo nem hora no Logcat. Em *debug* pode aparecer apenas a linha `SMS received`, sem dados pessoais.

A base Room é cifrada com SQLCipher (RNF-07). A chave gera-se no dispositivo e guarda-se em `EncryptedSharedPreferences`. A lista em RAM, usada só para desenhar o ecrã, não é cifrada.

## Limitações conhecidas (úteis para o capítulo de discussão)

- O Google Play Protect bloqueia a instalação de APKs com `RECEIVE_SMS` / `READ_SMS` quando o ficheiro chega por WhatsApp, gestor de ficheiros ou browser. A instalação por USB (*Android Debug Bridge*) não sofre deste bloqueio. Ver [guia-laboratorio.md](guia-laboratorio.md).
- No Android 13 e posteriores, após instalar fora da loja, pode ser necessário **Permitir definições restritas** antes de conceder SMS.
- A aplicação não é a app de SMS predefinida e não escreve no fornecedor de SMS do sistema.
- Ainda não há correcção manual da categoria nem opção para apagar os dados guardados (Fase 6).
- **Acentos no modelo ONNX:** o exportador `skl2onnx` substituiu o tokenizador do scikit-learn por `[a-zA-Z0-9_]+`. Por isso, 328 dos 6 048 termos com acentos (por exemplo `não` e `está`) nunca são reconhecidos no telemóvel, e as palavras de uma só letra passam a interromper os bigramas. Num teste com 7 SMS, as previsões do `.onnx` e do `.pkl` coincidiram, mas a diferença deve ser medida no conjunto de teste.
- **Qualidade do modelo:** em testes rápidos, mensagens de publicidade e de apostas foram classificadas como `Pessoal`. A matriz de confusão do conjunto de teste deve entrar no capítulo de resultados.
- **Tamanho do APK:** o ONNX Runtime completo acrescenta cerca de 34 MB por arquitectura. O APK de *debug* inclui apenas `arm64-v8a` (cerca de 60 MB). Uma compilação reduzida do ONNX Runtime, só com as operações usadas pelo modelo, fica para a Fase 8 (RNF-04).

## Versões de construção

- `minSdk` 26, `compileSdk` / `targetSdk` 36
- Android Gradle Plugin 8.13, Kotlin 2.3, Compose BOM 2026.06.01
- ONNX Runtime para Android 1.31; ABI `arm64-v8a`
- Versão da app: `0.1.0` (`versionCode` 1)
