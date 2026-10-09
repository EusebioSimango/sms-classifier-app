# Plano de desenvolvimento e estado do protótipo

O plano de trabalho foi definido para um cronograma curto, com ordem deliberada: primeiro a estrutura Android e a captura de SMS, depois o armazenamento, só então o modelo de classificação, e por último a interface elaborada por categorias de PLN. O maior risco identificado não é o processamento de linguagem, e sim aprender Android ao mesmo tempo que se lidam com permissões sensíveis e requisitos de privacidade.

## Stack prevista (documento original)

- Kotlin, Jetpack Compose, MVVM (ViewModel + StateFlow)
- Room + SQLCipher (Fase 2)
- DataStore / preferências encriptadas (Fase 6)
- BroadcastReceiver (`SMS_RECEIVED`) + WorkManager (Fase 2)
- Pré-processamento em Kotlin; treino em Python (scikit-learn); inferência leve na app (Fase 4)
- Testes JUnit e Espresso (Fase 7)

## Cronograma previsto

Início a 28 de setembro de 2026 e conclusão prevista a 20 de novembro de 2026 (cerca de 7,7 semanas contínuas). As datas abaixo são as do plano original; o estado reflecte o repositório à data desta documentação.

## Fases, requisitos e estado

### Fase 0 — Ambiente e fundamentos (28/09 a 04/10/2026)

Projecto Kotlin + Compose, Gradle, execução em emulador ou dispositivo.

**Estado:** concluída no essencial. O repositório Gradle existe; a compilação faz-se com `./build.sh`.

### Fase 1 — Permissões e captura de SMS (05/10 a 08/10/2026) · RF-01, RF-02

Permissões `RECEIVE_SMS` e `READ_SMS` em tempo de execução; `BroadcastReceiver` para `SMS_RECEIVED`; extracção de remetente, corpo e data/hora.

**Estado:** concluída. O entregável original (registo no Logcat com o conteúdo da mensagem) foi **alterado por privacidade**: o receptor já não escreve remetente nem corpo no Logcat (ver [prototipo-fase-1.md](prototipo-fase-1.md)).

**Extensões fora do plano original (já no protótipo):**

- Leitura da caixa de entrada do sistema (até 100 SMS), para demonstração sem esperar por uma mensagem nova.
- Agrupamento por remetente (lista de conversas e fio ao tocar), padrão de aplicação de mensagens. O plano original reserva a lista elaborada para a **Fase 5**, mas agrupada por **categoria de PLN**, não por remetente.

### Fase 2 — Armazenamento local seguro (09/10 a 11/10/2026) · RF-06, RNF-01, RNF-06, RNF-07

Entidade Room, SQLCipher, WorkManager quando chega um SMS.

**Estado:** pendente. A persistência actual é só em memória de processo (`InMemorySmsRepository`).

### Fase 3 — Pré-processamento de texto (12/10 a 15/10/2026) · RF-03, RNF-05

Normalização, tokenização, *stopwords* em português (contexto moçambicano).

**Estado:** pendente.

### Fase 4 — Modelo de classificação local (16/10 a 25/10/2026) · RF-04, RF-05, RF-14, RNF-02, RNF-03, RNF-04, RNF-09, RNF-10

Dataset académico separado das mensagens pessoais; treino em scikit-learn; inferência na app; versão do modelo; falha do modelo não impede guardar o SMS.

**Estado:** pendente.

### Fase 5 — Interface por categorias, lista, pesquisa e detalhes (26/10 a 01/11/2026) · RF-07 a RF-10

Ecrã de categorias com contagens; lista por categoria; detalhe; pesquisa por texto ou remetente.

**Estado:** pendente. Existe já uma lista por **remetente**, que não substitui este entregável.

### Fase 6 — Reclassificação, preferências e transparência (02/11 a 08/11/2026) · RF-11, RF-12, RF-13, RF-15, RF-17

Correcção manual de categoria; definições; ecrã sobre tratamento de dados; apagar todos os dados.

**Estado:** pendente.

### Fase 7 — Robustez, acessibilidade e testes (09/11 a 15/11/2026) · RF-16, RNF-08, RNF-09, RNF-11, RNF-12

Tratamento de falhas, *minSdk*, acessibilidade, testes unitários e de UI.

**Estado:** pendente. O `minSdk` actual é 26 (Android 8.0).

### Fase 8 — Optimização, conformidade e fecho (16/11 a 20/11/2026) · RNF-04 e privacidade

R8/ProGuard, checklist de processamento local, *build* assinado, material para a monografia.

**Estado:** pendente. Esta pasta `docs/` é um primeiro suporte à tese, não o fecho da Fase 8.

## Notas do plano original que se mantêm

- Para demonstração académica, a aplicação corre em dispositivos de teste; não depende de lojas nem de servidores.
- O *dataset* de treino (Fase 4) deve permanecer separado das mensagens reais do telemóvel.
- Se o tempo apertar na Fase 4, um classificador clássico (TF-IDF + Naive Bayes ou regressão logística) é a opção alinhada com o tamanho do APK e com a RAM (RNF-04, RNF-05).
