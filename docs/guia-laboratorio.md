# Guia de laboratório

Instruções para reproduzir o protótipo num computador com Android SDK e num telemóvel de teste (por exemplo Samsung Galaxy A06). Destina-se a sessões de demonstração da monografia e a desenvolvimento local.

## Requisitos

- JDK 17 (ou o JBR do Android Studio)
- Android SDK com plataforma API 36
- `ANDROID_HOME` (por omissão nos *scripts*: `/home/esimango/Android/Sdk`)
- Telemóvel com depuração USB activa, ou emulador

## Compilar e instalar

Na raiz do projecto Android (`android-app`):

```bash
./build.sh && ./install-debug.sh
```

- `build.sh` executa `./gradlew assembleDebug` e indica o caminho do APK.
- `install-debug.sh` instala por ADB (`adb install -r`). Se o APK ainda não existir, compila-o.

APK gerado:

`app/build/outputs/apk/debug/app-debug.apk`

É um *build* de depuração, assinado com a chave de desenvolvimento. Não é a versão final da monografia (Fase 8).

## Se o ADB recusar o dispositivo

Mensagens `unauthorized` significam que o telemóvel ainda não autorizou este computador. Aceitar o diálogo de depuração USB no ecrã e voltar a correr `./install-debug.sh`.

## Se a instalação pelo ficheiro for bloqueada

O Play Protect recusa APKs com permissões de SMS vindos de fontes da Internet (WhatsApp, Drive, gestor de ficheiros). Para o TCC, instalar **sempre por USB** com o *script* acima.

Se a app instalar mas o interruptor de SMS estiver cinzento: Definições → Aplicações → Categorizador SMS → menu → **Permitir definições restritas**. Depois abrir a app e conceder `RECEIVE_SMS` e `READ_SMS`.

## Roteiro de teste (Fase 1)

1. Abrir **Categorizador SMS** e conceder permissões, se pedidas.
2. Verificar a grelha de categorias e as mensagens recentes.
3. Abrir uma categoria e uma conversa; voltar com **Voltar** ou o botão do sistema.
3b. Em Definições, alternar Claro / Escuro e confirmar que a escolha se mantém ao reabrir.
4. Enviar um SMS de teste para o número do dispositivo e confirmar que a conversa se actualiza (a app deve estar instalada e com permissão).
5. No Logcat, filtrar `SmsReceiver`: não devem aparecer o corpo nem o número da mensagem.
6. Fechar a app por completo e voltar a abrir: as conversas devem continuar (persistência local cifrada).
7. Numa conversa com número de telefone, responder. A bolha enviada fica à direita e o separador do dia mostra **Hoje**. Um remetente como `Vodacom` não tem campo de resposta.
8. Na lista de mensagens, tocar em **+**. Escrever um número com pelo menos 8 dígitos e enviar. Se esse número já tiver conversa, a mensagem entra nela. Com a permissão de contactos, o nome da pessoa substitui o número na lista e no título.
9. Conceder notificações, se pedidas. Enviar um SMS de teste com a aplicação fechada: aparece um aviso. No ecrã de bloqueio só diz «Nova mensagem». Desbloqueado, mostra o nome ou o número e, pouco depois, a categoria. O texto não aparece. Tocar no aviso abre essa conversa. Com a conversa já aberta, o aviso não aparece.

## Emulador

*Extended controls* → *Phone* → *Send SMS*. As permissões e o receptor comportam-se de forma análoga, sem o bloqueio do Play Protect associado ao *sideload* por ficheiro.
